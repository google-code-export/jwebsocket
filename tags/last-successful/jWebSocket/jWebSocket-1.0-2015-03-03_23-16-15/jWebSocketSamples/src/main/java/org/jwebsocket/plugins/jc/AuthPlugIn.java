//	---------------------------------------------------------------------------
//	jWebSocket - AuthPlugIn (Community Edition, CE)
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
package org.jwebsocket.plugins.jc;

import java.util.Map;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.eventmodel.event.C2SResponseEvent;
import org.jwebsocket.eventmodel.exception.InvalidConnectorIdentifier;
import org.jwebsocket.eventmodel.plugin.jc.JcPlugIn;
import org.jwebsocket.eventmodel.plugin.jc.JcResponseCallback;
import org.jwebsocket.eventmodel.s2c.FailureReason;
import org.jwebsocket.eventmodel.s2c.TransactionContext;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.jc.commands.*;
import org.jwebsocket.plugins.jc.event.DoLogin;
import org.jwebsocket.plugins.jc.event.GetUserInfo;
import org.jwebsocket.util.Tools;

/**
 *
 * @author Rolando Santamaria Maso
 */
public class AuthPlugIn extends JcPlugIn {

	private static Logger mLog = Logging.getLogger(AuthPlugIn.class);
	private byte[] mAppName = new byte[]{(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
		(byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x52, (byte) 0x41, (byte) 0x44, (byte) 0x42};
	private byte[] mPIN = Tools.hexStringToByteArray("0000");
	private byte[] mSiteName = "jwebsocket.com".getBytes();
	private StateMachine mState;

	/**
	 *
	 * @param aEvent
	 * @param aResponseEvent
	 * @throws Exception
	 */
	public void processEvent(GetUserInfo aEvent, C2SResponseEvent aResponseEvent) throws Exception {
		String lUsername = aEvent.getConnector().getUsername();
		if (lUsername.equals("alex")) {
			aResponseEvent.getArgs().setString("firstname", "Alexander");
			aResponseEvent.getArgs().setString("secondname", "Schulze");
			aResponseEvent.getArgs().setString("address", "Reinhard-Neumann-Strabe 12 48149 Munster");
		} else if (lUsername.equals("marta")) {
			aResponseEvent.getArgs().setString("firstname", "Marta");
			aResponseEvent.getArgs().setString("secondname", "Rodriguez Freire");
			aResponseEvent.getArgs().setString("address", "26 Street, 2565, C. Habana Cuba");
		}
	}

	/**
	 *
	 * @param aEvent
	 * @param aResponseEvent
	 * @throws Exception
	 */
	public void processEvent(DoLogin aEvent, C2SResponseEvent aResponseEvent) throws Exception {
		mState = StateMachine.READY;
		String lClient = aEvent.getConnector().getId();
		transmit(lClient, getTerminals(lClient).toArray()[0].toString(), new CommandAPDU(new Select(mAppName).getBytes()), new JcResponseCallback(new TransactionContext(getEm(), aEvent, null)) {
			private String mUsername = null;
			private String mPassword = null;

			@Override
			public void success(ResponseAPDU aResponse, String aFrom) {
				try {
					if (mState.equals(StateMachine.READY)) {
						mState = StateMachine.APP_SELECTED;

						transmit(aFrom, getTerminals(aFrom).toArray()[0].toString(), new CommandAPDU(new VerifyPIN(mPIN).getBytes()), this);
					} else if (mState.equals(StateMachine.APP_SELECTED)) {
						mState = StateMachine.AUTHENTICATED;

						transmit(aFrom, getTerminals(aFrom).toArray()[0].toString(), new CommandAPDU(new SearchSite(mSiteName).getBytes()), this);
					} else if (mState.equals(StateMachine.AUTHENTICATED)) {
						mState = StateMachine.SITE_SELECTED;

						transmit(aFrom, getTerminals(aFrom).toArray()[0].toString(), new CommandAPDU(new GetUser().getBytes()), this);
					} else if (mState.equals(StateMachine.SITE_SELECTED)) {
						mState = StateMachine.GET_USER;
						mUsername = new String(aResponse.getData());

						transmit(aFrom, getTerminals(aFrom).toArray()[0].toString(), new CommandAPDU(new GetPassword().getBytes()), this);
					} else if (mState.equals(StateMachine.GET_USER)) {
						mState = StateMachine.GET_PASSWORD;
						mPassword = new String(aResponse.getData());

						Map<String, String> lResponse = new FastMap<String, String>();
						lResponse.put("username", mUsername);
						lResponse.put("password", mPassword);

						((TransactionContext) getContext()).success(lResponse);
					}
				} catch (InvalidConnectorIdentifier ex) {
					mLog.error(ex.getMessage(), ex);
				}
			}

			@Override
			public void failure(FailureReason aReason, String aFrom) {
				mLog.error(">> Failure in state " + mState.name());
			}
		});
	}
}
