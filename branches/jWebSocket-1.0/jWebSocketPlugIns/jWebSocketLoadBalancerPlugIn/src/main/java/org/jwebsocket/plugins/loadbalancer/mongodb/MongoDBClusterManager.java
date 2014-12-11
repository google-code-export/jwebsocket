//	---------------------------------------------------------------------------
//	jWebSocket Load Balancer MongoDBClusterManager (Community Edition, CE)
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
package org.jwebsocket.plugins.loadbalancer.mongodb;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jwebsocket.plugins.loadbalancer.api.ICluster;
import org.jwebsocket.plugins.loadbalancer.api.IClusterEndPoint;
import org.jwebsocket.plugins.loadbalancer.api.IClusterManager;

/**
 *
 * @author Rolando Santamaria Maso
 */
public class MongoDBClusterManager implements IClusterManager {

	@Override
	public Iterator<ICluster> getClusters() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Map<String, Object>> getClustersInfo() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Map<String, String>> getStickyRoutes() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ICluster getClusterByAlias(String aAlias) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setBalancerAlgorithm(Integer aAlgorithm) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Integer getBalancerAlgorithm() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ICluster getClusterByNamespace(String aNS) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void updateCpuUsage(String aConnectorId, double aCpuUsage) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isNamespaceSupported(String aNS) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public IClusterEndPoint getOptimumServiceEndPoint(String aNS) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int removeConnectorEndPoints(String aConnectorId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}