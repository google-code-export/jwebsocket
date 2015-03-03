//	---------------------------------------------------------------------------
//	jWebSocket ItemStorage Plug-in EE test specs (Community Edition, CE)
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

jws.tests.ItemStorageEE = {

	title: "ItemStorage EE plug-in",
	description: "jWebSocket itemstorage (enterprise edition) plug-in. Designed for generic data storage.",
	category: "Enterprise Edition",
	priority: 30,
	
	testCreateCollection: function(aCollectionName, aItemType, aSecretPwd, aAccessPwd, aIsPrivate, aCapacity, aExpectedCode) {
		
		var lSpec = "createItemCollection (admin, " + aCollectionName + ", " + aItemType
		+ ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			
			jws.Tests.getAdminTestConn().setConfiguration(jws.ItemStoragePlugIn.NS, {
				events: {
					itemUpdateOnly: true
				}
			});
			
			jws.Tests.getAdminTestConn().createCollection( aCollectionName, aItemType, 
				aSecretPwd, aAccessPwd,  aIsPrivate, {
					capacity: aCapacity,
					OnResponse: function( aToken ) {
						lResponse = aToken;
					}
				});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});

		});
	},
	
	testRemoveCollection: function(aCollectionName,aSecretPwd, aExpectedCode) {
		var lSpec = "removeItemCollection (admin, " + aCollectionName + ", " + aSecretPwd
		+ ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().removeCollection( aCollectionName, aSecretPwd, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testExistsCollection: function(aCollectionName, aExists) {
		var lSpec = "existsCollection (admin, " + aCollectionName
		+ ", " + aExists + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().existsCollection( aCollectionName, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.exists ).toEqual( aExists );
			});
		});
	},
	
	testSubscribeCollection: function(aCollectionName, aAccessPwd, aExpectedCode) {
		var lSpec = "subscribeCollection (admin, " + aCollectionName + ", " + aAccessPwd
		+ ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			var lEvent = null;
			
			jws.Tests.getAdminTestConn().setItemStorageCallbacks({
				OnCollectionSubscription: function (aToken){
					lEvent = aToken;
				}
			});
			jws.Tests.getAdminTestConn().subscribeCollection(aCollectionName, aAccessPwd, {
				OnResponse: function( aToken ) {
					if (-1 == aToken.code){
						lEvent = false;
					}
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse && null != lEvent );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testUnsubscribeCollection: function(aCollectionName, aExpectedCode) {
		var lSpec = "unsubscribeCollection (admin, " + aCollectionName + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().unsubscribeCollection(aCollectionName, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testAuthorizeCollection: function(aCollectionName, aSecretPwd, aExpectedCode) {
		var lSpec = "authorizeCollection (admin, " + aCollectionName + ", " + aSecretPwd
		+ ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			var lEvent = null;
			
			jws.Tests.getAdminTestConn().setItemStorageCallbacks({
				OnCollectionAuthorization: function (aToken){
					lEvent = aToken;
				}
			});
			jws.Tests.getAdminTestConn().authorizeCollection( aCollectionName, aSecretPwd, {
				OnResponse: function( aToken ) {
					if (-1 == aToken.code){
						lEvent = false;
					}
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse && null != lEvent );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testClearCollection: function(aCollectionName, aSecretPwd, aExpectedCode) {
		var lSpec = "clearCollection (admin, " + aCollectionName + ", " + aSecretPwd
		+ ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			var lEvent = null;
			
			jws.Tests.getAdminTestConn().setItemStorageCallbacks({
				OnCollectionCleaned: function (aToken){
					lEvent = aToken;
				}
			});
			jws.Tests.getAdminTestConn().clearCollection( aCollectionName, aSecretPwd, {
				OnResponse: function( aToken ) {
					if (-1 == aToken.code){
						lEvent = false;
					}
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse && null != lEvent );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testEditCollection: function(aCollectionName, aSecretPwd, aNewSecretPwd, 
		aAccessPwd, aIsPrivate, aExpectedCode) {
		var lSpec = "editCollection (admin, " + aCollectionName + ", " 
		+ aSecretPwd + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			
			jws.Tests.getAdminTestConn().editCollection(aCollectionName, aSecretPwd, {
				newSecretPassword: aNewSecretPwd,
				accessPassword: aAccessPwd,
				isPrivate: aIsPrivate,
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testRestartCollection: function(aCollectionName, aSecretPwd, aExpectedCode) {
		var lSpec = "restartCollection (admin, " + aCollectionName + ", " 
		+ aSecretPwd + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			var lEvent = null;
			
			jws.Tests.getAdminTestConn().setItemStorageCallbacks({
				OnCollectionRestarted: function (aToken){
					lEvent = aToken;
				}
			});
			jws.Tests.getAdminTestConn().restartCollection(aCollectionName, aSecretPwd, {
				OnResponse: function( aToken ) {
					if (-1 == aToken.code){
						lEvent = false;
					}
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse && null != lEvent );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testGetCollectionNames: function( aUserOnly, aExpectedCode, aExpectedSize) {
		var lSpec = "getCollectionNames (admin, " + aExpectedCode + ", " + aExpectedSize + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			
			jws.Tests.getAdminTestConn().getCollectionNames( aUserOnly, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				if (0 == lResponse.code){
					expect( aExpectedSize == lResponse.data.length );
				}
			});
		});
	},
	
	testFindCollection: function( aCollectionName, aFound ) {
		var lSpec = "findCollection (admin, " + aCollectionName + ", " + aFound + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			
			jws.Tests.getAdminTestConn().findCollection( aCollectionName, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( null != lResponse.data ).toEqual( aFound );
			});
		});
	},
	
	testSaveItem: function( aCollectionName, aItem, aExpectedCode ) {
		var lSpec = "saveItem (admin, " + aCollectionName + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			var lEvent = null;
			
			jws.Tests.getAdminTestConn().setItemStorageCallbacks({
				OnItemSaved: function (aToken){
					lEvent = aToken;
				}
			});
			
			jws.Tests.getAdminTestConn().saveItem(aCollectionName, aItem, {
				OnResponse: function( aToken ) {
					if (0 != aToken.code){
						lEvent = false;
					}
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse && null != lEvent );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testRemoveItem: function( aCollectionName, aPK, aExpectedCode ) {
		var lSpec = "removeItem (admin, " + aCollectionName + ", " + aPK + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			var lEvent = null;
			
			jws.Tests.getAdminTestConn().setItemStorageCallbacks({
				OnItemRemoved: function (aToken){
					lEvent = aToken;
				}
			});
			
			jws.Tests.getAdminTestConn().removeItem(aCollectionName, aPK, {
				OnResponse: function( aToken ) {
					if (0 != aToken.code){
						lEvent = false;
					}
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse && null != lEvent );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testFindItemByPK: function( aCollectionName, aPK, aExpectedCode, aExists ) {
		var lSpec = "findItemByPK (admin, " + aCollectionName + ", " + aPK + ", " + aExists + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().findItemByPK(aCollectionName, aPK, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				if (0 == lResponse.code && aExists){
					expect( lResponse.data.pk  ).toEqual( aPK );
				}
			});
		});
	},
	
	testFindItemRandom: function( aCollectionName, aExpectedCode, aExists ) {
		var lSpec = "findItemRandom (admin, " + aCollectionName + ", " + aExists + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().findItemRandom(aCollectionName, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				expect( null != lResponse.data  ).toEqual( aExists );
			});
		});
	},
	
	testExistsItem: function( aCollectionName, aPK, aExists ) {
		var lSpec = "findItemByPK (admin, " + aCollectionName + ", " + aPK + ", " + aExists + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().existsItem(aCollectionName, aPK, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.exists  ).toEqual( aExists );
			});
		});
	},
	
	testFindItems: function( aCollectionName, aAttr, aValue, aExpectedCode, aExists ) {
		var lSpec = "findItems (admin, " + aCollectionName + ", " + aAttr 
		+ ", " + aValue + ", " + aExists + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().findItems(aCollectionName, aAttr, aValue, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				if (0 == lResponse.code && aExists){
					expect( lResponse.data.length > 0 ).toEqual( true );
				}
			});
		});
	},
	
	testRegisterItemDef: function( aItemType, aItemPK, aAttributes, aExpectedCode ) {
		var lSpec = "registerItemDefinition (admin, " + aItemType + ", " + aItemPK 
		+ ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().registerItemDefinition(aItemType, aItemPK, aAttributes, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testRemoveItemDef: function( aItemType, aExpectedCode ) {
		var lSpec = "removeItemDefinition (admin, " + aItemType + ", " 
		+ aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().removeItemDefinition(aItemType, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
			});
		});
	},
	
	testFindItemDef: function( aItemType, aExists ) {
		var lSpec = "findItemDefinition (admin, " + aItemType + ", " 
		+ aExists + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().findItemDefinition(aItemType, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( null != lResponse.data["type"] ).toEqual( aExists );
			});
		});
	},
	
	testExistsItemDef: function( aItemType, aExists ) {
		var lSpec = "existsItemDefinition (admin, " + aItemType + ", " 
		+ aExists + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().existsItemDefinition(aItemType, {
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.exists ).toEqual( aExists );
			});
		});
	},
	
	testListItemDef: function( aExpectedSize ) {
		var lSpec = "listDefinitions (admin, " + aExpectedSize + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().listItemDefinitions({
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.data.length >= aExpectedSize ).toEqual( true );
			});
		});
	},
	
	testListItems: function( aCollectionName, aOffset, aLength, aExpectedCode, aExpectedSize) {
		var lSpec = "listItems (admin, " + aCollectionName + ", " + aOffset 
		+ ", " + aLength + ", " + aExpectedSize + ", " + aExpectedCode + ")";
		
		it( lSpec, function () {

			var lResponse = null;
			jws.Tests.getAdminTestConn().listItems(aCollectionName, {
				offset: aOffset,
				length: aLength,
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				if (0 == lResponse.code){
					expect( lResponse.data.length  ).toEqual( aExpectedSize );
				}
			});
		});
	},
	
	testGetCollectionLogs: function( aCollectionName, aOffset, aLength, aExpectedCode, aExpectedSize) {
		var lSpec = "getLogs (admin, " + aCollectionName + ", " + aOffset 
		+ ", " + aLength + ", " + aExpectedCode + ", " + aExpectedSize + ")";
		
		it( lSpec, function () {
			var lResponse = null;
			jws.Tests.getAdminTestConn().getISLogs("collection", aCollectionName, {
				offset: aOffset,
				length: aLength,
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				if (0 == lResponse.code){
					expect( lResponse.data.length  ).toEqual( aExpectedSize );
				}
			});
		});
	},
	
	testGetItemLogs: function( aCollectionName, aItemPK, aOffset, aLength, aExpectedCode, aExpectedSize) {
		var lSpec = "getLogs (admin, " + aCollectionName + ":" + aItemPK+ ", " + aOffset 
		+ ", " + aLength + ", " + aExpectedCode + ", " + aExpectedSize + ")";
		
		it( lSpec, function () {
			var lResponse = null;
			jws.Tests.getAdminTestConn().getISLogs("item", aCollectionName, {
				offset: aOffset,
				length: aLength,
				itemPK: aItemPK,
				OnResponse: function( aToken ) {
					lResponse = aToken;
				}
			});

			waitsFor(
				function() {
					return( null != lResponse );
				},
				lSpec,
				3000
				);

			runs( function() {
				expect( lResponse.code ).toEqual( aExpectedCode );
				if (0 == lResponse.code){
					expect( lResponse.data.length  ).toEqual( aExpectedSize );
				}
			});
		});
	},
	
	runSpecs: function() {
		var lCollectionName = "mycontacts";
		var lPwd = "123";
		
		// create
		this.testCreateCollection(lCollectionName, "contact", lPwd, lPwd, false, 10, 0);
		this.testCreateCollection(lCollectionName, "contact", lPwd, lPwd, false, 10, -1);
		// get names
		this.testGetCollectionNames(false, 0, 1);
		this.testGetCollectionNames(true, 0, 1);
		
		// exists collection
		this.testExistsCollection(lCollectionName, true);
		
		// create other
		this.testCreateCollection(lCollectionName + "1", "contact", lPwd, lPwd, false, 10, 0);
		
		// exists collection
		this.testExistsCollection(lCollectionName + "1", true);
		// exists collection
		this.testExistsCollection("wrong collection name", false);
		
		// get names
		this.testGetCollectionNames(false, 0, 2);
		this.testGetCollectionNames(true, 0, 2);
		
		// get collection
		this.testFindCollection(lCollectionName, true);
		this.testFindCollection("wrong collection name", false);

		// subscribe
		this.testSubscribeCollection(lCollectionName, lPwd, 0);
		this.testUnsubscribeCollection(lCollectionName, 0);
		this.testSubscribeCollection(lCollectionName, lPwd, 0);
		this.testSubscribeCollection(lCollectionName, lPwd, -1); // subscribed already
		this.testSubscribeCollection(lCollectionName, "wrong password", -1);

		// get logs (testing pagination)
		this.testGetCollectionLogs(lCollectionName, 0, 10, 0, 4);
		this.testGetCollectionLogs(lCollectionName, 1, 10, 0, 3);
		this.testGetCollectionLogs(lCollectionName, 2, 10, 0, 2);
		this.testGetCollectionLogs(lCollectionName, 3, 10, 0, 1);
		this.testGetCollectionLogs("wrongCollectionName", 0, 10, -1, 3); // should fail (collection not exists)

		// restart
		this.testRestartCollection(lCollectionName, lPwd, 0);
		
		// find by PK
		this.testFindItemByPK("wrongCollectionName", "rsantamaria", -1, 
			true); // should fail (collection not exists)
		this.testFindItemByPK(lCollectionName, "rsantamaria", -1, 
			false); // should fail (not subscribed)
			
		// find items
		this.testFindItems("wrongCollectionName", "username", "rsantamaria", -1, 
			true); // should fail (collection not exists)
		this.testFindItems(lCollectionName, "username", "rsantamaria", -1, 
			false); // should fail (not subscribed)
		
		// subscribe again
		this.testSubscribeCollection(lCollectionName, lPwd, 0);
		
		// save item
		this.testSaveItem("wrongCollectionName", {
			name: "Rolando SM",
			mailAddress: "rsantamaria@jwebsocket.org",
			siteURL: "http://jwebsocket.org",
			comment: "jWebSocket developer",
			image: "base64 image content",
			username: "rsantamaria",
			sex: true
		}, -1); // should fail (collection not exists)
		
		// save item
		this.testSaveItem(lCollectionName, {
			name: "Rolando SM",
			mailAddress: "rsantamaria@jwebsocket.org",
			siteURL: "http://jwebsocket.org",
			comment: "jWebSocket developer",
			image: "base64 image content",
			username: "rsantamaria",
			sex: true
		}, -1); // should fail (not authorized)
		
		this.testRemoveItem(lCollectionName, "rsantamaria", 
			-1); // should fail (not authorized)
		this.testRemoveItem("wrongCollectionName", "rsantamaria", 
			-1); // should fail (collection not exists)

		// authorize
		this.testAuthorizeCollection(lCollectionName, lPwd, 0);
		
		// save item
		this.testSaveItem(lCollectionName, {
			name: "Rolando SM",
			mailAddress: "rsantamaria@jwebsocket.org",
			siteURL: "http://jwebsocket.org",
			comment: "jWebSocket developer",
			image: "base64 image content",
			username: "rsantamaria",
			sex: true
		}, 0);
		
		this.testGetItemLogs(lCollectionName, "rsantamaria", 0, 10, 0, 1);
		
		// find random
		this.testFindItemRandom(lCollectionName, 0, true);
		// find random
		this.testFindItemRandom("wrong collection name", -1, false);
		
		// save item (modify)
		this.testSaveItem(lCollectionName, {
			name: "Rolando Santamaria Maso",
			username: "rsantamaria"
		}, 0);
		
		this.testGetItemLogs(lCollectionName, "rsantamaria", 0, 10, 0, 2);
		this.testGetItemLogs(lCollectionName, "rsantamaria", 1, 10, 0, 1);
		
		// find by PK
		this.testFindItemByPK(lCollectionName, "rsantamaria", 0, true);
		this.testFindItemByPK(lCollectionName, "wrongPK", 0, false);
		
		// find items
		this.testFindItems(lCollectionName, "siteURL", "http://jwebsocket.org", 0, 
			true); 
		// find items
		this.testFindItems(lCollectionName, "siteURL", "%jwebsocket.org", 0, 
			true); 
		// find items
		this.testFindItems(lCollectionName, "siteURL", "http://jwebsocket%", 0, 
			true); 
		// find items
		this.testFindItems(lCollectionName, "siteURL", "%://jwebsocket%", 0, 
			true); 
		this.testFindItems(lCollectionName, "siteURL", "http://microsoft.com", 0, 
			false); 
		this.testFindItems(lCollectionName, "siteURL2", "http://microsoft.com", -1, 
			false); // should fail (invalid attr name)
		
		// list items
		this.testListItems(lCollectionName, 0, 1, 0, 1);
		this.testListItems(lCollectionName, 5, 1, -1, 
			0); // should fail (index out of bound)
		this.testListItems(lCollectionName, 0, -1, -1, 
			0); // should fail (expected length > 0)
		
		// remove item
		this.testExistsItem(lCollectionName, "rsantamaria", true);
		
		// remove item
		this.testRemoveItem(lCollectionName, "rsantamaria", 0);
		this.testGetItemLogs(lCollectionName, "rsantamaria", 0, 10, -1, 2); //should fail (item not exists)
		this.testExistsItem(lCollectionName, "rsantamaria", false);
		this.testRemoveItem(lCollectionName, "rsantamaria", 
			-1); // should fail (item not exists)
		
		// save item
		this.testSaveItem(lCollectionName, {
			name: "Rolando SM",
			mailAddress: "rsantamaria@jwebsocket.org",
			siteURL: "http://jwebsocket.org",
			comment: "jWebSocket developer",
			image: "base64 image content",
			sex: true
		}, -1); // should fail (missing PK)
		// 
		// save item
		this.testSaveItem(lCollectionName, {
			name: "Rolando SM",
			mailAddress: "rsantamaria@jwebsocket.org",
			siteURL: "http://jwebsocket.org",
			comment: "jWebSocket developer",
			image: "base64 image content",
			sex: true,
			arbitraryAttr: "bla bla bla"
		}, -1); // should fail (missing attribute definition)
		
		this.testAuthorizeCollection(lCollectionName, lPwd, -1); // authorized already
		this.testAuthorizeCollection(lCollectionName, "wrong password", -1);
		
		// save item
		this.testSaveItem(lCollectionName, {
			name: "Rolando SM",
			mailAddress: "rsantamaria@jwebsocket.org",
			siteURL: "http://jwebsocket.org",
			comment: "jWebSocket developer",
			image: "base64 image content",
			username: "rsantamaria",
			sex: true
		}, 0);
		
		// clear
		this.testClearCollection(lCollectionName, lPwd, 0);
		this.testClearCollection(lCollectionName, "wrong password", -1);
		
		// change config
		this.testEditCollection(lCollectionName, lPwd, "abc", "abc", true, 0);
		this.testEditCollection(lCollectionName, lPwd, "abc", "abc", true, -1);
		this.testEditCollection(lCollectionName, "abc", lPwd, lPwd, false, 0);
		
		// register definition
		this.testRegisterItemDef("mixedValues", "id", {
			integer: "integer",
			string: "string" + JSON.stringify({
				min_length: 2,
				max_length: 140,
				required: true,
				reg_exp: "^[a-z]+[a-z]*"
			}),
			mail: "string" + JSON.stringify({
				mail: true
			}),
			bool: "boolean",
			doublee: "double"+ JSON.stringify({
				"between": [1,4],
				"in": [2,5]
			}),
			longe: "long"
		}, 0);
		this.testCreateCollection("mymixed", "mixedValues", "123", "123", false, 1, 0);
		this.testSubscribeCollection("mymixed", "123", 0);
		this.testAuthorizeCollection("mymixed", "123", 0);
		this.testSaveItem("mymixed", {
			string: "abc",
			mail:"rsantamaria@jwebsocket.org",
			integer: 654,
			bool: false,
			doublee: 2,
			longe: 12313123123131
		}, 0);
		
		// find random
		this.testFindItemRandom("mymixed", 0, true);
		
		this.testSaveItem("mymixed", {
			string: "abccv",
			mail:"rsantamaria22@jwebsocket.org",
			integer: 654,
			bool: false,
			doublee: 2,
			longe: 5645
		}, -1); // should fail, exceeds collection capacity
		
		//list definitions
		this.testListItemDef(2);

		// exists definition
		this.testExistsItemDef("mixedValues", true);
		
		// find item def
		this.testFindItemDef("mixedValues", true);

		// remove definition
		this.testRemoveItemDef("mixedValues", -1); // should fail (item definition in use)
		this.testClearCollection("mymixed", lPwd, 0);
		
		// remove collection
		this.testRemoveCollection(lCollectionName, "wrong password", -1);
		this.testRemoveCollection(lCollectionName, lPwd, 0);
		this.testRemoveCollection("mymixed", lPwd, 0);
		this.testRemoveCollection(lCollectionName + "1", lPwd, 0);
		
		this.testRemoveItemDef("mixedValues", 0); 
		this.testExistsItemDef("mixedValues", false);
		this.testRemoveItemDef("mixedValues", -1); // should fail (def not found)
		
		// list definitions
		this.testListItemDef(1);
	}
};