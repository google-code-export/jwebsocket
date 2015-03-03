//	---------------------------------------------------------------------------
//	jWebSocket JMS Plug-in test specs (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//  Alexander Schulze, Germany (NRW)
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

jws.tests.JMS = {

	title: "JMS plug-in",
	description: "jWebSocket JMS plug-in",
	category: "Community Edition",

	// this spec tests the listen method of the JMS plug-in
	testListen: function() {
		var lSpec = "listen (no Pub/Sub)";
		
		it( lSpec, function () {

			var lResponse = {};
			jws.Tests.getAdminTestConn().listenJms( 
				"connectionFactory",	// aConnectionFactoryName, 
				"testQueue",			// aDestinationName, 
				false,					// aPubSubDomain,
				{	OnResponse: function( aToken ) {
						lResponse = aToken;
					}
				}
			);

			waitsFor(
				function() {
					return( 0 === lResponse.code );
				},
				lSpec,
				3000
			);

			runs( function() {
				expect( lResponse.code ).toEqual( 0 );
			});

		});
	},

	// this spec tests the listen method of the JMS plug-in
	testUnlisten: function() {
		var lSpec = "unlisten (no Pub/Sub)";
		
		it( lSpec, function () {

			var lResponse = {};
			jws.Tests.getAdminTestConn().unlistenJms( 
				"connectionFactory",	// aConnectionFactoryName, 
				"testQueue",			// aDestinationName, 
				false,					// aPubSubDomain,
				{	OnResponse: function( aToken ) {
						lResponse = aToken;
					}
				}
			);

			waitsFor(
				function() {
					return( 0 === lResponse.code );
				},
				lSpec,
				3000
			);

			runs( function() {
				expect( lResponse.code ).toEqual( 0 );
			});

		});
	},
	
	runSpecs: function() {
		jws.tests.JMS.testListen();
		jws.tests.JMS.testUnlisten();
	}
};