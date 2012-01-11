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
package org.jwebsocket.eventmodel.event.em;

import org.jwebsocket.eventmodel.annotation.ImportFromToken;
import org.jwebsocket.eventmodel.event.C2SEvent;

/**
 * The s2c event response from the client
 * 
 * @author kyberneees
 */
public class S2CResponse extends C2SEvent {
	
	private String reqId;
	private Object response;

	/**
	 * @return The client response
	 */
	public Object getResponse() {
		return response;
	}

	/**
	 * @param response The client response
	 */
	public void setResponse(Object response) {
		this.response = response;
	}

	/**
	 * @return The request identifier
	 */
	public String getReqId() {
		return reqId;
	}

	/**
	 * @param reqId The request identifier
	 */
	@ImportFromToken(key = "_rid")
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	/**
	 * @return Time required by the client to process the event
	 */
	public double getProcessingTime() {
		return getArgs().getDouble("_pt");
	}
}
