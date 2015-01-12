//      ---------------------------------------------------------------------------
//	jWebSocket Load Balancer Filter (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//      Alexander Schulze, Germany (NRW)
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
package org.jwebsocket.plugins.loadbalancer;

import java.util.List;
import org.apache.log4j.Logger;
import org.jwebsocket.api.FilterConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.filter.TokenFilter;
import org.jwebsocket.kit.FilterResponse;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.packetProcessors.JSONProcessor;
import org.jwebsocket.plugins.loadbalancer.api.ICluster;
import org.jwebsocket.token.Token;
import org.springframework.util.Assert;

/**
 * Load balancer filter captures the packets and forwards them to the load balancer plug-in.
 *
 * @author Rolando Betancourt Toucet
 * @author Rolando Santamaria Maso
 */
public class LoadBalancerFilter extends TokenFilter {

	private static final Logger mLog = Logging.getLogger();
	/**
	 * Load balancer plug-in id.
	 */
	private final String mLoadBalancerPlugInId;
	/**
	 * Load balancer plug-in instance.
	 */
	private LoadBalancerPlugIn mLoadBalancerPlugIn;

	/**
	 *
	 * @param aConfiguration
	 */
	public LoadBalancerFilter(FilterConfiguration aConfiguration) {
		super(aConfiguration);
		mLoadBalancerPlugInId = getFilterConfiguration().getSettings().get("loadbalancer_plugin_id");
		Assert.notNull(mLoadBalancerPlugInId, "Missing LoadBalancerFilter 'loadbalancer_plugin_id' "
				+ "setting!");
	}

	/**
	 * Automatically redirects incoming tokens to the load balancer plug-in.
	 *
	 * @param aResponse
	 * @param aConnector
	 * @param aToken
	 */
	@Override
	public void processTokenIn(FilterResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lTargetId = aToken.getString("targetId");
		String lAction = aToken.getString("action");
		if ("forward.json".equals(lAction) && null != lTargetId) {
			if (mLoadBalancerPlugIn.isAliasSupported(lTargetId)) {
				String lJson = aToken.getString("data");
				if (null != lJson) {
					aToken = JSONProcessor.JSONStringToToken(lJson);
				}
			}
		}

		// supporting JMS-Gateway integration
		if ("org.jwebsocket.plugins.jms".equals(aToken.getNS())) {
			String lHostname, lCanonicalHostName, lIpAddress, lEndPointId, lConnectionId;

			lEndPointId = System.getProperty("org.jwebsocket.plugins.jms.gateway.endPointId");
			lConnectionId = System.getProperty("org.jwebsocket.plugins.jms.gateway.connectionId");
			lHostname = System.getProperty("org.jwebsocket.plugins.jms.gateway.hostname");
			lCanonicalHostName = System.getProperty("org.jwebsocket.plugins.jms.gateway.canonicalHostname");
			lIpAddress = System.getProperty("org.jwebsocket.plugins.jms.gateway.ipAddress");

			if ("ping".equals(aToken.getType())) {
				if (null != lTargetId) {
					String lData = "{\"ns\":\"org.jwebsocket.jms.gateway\""
							+ ",\"type\":\"response\",\"reqType\":\"ping\""
							+ ",\"code\":0,\"msg\":\"pong\",\"utid\":" + aToken.getInteger("utid")
							+ ",\"sourceId\":\"${endpoint}\""
							+ ",\"isVirtual\":\"" + true + "\""
							+ (null != lHostname ? ",\"hostname\":\"" + lHostname + "\"" : "")
							+ (null != lCanonicalHostName ? ",\"canonicalHostname\":\"" + lCanonicalHostName + "\"" : "")
							+ (null != lIpAddress ? ",\"ip\":\"" + lIpAddress + "\"" : "")
							+ ",\"connectionId\":\"" + lConnectionId + "\""
							+ (null != lEndPointId ? ",\"gatewayId\":\"" + lEndPointId + "\"" : "")
							+ "}";
					if (mLoadBalancerPlugIn.isEndPointAvailable(lTargetId)) {
						aConnector.sendPacket(new RawPacket(lData.replace("${endpoint}", lTargetId)));
						aResponse.rejectMessage();
						return;
					}
				}
			} else if ("identify".equals(aToken.getType())) {
				if (null != lTargetId) {
					String lData = "{\"ns\":\"org.jwebsocket.jms.gateway\""
							+ ",\"type\":\"response\",\"reqType\":\"identify\""
							+ ",\"code\":0,\"msg\":\"ok\",\"utid\":" + aToken.getInteger("utid")
							+ ",\"sourceId\":\"${endpoint}\""
							+ ",\"isVirtual\":\"" + true + "\""
							+ (null != lHostname ? ",\"hostname\":\"" + lHostname + "\"" : "")
							+ (null != lCanonicalHostName ? ",\"canonicalHostname\":\"" + lCanonicalHostName + "\"" : "")
							+ (null != lIpAddress ? ",\"ip\":\"" + lIpAddress + "\"" : "")
							+ ",\"connectionId\":\"" + lConnectionId + "\""
							+ (null != lEndPointId ? ",\"gatewayId\":\"" + lEndPointId + "\"" : "")
							+ "}";
					if ("*".equals(lTargetId)) {
						List<String> lAliases = mLoadBalancerPlugIn.getAvailableClusters();
						for (String lAlias : lAliases) {
							aConnector.sendPacket(new RawPacket(lData.replace("${endpoint}", lAlias)));
						}
					} else if (mLoadBalancerPlugIn.isEndPointAvailable(lTargetId)) {

						aConnector.sendPacket(new RawPacket(lData));
						aResponse.rejectMessage();

						return;
					}
				}
			}
		}

		ICluster lCluster = mLoadBalancerPlugIn.getClusterByNamespace(aToken.getNS());
		if (null != lCluster) {
			// redirect token
			if (null != aToken.getInteger("utid", null)) {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Redirecting incoming token to the load balancer plug-in...");
				}
				mLoadBalancerPlugIn.sendToService(aConnector, aToken, lCluster);

				// stop token propagation
				aResponse.rejectMessage();
			}
		}
	}

	@Override
	public void systemStarted() throws Exception {
		mLoadBalancerPlugIn = (LoadBalancerPlugIn) getServer().getPlugInById(mLoadBalancerPlugInId);
		Assert.notNull(mLoadBalancerPlugIn, "Unable to start the LoadBalancerFilter because "
				+ "the LoadBalancer plug-in '" + mLoadBalancerPlugInId + "' was not found!");

		if (mLog.isDebugEnabled()) {
			mLog.debug("Filter started successfully!");
		}
	}
}
