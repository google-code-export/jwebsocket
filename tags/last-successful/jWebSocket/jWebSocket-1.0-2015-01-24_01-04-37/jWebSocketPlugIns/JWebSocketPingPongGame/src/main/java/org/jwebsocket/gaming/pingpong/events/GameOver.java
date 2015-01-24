//	---------------------------------------------------------------------------
//	jWebSocket - GameOver (Community Edition, CE)
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
//	distributed under the Liciense is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//	---------------------------------------------------------------------------
package org.jwebsocket.gaming.pingpong.events;

import org.jwebsocket.gaming.pingpong.objects.PingpongMatch;

/**
 *
 * @author armando
 */
public class GameOver extends BaseEvent {

	boolean mBool;

	/**
	 *
	 * @param aPingpongMatch
	 * @param aBool
	 */
	public GameOver(PingpongMatch aPingpongMatch, boolean aBool) {
		super(aPingpongMatch);
		mBool = aBool;
	}

	/**
	 *
	 * @return
	 */
	public boolean getBoolean() {
		return mBool;
	}
}
