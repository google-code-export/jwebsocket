//	---------------------------------------------------------------------------
//	jWebSocket - ChannelGroup (Community Edition, CE)
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

import java.util.Collections;
import java.util.Map;
import javolution.util.FastMap;

/**
 * maintains a group of channels, which e.g. can be used watch lists or similar.
 *
 * @author Alexander Schulze
 */
public class ChannelGroup {

	private final String mId = null;
	private Map<String, Channel> mChannels = null;

	/**
	 *
	 *
	 * @param aId
	 */
	public ChannelGroup(String aId) {
		mChannels = new FastMap<String, Channel>();
	}

	/**
	 * adds a new channel to an existing channel group.
	 *
	 * @param aChannel
	 * @return
	 */
	public boolean addChannel(Channel aChannel) {
		if (aChannel != null) {
			mChannels.put(aChannel.getId(), aChannel);
			return true;
		}
		return false;
	}

	/**
	 * checks if the given channel does exist in the channel group.
	 *
	 * @param aChannel
	 * @return
	 */
	public boolean containsChannel(Channel aChannel) {
		return mChannels.containsValue(aChannel);
	}

	/**
	 * checks if a channel with the given id does exist in the channel group.
	 *
	 * @param aChannelId
	 * @return
	 */
	public boolean containsChannel(String aChannelId) {
		return mChannels.containsKey(aChannelId);
	}

	/**
	 * returns the channel with the given channel id or <tt>null</tt> if the
	 * channel does not exist in the channel group.
	 *
	 * @param aChannelId
	 * @return
	 */
	public Channel getChannel(String aChannelId) {
		return mChannels.get(aChannelId);
	}

	/**
	 * removes a channel from an existing channel group.
	 *
	 * @param aChannel
	 * @return
	 */
	public boolean removeChannel(Channel aChannel) {
		if (aChannel != null) {
			mChannels.remove(aChannel.getId());
			return true;
		}
		return false;
	}

	/**
	 * removes a channel from an existing channel group.
	 *
	 * @param aChannelId
	 * @return
	 */
	public boolean removeChannel(String aChannelId) {
		if (aChannelId != null) {
			mChannels.remove(aChannelId);
			return true;
		}
		return false;
	}

	/**
	 * returns the unmodifiable map of channels in the channel group.
	 *
	 * @return
	 */
	public Map<String, Channel> getChannels() {
		return Collections.unmodifiableMap(mChannels);
	}

	/**
	 * returns the id of the channel group.
	 *
	 * @return the mId
	 */
	public String getId() {
		return mId;
	}
}
