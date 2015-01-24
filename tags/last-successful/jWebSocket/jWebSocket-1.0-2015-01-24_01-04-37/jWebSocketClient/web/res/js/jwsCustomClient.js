//	---------------------------------------------------------------------------
//	Custom specific WebSocket Base Client (Community Edition, CE)
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

//	---------------------------------------------------------------------------
//	This demo shows how to easily create a low level custom client
//	w/o the jWebSocket token processes.
//	---------------------------------------------------------------------------

jws.oop.declareClass( "jws", "jWebSocketCustomClient", jws.jWebSocketBaseClient, {

	processOpenEvent: function( aEvent ) {
		// can to be overwritten in descendant classes
		// to easily handle open event in this class
		if( console ) {
			console.log( "connected" );
		}
	},

	processMessageEvent: function( aEvent ) {
		// can to be overwritten in descendant classes
		// to easily handle message event in this class
		if( console ) {
			console.log( "message: " + aEvent.data );
		}
	},

	processCloseEvent: function( aEvent ) {
		// can to be overwritten in descendant classes
		// to easily handle open event in this class
		if( console ) {
			console.log( "disconnected" );
		}
	},

	connect: function( aURL, aOptions ) {
		var lRes = true;
		try {
			// call inherited connect, catching potential exception
			arguments.callee.inherited.call( this, aOptions );
		} catch( ex ) {
			// handle exception here
			lRes = false;
		}
		return lRes;
	},

	sendStream: function( aStream ) {
		var lRes = true;
		try {
			// call inherited sendStream, catching potential exception
			arguments.callee.inherited.call( this, aStream );
		} catch( ex ) {
			// handle exception here
			lRes = false;
		}
		return lRes;
	},

	disconnect: function( aOptions ) {
		var lRes = true;
		try {
			// call inherited disconnect, catching potential exception
			arguments.callee.inherited.call( this, aOptions );
		} catch( ex ) {
			// handle exception here
			lRes = false;
		}
		return lRes;
	}

});

