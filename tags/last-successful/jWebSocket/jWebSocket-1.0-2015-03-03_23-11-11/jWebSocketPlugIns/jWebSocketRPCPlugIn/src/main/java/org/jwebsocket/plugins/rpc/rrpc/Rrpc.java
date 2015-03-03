//	---------------------------------------------------------------------------
//	jWebSocket - Rrpc (Community Edition, CE)
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
package org.jwebsocket.plugins.rpc.rrpc;

import java.util.List;
import javolution.util.FastList;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.plugins.rpc.AbstractRrpc;
import org.jwebsocket.plugins.rpc.CommonRpcPlugin;
import org.jwebsocket.plugins.rpc.RPCPlugIn;
import org.jwebsocket.security.SecurityFactory;
import org.jwebsocket.token.Token;

/**
 * Class used to call a Rrpc method (S2C) Example: new Rrpc.Call("aClass",
 * "aMethod").send("hello", "it's a rrpc call",
 * 123).from(aConnector).to(anotherConnector) or new Rrpc.Call("aClass",
 * "aMethod").send(SomethingToSend).to(anotherConnector) (in this case, the
 * sender will be the server)
 *
 * @author Quentin Ambard
 */
public class Rrpc extends AbstractRrpc {//implements RpcInterface, RpcInterfaceCaller, RpcInterfaceFromCaller{

	private WebSocketConnector mConnectorFrom = null;
	private List<WebSocketConnector> mConnectorsTo;

	/**
	 *
	 * @param aClassname
	 * @param aMethod
	 */
	public Rrpc(String aClassname, String aMethod) {
		super(aClassname, aMethod);
	}

	/**
	 *
	 * @param aClassname
	 * @param aMethod
	 * @param aSpawnTread
	 */
	public Rrpc(String aClassname, String aMethod, boolean aSpawnTread) {
		super(aClassname, aMethod, aSpawnTread);
	}

	/**
	 * The token should contains all the necessary informations. Can be usefull
	 * to create a direct call from an already-created token
	 *
	 * @param aToken
	 * @throws RrpcRightNotGrantedException
	 */
	public Rrpc(Token aToken) throws RrpcRightNotGrantedException {
		super(aToken);
		String lConnectorFromId = aToken.getString(CommonRpcPlugin.RRPC_KEY_SOURCE_ID);
		if (lConnectorFromId != null) {
			from(lConnectorFromId);
		}
	}

	/**
	 * Eventually, the connector the rrpc comes from. If this method is not
	 * called during the rrpc, the server will be the source.
	 *
	 * @param aConnector
	 * @return
	 * @throws RrpcRightNotGrantedException
	 */
	public Rrpc from(WebSocketConnector aConnector) throws RrpcRightNotGrantedException {
		mConnectorFrom = aConnector;
		//check if user is allowed to run 'rrpc' command		
		if (mConnectorFrom != null && !SecurityFactory.hasRight(RPCPlugIn.getUsernameStatic(mConnectorFrom), CommonRpcPlugin.NS_RPC_DEFAULT + "." + CommonRpcPlugin.RRPC_RIGHT_ID)) {
			throw new RrpcRightNotGrantedException();
		}
		return this;
	}

	/**
	 * Eventually, the connectorId the rrpc comes from. If this method is not
	 * called during the rrpc, the server will be the source.
	 *
	 * @param aConnectorId
	 * @return
	 * @throws RrpcRightNotGrantedException
	 */
	public Rrpc from(String aConnectorId) throws RrpcRightNotGrantedException {
		return from(RPCPlugIn.getConnector("tcp0", aConnectorId));
	}

	/**
	 * The connector you want to send the rrpc
	 *
	 * @param aConnector
	 * @return
	 */
	public Rrpc to(WebSocketConnector aConnector) {
		if (mConnectorsTo == null) {
			mConnectorsTo = new FastList<WebSocketConnector>();
		}
		mConnectorsTo.add(aConnector);
		return this;
	}

	/**
	 * The connectors you want to send the rrpc
	 *
	 * @param aConnectors
	 * @return
	 */
	public Rrpc toConnectors(List<WebSocketConnector> aConnectors) {
		mConnectorsTo = aConnectors;
		return this;
	}

	/**
	 * The connectorId you want to send the rrpc
	 *
	 * @return
	 */
	public Rrpc to(String aConnectorId) {
		WebSocketConnector lConnector = RPCPlugIn.getConnector("tcp0", aConnectorId);
		to(lConnector);
		return this;
	}

	/**
	 * The connectorsId you want to send the rrpc
	 *
	 * @param aConnectorsId
	 * @return
	 */
	public Rrpc to(List<String> aConnectorsId) {
		mConnectorsTo = new FastList<WebSocketConnector>();
		for (String lConnectorId : aConnectorsId) {
			WebSocketConnector lConnector = RPCPlugIn.getConnector("tcp0", lConnectorId);
			mConnectorsTo.add(lConnector);
		}
		return this;
	}

	public Token call() {
		Token lToken = super.call();
		String idConnectorFrom;
		if (mConnectorFrom == null) {
			idConnectorFrom = CommonRpcPlugin.SERVER_ID;
		} else {
			idConnectorFrom = mConnectorFrom.getId();
		}
		lToken.setString(CommonRpcPlugin.RRPC_KEY_SOURCE_ID, idConnectorFrom);

		RPCPlugIn.processRrpc(mConnectorFrom, mConnectorsTo, lToken);
		return lToken;
	}
//	/**
//	 * Interface used to make sure the user uses the instructions in the correct order.
//	 * only allows to perform a from or a send.
//	 * Used after a Call()
//	 */
//	@SuppressWarnings("rawtypes")
//	public interface RpcInterface  {
//		public RpcInterfaceFromCaller from (String aConnectorId) throws RrpcRightNotGrantedException;
//		public RpcInterfaceFromCaller from (WebSocketConnector aConnector)  throws RrpcRightNotGrantedException;
//		public RpcInterfaceCaller send (Object... aArg);
//		public RpcInterfaceCaller sendListOfArgs (List aArg);
//	}
//	
//	/**
//	 * Interface used to make sure the user uses the instructions in the correct order.
//	 * only allows to perform a to() or a from.
//	 * Used after a send ()
//	 */
//	public interface RpcInterfaceCaller  {
//		public RpcInterfaceFromCaller from (String aConnectorId) throws RrpcRightNotGrantedException;
//		public RpcInterfaceFromCaller from (WebSocketConnector aConnector)  throws RrpcRightNotGrantedException;
//		public void to (String aConnectorId) throws RrpcConnectorNotFoundException;
//		public void to (WebSocketConnector aConnector);
//	}
//	
//	/**
//	 * Interface used to make sure the user uses the instructions in the correct order.
//	 * Final instruction, used after a from(): only allows to perform a to()
//	 */
//	public interface RpcInterfaceFromCaller  {
//		public void to (String aConnectorId) throws RrpcConnectorNotFoundException;
//		public void to (WebSocketConnector aConnector);
//	}
}
