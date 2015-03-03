//	---------------------------------------------------------------------------
//	jWebSocket - UserServiceImpl (Community Edition, CE)
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
package org.jwebsocket.gaming.pingpong.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.jwebsocket.gaming.pingpong.api.IUserService;
import java.util.Date;
import org.jwebsocket.util.JWSTimerTask;

/**
 *
 * @author armando
 */
public class UserServiceImpl extends JWSTimerTask implements IUserService {

	private final DBCollection mCollection;
	private final int mRemoveExpiredUsersTime;

	/**
	 *
	 * @param aCollection
	 * @param aRemoveExpiredUsersTime
	 */
	public UserServiceImpl(DBCollection aCollection, int aRemoveExpiredUsersTime) {
		mCollection = aCollection;
		mRemoveExpiredUsersTime = aRemoveExpiredUsersTime;
	}

	@Override
	public boolean containsUn(String aUserName) {
		return null != mCollection.findOne(new BasicDBObject("user", aUserName));
	}

	@Override
	public boolean create(DBObject aUser) {
		if (containsUn(aUser.get("user").toString())) {
			return false;
		}
		mCollection.insert(aUser);
		return true;
	}

	@Override
	public DBCursor getProfileList() {
		DBCursor lProfile = mCollection.find(new BasicDBObject(),
				new BasicDBObject("user", true).append("wins", true).
				append("lost", true).append("_id", false));
		return lProfile;
	}

	@Override
	public boolean isPwdCorrect(String aUserName, String aPwd) {
		DBObject lUser = mCollection.findOne(
				new BasicDBObject().append("user", aUserName).append("pwd", aPwd));
		if (lUser == null) {
			return false;
		}
		DBObject lData = new BasicDBObject();
		lData.put("date", new Date());
		mCollection.update(lUser, new BasicDBObject("$set", lData));
		return true;
	}

	@Override
	public void updateValue(String aUserName, int aWins, int aLost) {
		DBObject lData = new BasicDBObject();
		DBObject lDBObject = getProfile(aUserName);

		if (aWins == 1) {
			lData.put("wins", aWins + Integer.parseInt(lDBObject.get("wins").
					toString()));
			lData.put("lost", aLost + Integer.parseInt(lDBObject.get("lost").
					toString()));
		} else {
			lData.put("wins", aWins + Integer.parseInt(lDBObject.get("wins").
					toString()));
			lData.put("lost", aLost + Integer.parseInt(lDBObject.get("lost").
					toString()));
		}
		mCollection.update(lDBObject, new BasicDBObject("$set", lData));
	}

	@Override
	public DBObject getProfile(String aUserName) {

		return mCollection.findOne(new BasicDBObject().append("user", aUserName));

	}

	@Override
	public void removeUser(int aDay) {
		final long lMllsecsPerDay = 24 * 60 * 60 * 1000;
		DBCursor lProfile = mCollection.find(new BasicDBObject(),
				new BasicDBObject("date", true).append("_id", false));
		for (DBObject lDB : lProfile) {
			Date lLastDate = (Date) lDB.get("date");
			Date lCurrentDate = new Date();
			long lDifference = (lCurrentDate.getTime() - lLastDate.getTime())
					/ lMllsecsPerDay;
			if (lDifference >= aDay) {
				mCollection.remove(lDB);
			}
		}
	}

	@Override
	public void runTask() {
		removeUser(mRemoveExpiredUsersTime);
	}
}
