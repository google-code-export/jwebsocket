//	---------------------------------------------------------------------------
//	jWebSocket - ChannelException (Community Edition, CE)
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
package org.jwebsocket.plugins.channels;

/**
 * Channel related exception class
 *
 * @author puran
 * @version $Id:$
 */
public class ChannelException extends Exception {

	/**
	 *
	 * @param error
	 */
	public ChannelException(String error) {
		super(error);
	}

	/**
	 *
	 * @param error
	 */
	public ChannelException(Throwable error) {
		super(error);
	}

	/**
	 *
	 * @param error
	 * @param throwable
	 */
	public ChannelException(String error, Throwable throwable) {
		super(error, throwable);
	}
	private static final long serialVersionUID = 1L;
}
