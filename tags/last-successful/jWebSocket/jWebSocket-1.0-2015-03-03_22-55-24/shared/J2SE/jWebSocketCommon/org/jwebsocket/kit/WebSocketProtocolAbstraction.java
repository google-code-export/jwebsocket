//	---------------------------------------------------------------------------
//	jWebSocket - WebSocketProtocolAbstraction (Community Edition, CE)
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
package org.jwebsocket.kit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.config.JWebSocketCommonConstants;

/**
 * Utility class for packetizing WebSocketPacket into web socket protocol packet
 * or packets (with fragmentation) and vice versa.
 * <p/>
 * <p>
 * Web socket protocol packet specification (see:
 * http://draft-ietf-hybi-thewebsocketprotocol-13): </p>
 * <pre>
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-------+-+-------------+-------------------------------+
 * |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
 * |I|S|S|S|  (4)  |A|     (7)     |             (16/63)           |
 * |N|V|V|V|       |S|             |   (if payload len==126/127)   |
 * | |1|2|3|       |K|             |                               |
 * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
 * |     Extended payload length continued, if payload len == 127  |
 * + - - - - - - - - - - - - - - - +-------------------------------+
 * |                               |Masking-key, if MASK set to 1  |
 * +-------------------------------+-------------------------------+
 * | Masking-key (continued)       |          Payload Data         |
 * +-------------------------------- - - - - - - - - - - - - - - - +
 * :                     Payload Data continued ...                :
 * + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
 * |                     Payload Data continued ...                |
 * +---------------------------------------------------------------+
 * </pre>
 *
 * @author jang
 * @author Alexander Schulze
 * @author Rolando Betancourt Toucet
 */
public class WebSocketProtocolAbstraction {
	// web socket protocol packet types

	/**
	 *
	 */
	public final static boolean MASKED = true;
	/**
	 *
	 */
	public final static boolean UNMASKED = false;

	/**
	 *
	 * @param aVersion
	 * @return
	 */
	public static boolean isHixieVersion(int aVersion) {
		return JWebSocketCommonConstants.WS_SUPPORTED_HIXIE_VERSIONS.contains(aVersion);
	}

	/**
	 *
	 * @param aVersion
	 * @return
	 */
	public static boolean isHybiVersion(int aVersion) {
		return JWebSocketCommonConstants.WS_SUPPORTED_HYBI_VERSIONS.contains(aVersion);
	}

	/**
	 *
	 * @param aVersion
	 * @return
	 */
	public static boolean isValidVersion(int aVersion) {
		return isHixieVersion(aVersion) || isHybiVersion(aVersion);
	}

	/**
	 *
	 * @param aDraft
	 * @return
	 */
	public static boolean isHixieDraft(String aDraft) {
		return JWebSocketCommonConstants.WS_SUPPORTED_HIXIE_DRAFTS.contains(aDraft);
	}

	/**
	 *
	 * @param aDraft
	 * @return
	 */
	public static boolean isHybiDraft(String aDraft) {
		return JWebSocketCommonConstants.WS_SUPPORTED_HYBI_DRAFTS.contains(aDraft);
	}

	/**
	 *
	 * @param aDraft
	 * @return
	 */
	public static boolean isValidDraft(String aDraft) {
		return isHixieDraft(aDraft) || isHybiDraft(aDraft);
	}

	/**
	 *
	 * @param aReasonCode
	 * @param aReason
	 * @return
	 */
	public static byte[] calcClosePayload(int aReasonCode, byte[] aReason) {
		int lNewLength = aReason.length + 2;
		byte[] lRes = new byte[lNewLength];
		System.arraycopy(aReason, 0, lRes, 2, aReason.length);
		aReasonCode &= 0xFFFF;
		lRes[0] = (byte) (aReasonCode >> 8);
		lRes[1] = (byte) (aReasonCode & 0xFF);
		return lRes;
	}

	/**
	 *
	 * @param aReasonCode
	 * @param aReasonString
	 * @return
	 */
	public static byte[] calcCloseData(int aReasonCode, String aReasonString) {
		return calcClosePayload(aReasonCode, aReasonString.getBytes());
	}

	/**
	 * converts an abstract data packet into a protocol specific frame according
	 * to the correct version.
	 *
	 * @param aVersion
	 * @param aDataPacket
	 * @param aMasked
	 * @return
	 */
	public static byte[] rawToProtocolPacket(int aVersion, WebSocketPacket aDataPacket, boolean aMasked) {

		byte[] lBuff = new byte[2]; // resulting packet will have at least 2 bytes
		WebSocketFrameType lFrameType = aDataPacket.getFrameType();
		int lTargetType = frameTypeToOpcode(aVersion, lFrameType);
		if (lTargetType == -1) {
			throw new WebSocketRuntimeException(
					"Cannot construct a packet with unknown packet type: "
					+ lFrameType);
		}
		int lMaskByte = (aMasked ? 0x80 : 0x00);

		// Determine fragmentation
		// 0x80 means it's the final frame, the RSV bits are not yet set
		if (aDataPacket.isFragment()) {
			lBuff[0] = (byte) (lTargetType);
		} else {
			lBuff[0] = (byte) (lTargetType | 0x80);
		}

		int lPayloadLen = aDataPacket.getByteArray().length;

		// Here, the spec allows payload length with up to 64-bit integer
		// in size (that is long data type in java):
		// ----
		//   The length of the payload: if 0-125, that is the payload length.
		//   If 126, the following 2 bytes interpreted as a 16 bit unsigned
		//   integer are the payload length.  If 127, the following 8 bytes
		//   interpreted as a 64-bit unsigned integer (the high bit must be 0)
		//   are the payload length.
		// ----
		// However, arrays in java may only have Integer.MAX_VALUE(32-bit) elements.
		// Therefore, we never set target payload length greater than signed 32-bit number
		// (Integer.MAX_VALUE).
		if (lPayloadLen < 126) {
			lBuff[1] = (byte) (lPayloadLen | lMaskByte); // just write the payload length
		} else if (lPayloadLen >= 126 && lPayloadLen < 0xFFFF) {
			// first write 126 (meaning, there will follow two bytes for actual length)
			lBuff[1] = (byte) (126 | lMaskByte);
			int lSize = lBuff.length;
			lBuff = copyOf(lBuff, lSize + 2);
			lBuff[lSize] = (byte) ((lPayloadLen >>> 8) & 0xFF);
			lBuff[lSize + 1] = (byte) (lPayloadLen & 0xFF);
		} else if (lPayloadLen >= 0xFFFF) {
			// first write 127 (meaning, there will follow eight bytes for actual length)
			lBuff[1] = (byte) (127 | lMaskByte);
			long lLen = (long) lPayloadLen;
			int lSize = lBuff.length;
			lBuff = copyOf(lBuff, lSize + 8);
			lBuff[lSize] = (byte) (lLen >>> 56);
			lBuff[lSize + 1] = (byte) (lLen >>> 48);
			lBuff[lSize + 2] = (byte) (lLen >>> 40);
			lBuff[lSize + 3] = (byte) (lLen >>> 32);
			lBuff[lSize + 4] = (byte) (lLen >>> 24);
			lBuff[lSize + 5] = (byte) (lLen >>> 16);
			lBuff[lSize + 6] = (byte) (lLen >>> 8);
			lBuff[lSize + 7] = (byte) lLen;
		}

		int lSize = lBuff.length;

		if (aMasked) {
			lBuff = copyOf(lBuff, lSize + 4);
			lSize = lBuff.length - 1;

			// Add masking key to data frame, The masking key is a 32-bit value chosen at random.
			// The masking does not affect the length of the payload data
			for (int lIdx = 0; lIdx < 4; lIdx++) {
				lBuff[lSize--] = getByteRandom();
			}

			byte[] lMask = new byte[4];
			System.arraycopy(lBuff, lBuff.length - 4, lMask, 0, 4);

			//To masked data is use the follow algorithm 
			byte[] lBuffData = new byte[aDataPacket.getByteArray().length];
			System.arraycopy(aDataPacket.getByteArray(), 0, lBuffData, 0, lBuffData.length);
			byte[] lMaskedData = new byte[lBuffData.length];
			int lPos = 0;
			for (int lIdx = 0; lIdx < lBuffData.length; lIdx++) {
				lMaskedData[lIdx] = (byte) (lBuffData[lIdx] ^ lMask[lPos]);
				if (3 == lPos) {
					lPos = 0;
				} else {
					lPos++;
				}
			}
			lBuff = copyOf(lBuff, lBuff.length + lMaskedData.length);
			System.arraycopy(lMaskedData, 0, lBuff, lBuff.length - lMaskedData.length, lMaskedData.length);
		} else {
			// create a new byte array to contain header and payload
			lBuff = copyOf(lBuff, lSize + aDataPacket.getByteArray().length);
			// finally copy the payload into the transmissin packet
			System.arraycopy(aDataPacket.getByteArray(), 0, lBuff, lSize, aDataPacket.getByteArray().length);
		}
		return lBuff;
	}

	/**
	 *
	 * @param aIS
	 * @return
	 * @throws Exception
	 */
	public static int read(InputStream aIS) throws Exception {
		int lByte = aIS.read();
		if (lByte < 0) {
			throw new Exception("EOF");
		}
		return lByte;
	}

	/**
	 *
	 * @param aVersion
	 * @param aIS
	 * @return
	 * @throws Exception
	 */
	public static WebSocketPacket protocolToRawPacket(int aVersion, InputStream aIS) throws Exception {
		// begin normal packet read
		int lFlags = aIS.read();
		if (lFlags == -1) {
			return null;
		}

		// TODO: handle if stream gets closed within this method!
		ByteArrayOutputStream aBuff = new ByteArrayOutputStream();

		// Determine fragmentation
		// from Hybi Draft 04 it's the FIN flag < 04 its a more flag ;-)
		boolean lFragmented = (aVersion >= 4
				? (lFlags & 0x80) == 0x00
				: (lFlags & 0x80) == 0x80);
		boolean lMasked;
		int[] lMask = new int[4];

		// ignore upper 4 bits for now
		int lOpcode = lFlags & 0x0F;
		WebSocketFrameType lFrameType = WebSocketProtocolAbstraction.opcodeToFrameType(aVersion, lOpcode);

		if (lFrameType == WebSocketFrameType.INVALID) {
			// Could not determine packet type, ignore the packet.
			// Maybe we need a setting to decide, if such packets should abort the connection?
			/*
			 * if (mLog.isDebugEnabled()) { mLog.debug("Dropping packet with
			 * unknown type: " + lOpcode); }
			 */
		} else {
			// Ignore first bit. Payload length is next seven bits, unless its value is greater than 125.
			long lPayloadLen = read(aIS);

			// TODO: officially unmasked frames may not be accepted anymore, since hybi draft #10
			lMasked = (lPayloadLen & 0x80) == 0x80;
			lPayloadLen &= 0x7F;

			if (lPayloadLen == 126) {
				// following two bytes are acutal payload length (16-bit unsigned integer)
				lPayloadLen = read(aIS) & 0xFF;
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
			} else if (lPayloadLen == 127) {
				// following eight bytes are actual payload length (64-bit unsigned integer)
				lPayloadLen = read(aIS) & 0xFF;
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
				lPayloadLen = (lPayloadLen << 8) | (read(aIS) & 0xFF);
			}

			if (lMasked) {
				lMask[0] = read(aIS) & 0xFF;
				lMask[1] = read(aIS) & 0xFF;
				lMask[2] = read(aIS) & 0xFF;
				lMask[3] = read(aIS) & 0xFF;
			}

			if (lPayloadLen > 0) {
				// payload length may be extremely long, so we read in loop rather
				// than construct one byte[] array and fill it with read() method,
				// because java does not allow longs as array size
				if (lMasked) {
					int j = 0;
					while (lPayloadLen-- > 0) {
						aBuff.write(read(aIS) ^ lMask[j]);
						j++;
						j &= 3;
					}
				} else {
					while (lPayloadLen-- > 0) {
						aBuff.write(read(aIS));
					}
				}
			}
		}

		WebSocketPacket lRes = new RawPacket(lFrameType, aBuff.toByteArray());
		return lRes;
	}

	/*
	 * TODO: implement fragmentation
	 */
	/**
	 *
	 * @param aSrc
	 * @param aFragmentSize
	 * @return
	 */
	public static List<byte[]> toProtocolPacketFragmented(WebSocketPacket aSrc, int aFragmentSize) {
		throw new UnsupportedOperationException("Fragmentation is currently not supported");
	}

	/**
	 * converts a WebSocket protocol opcode to an abstract jWebSocket frame type
	 *
	 * @param aVersion
	 * @param aOpcode
	 * @return
	 */
	public static WebSocketFrameType opcodeToFrameType(int aVersion, int aOpcode) {

		WebSocketOpcode lOpcode = new WebSocketOpcode(aVersion);

		if (aOpcode == lOpcode.OPCODE_FRAGMENT) {
			return WebSocketFrameType.FRAGMENT;
		} else if (aOpcode == lOpcode.OPCODE_TEXT) {
			return WebSocketFrameType.TEXT;
		} else if (aOpcode == lOpcode.OPCODE_BINARY) {
			return WebSocketFrameType.BINARY;
		} else if (aOpcode == lOpcode.OPCODE_CLOSE) {
			return WebSocketFrameType.CLOSE;
		} else if (aOpcode == lOpcode.OPCODE_PING) {
			return WebSocketFrameType.PING;
		} else if (aOpcode == lOpcode.OPCODE_PONG) {
			return WebSocketFrameType.PONG;
		} else {
			return WebSocketFrameType.INVALID;
		}
	}

	/**
	 *
	 * @param aJWebSocketFormatConstant
	 * @return
	 */
	/*
	 * public static int toRawPacketType(String aJWebSocketFormatConstant) {
	 * return
	 * JWebSocketCommonConstants.WSWS_FORMAT_BINARY.equals(aJWebSocketFormatConstant)
	 * ? RawPacket.FRAMETYPE_BINARY // treat everything else as utf8 packet type
	 * : RawPacket.FRAMETYPE_UTF8; }
	 */
	/**
	 *
	 * @param aVersion
	 * @param aFrameType
	 * @return
	 */
	public static int frameTypeToOpcode(int aVersion, WebSocketFrameType aFrameType) {

		WebSocketOpcode lOpcode = new WebSocketOpcode(aVersion);

		switch (aFrameType) {
			case FRAGMENT:
				return lOpcode.OPCODE_FRAGMENT;
			case TEXT:
				return lOpcode.OPCODE_TEXT;
			case BINARY:
				return lOpcode.OPCODE_BINARY;
			case CLOSE:
				return lOpcode.OPCODE_CLOSE;
			case PING:
				return lOpcode.OPCODE_PING;
			case PONG:
				return lOpcode.OPCODE_PONG;
			default:
				return lOpcode.OPCODE_INVALID;
		}
	}

	/**
	 *
	 * @param aEncoding
	 * @return
	 */
	public static WebSocketFrameType encodingToFrameType(WebSocketEncoding aEncoding) {
		switch (aEncoding) {
			case TEXT: {
				return WebSocketFrameType.TEXT;
			}
			case BINARY: {
				return WebSocketFrameType.BINARY;
			}
			default: {
				return WebSocketFrameType.INVALID;
			}
		}
	}

	private static byte[] copyOf(byte[] aOriginal, int aNewLength) {
		byte[] lCopy = new byte[aNewLength];
		System.arraycopy(aOriginal, 0, lCopy, 0, Math.min(aOriginal.length, aNewLength));
		return lCopy;
	}

	private static byte getByteRandom() {
		Random lRan = new Random();
		byte lByte;
		do {
			lByte = (byte) lRan.nextInt(255);
		} while (lByte < 0);
		return lByte;
	}
}
