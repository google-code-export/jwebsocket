//  ---------------------------------------------------------------------------
//  jWebSocket - EventsPlugIn
//  Copyright (c) 2010 Innotrade GmbH, jWebSocket.org
//  ---------------------------------------------------------------------------
//  This program is free software; you can redistribute it and/or modify it
//  under the terms of the GNU Lesser General Public License as published by the
//  Free Software Foundation; either version 3 of the License, or (at your
//  option) any later version.
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  You should have received a copy of the GNU Lesser General Public License along
//  with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//  ---------------------------------------------------------------------------
package org.jwebsocket.eventmodel.plugin;

import org.jwebsocket.eventmodel.s2c.S2CEventNotification;
import java.util.Collection;
import org.jwebsocket.eventmodel.observable.ObservableObject;
import org.jwebsocket.eventmodel.api.IEventModelPlugIn;
import org.jwebsocket.eventmodel.core.EventModel;
import org.jwebsocket.eventmodel.observable.Event;
import org.jwebsocket.eventmodel.observable.ResponseEvent;
import java.util.Map;
import java.util.Set;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.eventmodel.event.C2SEventDefinitionManager;
import org.jwebsocket.eventmodel.event.S2CEvent;
import org.jwebsocket.eventmodel.event.C2SEventDefinition;
import org.jwebsocket.eventmodel.s2c.S2CEventNotificationHandler;
import org.jwebsocket.spring.JWebSocketBeanFactory;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.jwebsocket.logging.Logging;
import org.apache.log4j.Logger;

/**
 *
 * @author kyberneees
 */
public abstract class EventModelPlugIn extends ObservableObject implements IEventModelPlugIn {

	private String id;
	private EventModel em;
	private Map<String, Class<? extends Event>> clientAPI;
	private static Logger mLog = Logging.getLogger(EventModelPlugIn.class);

	/**
	 * {@inheritDoc }
	 * 
	 * @throws Exception 
	 */
	@Override
	public void initialize() throws Exception {
	}

	/**
	 * Short-cut to set the plug-in events definitions
	 * 
	 * @param defs The plug-in events definitions
	 */
	public void setEventsDefinitions(Set<C2SEventDefinition> defs) {
		((C2SEventDefinitionManager) (JWebSocketBeanFactory.getInstance(getEm().getNamespace()).
				getBean("EventDefinitionManager"))).getSet().addAll(defs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processEvent(Event aEvent, ResponseEvent aResponseEvent) {
		System.out.println(">> Response from '" + this.getClass().getName() + "', please override this method!");
	}

	@Override
	public Map<String, WebSocketConnector> getServerAllConnectors() {
		return this.getEm().getParent().getServer().getAllConnectors();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Override
	public void shutdown() throws Exception {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public S2CEventNotification notifyS2CEvent(S2CEvent aEvent) {
		return new S2CEventNotification(this.getId(), aEvent,
				((S2CEventNotificationHandler) JWebSocketBeanFactory.getInstance(getEm().getNamespace()).
				getBean("S2CEventNotificationHandler")));
	}

	/**
	 * Register the events in the EventModel subject and the plug-in as a listener for them
	 *
	 * @param emEvents The events to register
	 * @throws Exception
	 */
	public void setEmEvents(Collection<Class<? extends Event>> emEvents) throws Exception {
		getEm().addEvents(emEvents);
		getEm().on(emEvents, this);
	}

	/**
	 * Event Model events registration and client API definition
	 *
	 * @param emEvents
	 * @throws Exception
	 */
	public void setEmEventsAndClientAPI(Map<String, Class<? extends Event>> emEvents) throws Exception {
		setClientAPI(emEvents);
		getEm().addEvents(emEvents.values());
		getEm().on(emEvents.values(), this);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public EventModel getEm() {
		return em;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void setEm(EventModel em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public Map<String, Class<? extends Event>> getClientAPI() {
		return clientAPI;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void setClientAPI(Map<String, Class<? extends Event>> clientAPI) {
		this.clientAPI = clientAPI;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public String toString() {
		return getId();
	}

	public Map<String, Object> getSession(WebSocketConnector aConnector) throws Exception {
		return aConnector.getSession().getStorage();
	}

	public Map<String, Object> getSession(String aSessionId) throws Exception {
		return getEm().getSessionFactory().getSession(aSessionId);
	}

	/**
	 * 
	 * {@inheritDoc } 
	 */
	@Override
	public void readFromToken(Token aToken) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * 
	 * {@inheritDoc } 
	 */
	@Override
	public void writeToToken(Token aToken) {
		Token lApi = TokenFactory.createToken();
		Token lTokenEventDef;
		C2SEventDefinition lEventDef = null;

		for (String lKey : getClientAPI().keySet()) {
			try {
				String aEventId = getEm().getEventFactory().eventToString(getClientAPI().get(lKey));
				lEventDef = getEm().getEventFactory().getEventDefinitions().getDefinition(aEventId);
				lTokenEventDef = TokenFactory.createToken();
				lEventDef.writeToToken(lTokenEventDef);
				lApi.setToken(lKey, lTokenEventDef);
			} catch (Exception ex) {
				mLog.debug(ex.getMessage(), ex);
			}
		}

		aToken.setString("id", getId());
		aToken.setToken("api", lApi);
	}
}
