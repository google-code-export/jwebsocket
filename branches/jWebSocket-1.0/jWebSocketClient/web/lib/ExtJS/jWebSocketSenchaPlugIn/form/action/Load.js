//	<JasobNoObfs>
//  ---------------------------------------------------------------------------
//  jWebSocket - Sencha ExtJS PlugIn (Community Edition, CE)
//  ---------------------------------------------------------------------------
//  Copyright 2010-2013 Innotrade GmbH (jWebSocket.org)
//  Alexander Schulze, Germany (NRW)
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//  ---------------------------------------------------------------------------
//	</JasobNoObfs>

// ## :#file:*:jwsExtJSPlugIn.js
// ## :#d:en:Allows including jWebSocket Client in ExtJS/Sencha Touch Applications. _
// ## :#d:en:Gives to ExtJS users a new WebSocket based Ext.data.Proxy and also _
// ## :#d:en:includes the jWebSocket JavaScript Client transparently for the user _ 
// ## :#d:en:inside the class Ext.jws and fires WebSocket events inside it.

/**
 * @author Osvaldo Aguilar Lauzurique, (oaguilar, La Habana), Alexander Rojas Hernandez (arojas, Pinar del Rio), Victor Antonio Barzana Crespo (vbarzana, Münster Westfalen)
 **/

//	---------------------------------------------------------------------------
//  This class constains the jWebSocket implementation of the 
//  [tt]Ext.form.action.Load[/tt] class
//	---------------------------------------------------------------------------

//:package:*:Ext.jws.form.action
//:class:*:Ext.jws.form.action.Load
//:ancestor:*:Ext.form.action.Load
//:d:en:Implementation of the default load action of the form but using _
//:d:en:jWebSocketClient to get the data from the jWebSocket Server. _
//:d:en:This class representsis a proxy for the underlying BasicForm's load call
Ext.define( 'Ext.jws.form.action.Load', {
	extend: 'Ext.form.action.Load',
	requires: [ 'Ext.direct.Manager' ],
	alternateClassName: 'Ext.jws.form.action.Load',
	alias: 'formaction.jwsload',
	type: 'jwsload',
	ns: undefined,
	tokentype: undefined,
	//:m:*:constructor
	//:d:en:Creates the Ext.jws.form.action.Load class, this object will be _
	//:d:en:passed as parameter in the form.load method and will use the _
	//:d:en:current implementation to process the load action using jWebSocketClient
	//:a:en::aConfig:Object:The proxy configuration, this parameter is required.
	//:r:*::void:none
	constructor: function( ) {
		var self = this;
		self.callParent( arguments );

		if ( typeof self.ns === "undefined" ) {
			Ext.Error.raise( "You must specify a namespace (ns) value!" );
		}

		if ( typeof self.tokentype === "undefined" ) {
			Ext.Error.raise( "You must specify a token type (tokentype) value!" );
		}
	},
	//:m:*:run
	//:d:en:This method is executed by the internal doAction method to _
	//:d:en:perform application-specific processing. By overriding this method _
	//:d:en:the request to load the data from the server will be made using _
	//:d:en:the jWebSocketClient
	//:a:en::::none
	//:r:*::void:none
	run: function( ) {
		var lCallbacks = this.createCallback();
		Ext.jws.Client.send( this.ns, this.tokentype, this.getParams(), lCallbacks, this );
	},
	//:m:*:processResponse
	//:d:en:Processes the response returned by the server and fires the success _
	//:d:en:or failure callback depending on the results
	//:a:en::aResponse:Object:The response returned by the server
	//:r:*:fResult:Object:The response ready for the user
	processResponse: function( aResponse ) {
		this.fResponse = aResponse;
		if ( !aResponse.responseText && !aResponse.responseXML && !aResponse.type ) {
			return true;
		}
		return (this.fResult = this.handleResponse( aResponse ));
	},
	//:m:*:handleResponse
	//:d:en:Internal method to change the incoming jWebSocket server resopnse _
	//:d:en:to a normal ExtJS response, there are two important aspects to know _
	//:d:en:will be failure when the code != 0 and success variable not defined _
	//:d:en:or false, in other case the response will be success
	//:a:en::aResponse:Object:The response returned by the server
	//:r:*:fResult:Object:The response modified and ready for the client
	handleResponse: function( aResponse ) {
		if ( aResponse ) {
			var lData = aResponse.data ? aResponse.data[0] : null;
			return {
				success: aResponse.success || aResponse.code === 0,
				data: lData
			};
		}
		return Ext.decode( aResponse.data );
	}
} );