//	---------------------------------------------------------------------------
//	jWebSocket - NioTcpEngine (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//	Alexander Schulze, Germany (NRW)
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//	---------------------------------------------------------------------------
package org.jwebsocket.tcp.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketConnectorStatus;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.engines.BaseEngine;
import org.jwebsocket.instance.JWebSocketInstance;
import org.jwebsocket.kit.*;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.tcp.EngineUtils;
import org.jwebsocket.tcp.nio.ssl.IDelayedSSLPacketNotifier;
import org.jwebsocket.tcp.nio.ssl.NioSSLHandler;

/**
 * <p>
 * Tcp engine that uses java non-blocking io api to bind to listening port and
 * handle incoming/outgoing packets. There's one 'selector' thread that is
 * responsible only for handling socket operations. Therefore, every packet that
 * should be sent will be firstly queued into concurrent queue, which is
 * continuously processed by selector thread. Since the queue is concurrent,
 * there's no blocking and a call to send method will return immediately. </p>
 * <p>
 * All packets that are received from remote clients are processed in separate
 * worker threads. This way it's possible to handle many clients simultaneously
 * with just a few threads. Add more worker threads to handle more clients. </p>
 * <p>
 * Before making any changes to this source, note this: it is highly advisable
 * to read from (or write to) a socket only in selector thread. Ignoring this
 * advice may result in strange consequences (threads locking or spinning,
 * depending on actual scenario). </p>
 *
 * @author jang
 * @author Rolando Santamaria Maso
 */
public class NioTcpEngine extends BaseEngine {

	private static final Logger mLog = Logging.getLogger();
	/**
	 *
	 */
	public static final String NUM_WORKERS_CONFIG_KEY = "workers";
	/**
	 *
	 */
	public static final int DEFAULT_NUM_WORKERS = Runtime.getRuntime().availableProcessors();
	private Selector mPlainSelector;
	private Selector mSSLSelector;
	private ServerSocketChannel mPlainServer;
	private ServerSocketChannel mSSLServer;
	private boolean mIsRunning;
	private Map<String, Queue<DataFuture>> mPendingWrites; // <connector id, data queue>
	private ExecutorService mExecutorService;
	private Map<String, SocketChannel> mConnectorToChannelMap; // <connector id, socket channel>
	private Map<SocketChannel, String> mChannelToConnectorMap; // <socket channel, connector id>
	private ByteBuffer mReadBuffer;
	private Thread mPlainSelectorThread;
	private Thread mSSLSelectorThread;
	private final DelayedPacketsQueue mDelayedPacketsQueue = new DelayedPacketsQueue();
	private SSLContext mSSLContext;
	private int mNumWorkers = DEFAULT_NUM_WORKERS;
	private Map<String, NioTcpConnector> mBeforeHandshareConnectors = new FastMap<String, NioTcpConnector>().shared();
	private boolean mTcpNoDelay = DEFAULT_TCP_NODELAY;
	/**
	 *
	 */
	public static Boolean DEFAULT_TCP_NODELAY = true;
	/**
	 *
	 */
	public static String TCP_NODELAY_CONFIG_KEY = "tcpNoDelay";

	/**
	 *
	 * @return
	 */
	public int getWorkers() {
		return mNumWorkers;
	}

	/**
	 *
	 * @param aConfiguration
	 */
	public NioTcpEngine(EngineConfiguration aConfiguration) {
		super(aConfiguration);

		if (getConfiguration().getSettings().containsKey(TCP_NODELAY_CONFIG_KEY)) {
			if (mLog.isDebugEnabled()) {
				mLog.debug("Setting '" + TCP_NODELAY_CONFIG_KEY + "' configuration "
						+ "from engine configuration...");
			}
			try {
				mTcpNoDelay = Boolean.parseBoolean(getConfiguration().
						getSettings().
						get(TCP_NODELAY_CONFIG_KEY).
						toString());
			} catch (Exception lEx) {
				mLog.error(Logging.getSimpleExceptionMessage(lEx,
						"setting '" + TCP_NODELAY_CONFIG_KEY + "' configuration"));
			}
		}
	}

	@Override
	public void startEngine() throws WebSocketException {
		try {
			mPendingWrites = new ConcurrentHashMap<String, Queue<DataFuture>>();
			mConnectorToChannelMap = new ConcurrentHashMap<String, SocketChannel>();
			mChannelToConnectorMap = new ConcurrentHashMap<SocketChannel, String>();
			mReadBuffer = ByteBuffer.allocate(getConfiguration().getMaxFramesize());
			mPlainSelector = SelectorProvider.provider().openSelector();
			mSSLSelector = SelectorProvider.provider().openSelector();

			mPlainServer = Util.createServerSocketChannel(getConfiguration().getPort(),
					getConfiguration().getHostname());
			mPlainServer.register(mPlainSelector, SelectionKey.OP_ACCEPT);
			if (mLog.isDebugEnabled()) {
				mLog.debug("Non-SSL server running at port: " + getConfiguration().getPort() + "...");
			}

			// creating the SSL server only if required
			if (getConfiguration().getSSLPort() > 0) {
				mSSLContext = Util.createSSLContext(getConfiguration().getKeyStore(),
						getConfiguration().getKeyStorePassword());
				if (mLog.isDebugEnabled()) {
					mLog.debug("SSLContext created with key-store: " + getConfiguration().getKeyStore() + "...");
				}
				mSSLServer = Util.createServerSocketChannel(getConfiguration().getSSLPort(),
						getConfiguration().getHostname());
				mSSLServer.register(mSSLSelector, SelectionKey.OP_ACCEPT);
				if (mLog.isDebugEnabled()) {
					mLog.debug("SSL server running at port: " + getConfiguration().getSSLPort() + "...");
				}
			}

			mIsRunning = true;

			// start worker threads
			if (getConfiguration().getSettings().containsKey(NUM_WORKERS_CONFIG_KEY)) {
				mNumWorkers = Integer.parseInt(getConfiguration().
						getSettings().
						get(NUM_WORKERS_CONFIG_KEY).
						toString());
			}
			mExecutorService = Executors.newFixedThreadPool(mNumWorkers);
			for (int lIdx = 0; lIdx < mNumWorkers; lIdx++) {
				// give an index to each worker thread
				mExecutorService.submit(new ReadWorker(lIdx));
			}

			// start plain selector thread
			mPlainSelectorThread = new Thread(new SelectorThread(mPlainSelector));
			mPlainSelectorThread.start();

			if (getConfiguration().getSSLPort() > 0) {
				// start SSL selector thread
				mSSLSelectorThread = new Thread(new SelectorThread(mSSLSelector));
				mSSLSelectorThread.start();
			}

			if (mLog.isDebugEnabled()) {
				mLog.debug("NioTcpEngine started successfully with '" + mNumWorkers + "' workers!");
			}
		} catch (Exception e) {
			throw new WebSocketException(e.getMessage(), e);
		}
	}

	@Override
	public void stopEngine(CloseReason aCloseReason) throws WebSocketException {
		super.stopEngine(aCloseReason);
		if (mPlainSelector != null) {
			try {
				mIsRunning = false;

				mPlainSelectorThread.join();
				mPlainSelector.wakeup();
				mPlainServer.close();
				mPlainSelector.close();

				mSSLSelectorThread.join();
				mSSLSelector.wakeup();
				mSSLServer.close();
				mSSLSelector.close();

				mPendingWrites.clear();
				mExecutorService.shutdown();
				mLog.info("NIO engine stopped.");
			} catch (InterruptedException lEx) {
				throw new WebSocketException(lEx.getMessage(), lEx);
			} catch (IOException lEx) {
				throw new WebSocketException(lEx.getMessage(), lEx);
			}
		}
	}

	/**
	 *
	 * @param aConnectorId
	 * @param aFuture
	 */
	public void send(String aConnectorId, DataFuture aFuture) {
		try {
			if (mPendingWrites.containsKey(aConnectorId)) {
				NioTcpConnector lConnector = getConnector(aConnectorId);
				if (lConnector.isSSL()) {
					lConnector.getSSLHandler().send(aFuture.getData());
				} else {
					mPendingWrites.get(aConnectorId).add(aFuture);
					mPlainSelector.wakeup();
				}
			} else {
				aFuture.setFailure(new Exception("Discarding packet for unattached socket channel..."));
			}
		} catch (Exception lEx) {
			if (mLog.isDebugEnabled()) {
				mLog.debug("Data could not be sent!", lEx);
			}
			aFuture.setFailure(lEx);
		}
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (mConnectorToChannelMap.containsKey(aConnector.getId())) {
			mPendingWrites.remove(aConnector.getId());
			SocketChannel lChannel = mConnectorToChannelMap.remove(aConnector.getId());
			mChannelToConnectorMap.remove(lChannel);

			try {
				lChannel.close();
				lChannel.socket().close();
			} catch (IOException lEx) {
				//Ignore it. Channel has been closed previously!
			}

			if (mDelayedPacketsQueue.getDelayedPackets().containsKey((NioTcpConnector) aConnector)) {
				mDelayedPacketsQueue.getDelayedPackets().remove((NioTcpConnector) aConnector);
			}
		}

		if (((NioTcpConnector) aConnector).isAfterWSHandshake()) {
			super.connectorStopped(aConnector, aCloseReason);
		}
	}

	/**
	 * Socket operations are permitted only via this thread. Strange behavior
	 * will occur if anything is done to the socket outside of this thread.
	 */
	private class SelectorThread implements Runnable {

		Selector mSelector;

		public SelectorThread(Selector aSelector) {
			this.mSelector = aSelector;
		}

		@Override
		public void run() {
			Thread.currentThread().setName("jWebSocket NIO-Engine SelectorThread");

			if (mSelector == mPlainSelector) {
				engineStarted();
			}

			while (mIsRunning && mSelector.isOpen()) {
				boolean lWrite = false;
				for (String lConnectorId : mPendingWrites.keySet()) {
					try {
						SelectionKey lKey = mConnectorToChannelMap.get(lConnectorId).keyFor(mSelector);
						if (null != lKey && !mPendingWrites.get(lConnectorId).isEmpty()) {
							lKey.interestOps(SelectionKey.OP_WRITE);
							lWrite = true;
						}
					} catch (Exception lEx) {
						// just ignore it. client disconnect too fast
					}
				}
				if (lWrite) {
					mSelector.wakeup();
				}

				try {
					// Waits for 500ms for any data from connected clients or for new client connections.
					// We could have indefinite wait (selector.wait()), but it is good to check for 'running' variable
					// fairly often.
					if (mSelector.select(500) > 0 && mIsRunning) {
						Iterator<SelectionKey> lKeys = mSelector.selectedKeys().iterator();
						while (lKeys.hasNext()) {
							SelectionKey lKey = lKeys.next();
							lKeys.remove();
							try {
								if (lKey.isAcceptable()) {
									accept(lKey, mSelector);
								} else if (lKey.isReadable()) {
									read(lKey);
								} else if (lKey.isValid() && lKey.isWritable()) {
									write(lKey, mSelector);
								}
							} catch (CancelledKeyException lEx) {
								// ignore, key was cancelled an instant after isValid() returned true,
								// most probably the client disconnected just at the wrong moment
							}
						}
					}
				} catch (IOException lEx) {
					// something happened during socket operation (select, read or write), just log it
					mLog.error(Logging.getSimpleExceptionMessage(lEx, "Error during socket operation"));
				}
			}

			if (mSelector == mPlainSelector) {
				engineStopped();
			}
		}
	}

	private void write(SelectionKey aKey, Selector aSelector) throws IOException {
		SocketChannel lSocketChannel = (SocketChannel) aKey.channel();
		Queue<DataFuture> lQueue = null;
		try {
			lQueue = mPendingWrites.get(mChannelToConnectorMap.get(lSocketChannel));
		} catch (Exception lEx) {
			// the client was disconnected previously. ignore it
		}
		if (null == lQueue) {
			return; // client disconnected
		}

		if (!lQueue.isEmpty()) {
			DataFuture future = lQueue.element();
			try {
				ByteBuffer lData = future.getData();
				lSocketChannel.write(lData);
				if (lData.remaining() > 0) {
					// socket's buffer is full, stop writing for now and leave the remaining
					// data in queue for another round of writing
					return;
				} else {
					lQueue.remove();
					future.setSuccess();
				}
			} catch (IOException lIOEx) {
				future.setFailure(lIOEx);
				// don't throw exception here
				// pending close packets are maybe in reading queue
				// some connectors could be not stopped yet
			}
		}
		aKey.interestOps(SelectionKey.OP_READ);
		if (!lQueue.isEmpty()) {
			aSelector.wakeup();
		}
	}

	private void accept(SelectionKey aKey, Selector aSelector) throws IOException {
		try {
			// closing if server is not ready
			if (JWebSocketInstance.STARTED != JWebSocketInstance.getStatus()) {
				aKey.channel().close();
				aKey.cancel();
				return;
			}

			if (getConnectors().size() == getConfiguration().getMaxConnections()
					&& getConfiguration().getOnMaxConnectionStrategy().equals("close")) {
				aKey.channel().close();
				aKey.cancel();
				mLog.info("NIO client (" + ((ServerSocketChannel) aKey.channel()).socket().getInetAddress()
						+ ") not accepted due to max connections reached. Connection closed!");
			} else {
				SocketChannel lSocketChannel = ((ServerSocketChannel) aKey.channel()).accept();
				lSocketChannel.socket().setTcpNoDelay(mTcpNoDelay);
				lSocketChannel.configureBlocking(false);
				// restricting connection handshake timeout
				lSocketChannel.socket().setSoTimeout(10 * 1000);
				lSocketChannel.register(aSelector, SelectionKey.OP_READ);

				int lSocketPort = lSocketChannel.socket().getPort();
				int lServerPort = lSocketChannel.socket().getLocalPort();

				NioTcpConnector lConnector = new NioTcpConnector(
						this, lSocketChannel.socket().getInetAddress(),
						lSocketPort);

				mBeforeHandshareConnectors.put(lConnector.getId(), lConnector);
				mPendingWrites.put(lConnector.getId(), new ConcurrentLinkedQueue<DataFuture>());
				mConnectorToChannelMap.put(lConnector.getId(), lSocketChannel);
				mChannelToConnectorMap.put(lSocketChannel, lConnector.getId());

				// initialize the SSLHandler
				if (lServerPort == getConfiguration().getSSLPort()) {
					lConnector.setSSL(true);
					SSLEngine lEngine = mSSLContext.createSSLEngine();
					lEngine.setUseClientMode(false);
					lEngine.beginHandshake();

					lConnector.setSSLHandler(new NioSSLHandler(lConnector,
							mPendingWrites,
							mDelayedPacketsQueue,
							mSSLSelector,
							lEngine,
							getConfiguration().getMaxFramesize()));
				}

				if (mLog.isInfoEnabled()) {
					mLog.info("NIO" + ((lConnector.isSSL()) ? "(SSL)" : "(plain)")
							+ " client '" + lConnector.getId() + "' started!");
				}
			}
		} catch (IOException e) {
			mLog.warn("Could not start new client connection!");
			throw e;
		}
	}

	private void read(SelectionKey aKey) throws IOException {
		SocketChannel lSocketChannel = (SocketChannel) aKey.channel();
		mReadBuffer.clear();

		int lNumRead;
		try {
			lNumRead = lSocketChannel.read(mReadBuffer);
		} catch (IOException lIOEx) {
			// remote client probably disconnected uncleanly ?
			clientDisconnect(aKey);
			return;
		}
		if (lNumRead == -1) {
			// read channel closed, connection has ended
			clientDisconnect(aKey);
			return;
		}
		if (lNumRead > 0 && mChannelToConnectorMap.containsKey(lSocketChannel)) {
			String lConnectorId = mChannelToConnectorMap.get(lSocketChannel);
			final ReadBean lBean = new ReadBean(lConnectorId, Arrays.copyOf(mReadBuffer.array(), lNumRead));
			final NioTcpConnector lConnector = getConnector(lBean.getConnectorId());

			if (lSocketChannel.socket().getLocalPort() == getConfiguration().getSSLPort()) {
				mDelayedPacketsQueue.addDelayedPacket(new IDelayedSSLPacketNotifier() {
					@Override
					public NioTcpConnector getConnector() {
						return lConnector;
					}

					@Override
					public ReadBean getBean() {
						return lBean;
					}
				});
			} else {
				mDelayedPacketsQueue.addDelayedPacket(new IDelayedPacketNotifier() {
					@Override
					public NioTcpConnector getConnector() {
						return lConnector;
					}

					@Override
					public ReadBean getBean() {
						return lBean;
					}
				});
			}
		}
	}

	private NioTcpConnector getConnector(String aId) {
		if (mBeforeHandshareConnectors.containsKey(aId)) {
			return mBeforeHandshareConnectors.get(aId);
		} else {
			return (NioTcpConnector) getConnectors().get(aId);
		}
	}

	private void clientDisconnect(SelectionKey aKey) throws IOException {
		clientDisconnect(aKey, CloseReason.CLIENT);
	}

	private void clientDisconnect(SelectionKey aKey, CloseReason aReason) throws IOException {
		SocketChannel lChannel = (SocketChannel) aKey.channel();
		if (mChannelToConnectorMap.containsKey(lChannel)) {
			try {
				aKey.cancel();
				lChannel.socket().close();
				lChannel.close();
			} catch (IOException lEx) {
			}

			String lId = mChannelToConnectorMap.remove(lChannel);
			mPendingWrites.remove(lId);
			mConnectorToChannelMap.remove(lId);

			WebSocketConnector lConnector = getConnector(lId);
			if (mDelayedPacketsQueue.getDelayedPackets().containsKey((NioTcpConnector) lConnector)) {
				mDelayedPacketsQueue.getDelayedPackets().remove((NioTcpConnector) lConnector);
			}

			lConnector.setStatus(WebSocketConnectorStatus.DOWN);
			lConnector.stopConnector(aReason);

			if (mLog.isInfoEnabled()) {
				mLog.info("NIO" + ((lConnector.isSSL()) ? "(SSL)" : "(plain)")
						+ " client '" + lConnector.getId() + "' stopped!");
			}
		}
	}

	private void clientDisconnect(WebSocketConnector aConnector) throws IOException {
		clientDisconnect(aConnector, CloseReason.CLIENT);
	}

	private void clientDisconnect(WebSocketConnector aConnector,
			CloseReason aReason) throws IOException {
		if (mConnectorToChannelMap.containsKey(aConnector.getId())) {
			Selector lSelector = (aConnector.isSSL()) ? mSSLSelector : mPlainSelector;
			clientDisconnect(mConnectorToChannelMap.get(aConnector.getId()).keyFor(lSelector), aReason);
		}
	}

	private class ReadWorker implements Runnable {

		int mId = -1;

		public ReadWorker(int aId) {
			super();
			mId = aId;
		}

		@Override
		public void run() {
			Thread.currentThread().setName("jWebSocket NIO-Engine ReadWorker " + this.mId);
			while (mIsRunning) {
				try {
					IDelayedPacketNotifier lDelayedPacket;
					lDelayedPacket = mDelayedPacketsQueue.take();
					NioTcpConnector lConnector = lDelayedPacket.getConnector();

					// processing SSL packets
					if (lDelayedPacket instanceof IDelayedSSLPacketNotifier) {
						lConnector.getSSLHandler().
								processSSLPacket(ByteBuffer.wrap(lDelayedPacket.getBean().getData()));
					} else {
						// processing plain packets
						doRead(lConnector, lDelayedPacket.getBean());
					}

					// release the connector for future reads
					lConnector.releaseWorker();
				} catch (InterruptedException lEx) {
					// uncaught exception during packet processing - kill the worker (todo: think about worker restart)
					mLog.error("Unexpected InterruptedException during incoming packet processing", lEx);
					break;
				} catch (IOException lEx) {
					// uncaught exception during packet processing - kill the worker (todo: think about worker restart)
					mLog.error("Unexpected IOException during incoming packet processing", lEx);
					break;
				}
			}
		}

		private void doRead(NioTcpConnector aConnector, ReadBean aBean) throws IOException {
			if (aConnector.isAfterWSHandshake()) {
				boolean lIsHixie = aConnector.isHixie();
				if (lIsHixie) {
					readHixie(new ByteArrayInputStream(aBean.getData()), aConnector);
				} else {
					readHybi(aConnector.getVersion(), new ByteArrayInputStream(aBean.getData()), aConnector);
				}
			} else {
				// checking if "max connnections" value has been reached
				if (getConnectors().size() > getConfiguration().getMaxConnections()) {
					if (getConfiguration().getOnMaxConnectionStrategy().equals("reject")) {
						if (mLog.isDebugEnabled()) {
							mLog.debug("NIO client not accepted due to max connections reached."
									+ " Connection rejected!");
						}
						clientDisconnect(aConnector, CloseReason.SERVER_REJECT_CONNECTION);
					} else {
						if (mLog.isDebugEnabled()) {
							mLog.debug("NIO client not accepted due to max connections reached."
									+ " Connection redirected!");
						}
						clientDisconnect(aConnector, CloseReason.SERVER_REDIRECT_CONNECTION);
					}
				} else {
					if (mLog.isDebugEnabled()) {
						mLog.debug("Parsing handshake request: " + new String(aBean.getData()).replace("\r\n", "\\n"));
					}

					Map<String, Object> lReqMap = WebSocketHandshake.parseC2SRequest(aBean.getData(), false);
					if (null == lReqMap) {
						mLog.error("Client not accepted on port " + aConnector.getRemotePort()
								+ " due to handshake issues. "
								+ "Probably the client supported WebSocket protocol is not updated!");
						// disconnect the client
						clientDisconnect(aConnector);
					}
					EngineUtils.parseCookies(lReqMap);

					Socket lSocket = mConnectorToChannelMap.get(aBean.getConnectorId()).socket();
					
					RequestHeader lReqHeader = EngineUtils.validateC2SRequest(
							getConfiguration().getDomains(), lReqMap, mLog, 
							lSocket);

					byte[] lResponse = WebSocketHandshake.generateS2CResponse(lReqMap, lReqHeader);

					if (lResponse == null || lReqHeader == null) {
						mLog.error("Client not accepted on port "
								+ aConnector.getRemotePort()
								+ " due to handshake issues. "
								+ "Probably the client supported WebSocket protocol is not updated, "
								+ "the SSL handshake could not be established or "
								+ "the connection has been closed unexpectedly!");
						// disconnect the client
						clientDisconnect(aConnector);
					}

					send(aConnector.getId(), new DataFuture(aConnector, ByteBuffer.wrap(lResponse)));

					if (mLog.isDebugEnabled()) {
						mLog.debug("Flushing handshake response: " + new String(lResponse).replace("\r\n", "\\n"));
					}

					int lTimeout = lReqHeader.getTimeout(getConfiguration().getTimeout());
					// mConnectorToChannelMap.get(aBean.getConnectorId()).socket().setSoTimeout(lTimeout);
					lSocket.setSoTimeout(lTimeout);
					
					// initializing connector
					aConnector.wsHandshakeValidated();
					aConnector.setHeader(lReqHeader);
					aConnector.setStatus(WebSocketConnectorStatus.UP);
					
					// setting the session identifier
					aConnector.getSession().setSessionId(aConnector.getHeader().
							getCookies().get(aConnector.getHeader().getSessionCookieName()).toString());
					
					// registering the connector
					getConnectors().put(aConnector.getId(), aConnector);
					// removing from the temporal map
					mBeforeHandshareConnectors.remove(aConnector.getId());
					// finally starting the connector
					aConnector.startConnector();
				}
			}
		}
	}

	private void readHybi(int aVersion, ByteArrayInputStream aIS, NioTcpConnector aConnector) throws IOException {
		try {
			WebSocketPacket lRawPacket;
			lRawPacket = WebSocketProtocolAbstraction.protocolToRawPacket(aVersion, aIS);

			if (lRawPacket.getFrameType() == WebSocketFrameType.PING) {
				// As per spec, server must respond to PING with PONG (maybe
				// this should be handled higher up in the hierarchy?)
				WebSocketPacket lPong = new RawPacket(lRawPacket.getByteArray());
				lPong.setFrameType(WebSocketFrameType.PONG);
				aConnector.sendPacket(lPong);
			} else if (lRawPacket.getFrameType() == WebSocketFrameType.CLOSE) {
				// As per spec, server must respond to CLOSE with acknowledgment CLOSE (maybe
				// this should be handled higher up in the hierarchy?)
				WebSocketPacket lClose = new RawPacket(lRawPacket.getByteArray());
				lClose.setFrameType(WebSocketFrameType.CLOSE);
				aConnector.sendPacket(lClose);
				clientDisconnect(aConnector, CloseReason.CLIENT);
			} else if (lRawPacket.getFrameType() == WebSocketFrameType.TEXT) {
				aConnector.flushPacket(lRawPacket);
			} else if (lRawPacket.getFrameType() == WebSocketFrameType.INVALID) {
				mLog.debug(getClass().getSimpleName() + ": Discarding invalid incoming packet... ");
			} else if (lRawPacket.getFrameType() == WebSocketFrameType.FRAGMENT
					|| lRawPacket.getFrameType() == WebSocketFrameType.BINARY) {
				mLog.debug(getClass().getSimpleName() + ": Discarding unsupported ('"
						+ lRawPacket.getFrameType().toString() + "') incoming packet... ");
			}

			// read more pending packets in the buffer (for high concurrency scenarios)
			// DO NOT REMOVE 
			if (aIS.available() > 0) {
				readHybi(aVersion, aIS, aConnector);
			}
		} catch (Exception e) {
			mLog.error("(other) " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
			clientDisconnect(aConnector, CloseReason.SERVER);
		}
	}

	private void readHixie(ByteArrayInputStream aIS, NioTcpConnector lConnector) throws IOException {
		ByteArrayOutputStream lBuff = new ByteArrayOutputStream();

		while (true) {
			try {
				int lByte = WebSocketProtocolAbstraction.read(aIS);
				// start of frame
				if (lByte == 0x00) {
					lBuff.reset();
					// end of frame
				} else if (lByte == 0xFF) {
					RawPacket lPacket = new RawPacket(lBuff.toByteArray());
					try {
						lConnector.flushPacket(lPacket);
						// read more pending packets in the buffer (for high concurrency scenarios)
						// DO NOT REMOVE
						if (aIS.available() > 0) {
							readHixie(aIS, lConnector);
						}
					} catch (IOException lEx) {
						mLog.error(lEx.getClass().getSimpleName()
								+ " in processPacket of connector "
								+ lConnector.getClass().getSimpleName()
								+ ": " + lEx.getMessage());
					}
					break;
				} else {
					lBuff.write(lByte);
				}
			} catch (Exception lEx) {
				mLog.error("Error while processing incoming packet", lEx);
				clientDisconnect(lConnector, CloseReason.SERVER);
				break;
			}
		}
	}
}
