
//	---------------------------------------------------------------------------
//	jWebSocket Enterprise ItemStorage Client Plug-In
//	(C) Copyright 2012-2013 Innotrade GmbH, Herzogenrath Germany
//	Author: Rolando Santamaria Maso
//	---------------------------------------------------------------------------
jws.ItemStoragePlugIn={NS:jws.NS_BASE+".plugins.itemstorage",getISLogs:function(aU,l,J){var C=this.checkConnected();if(0==C.code){
var G={ns:jws.ItemStoragePlugIn.NS,type:"getLogs",eType:aU,collectionName:l};if(!J){J={};}if(J.itemPK){G.itemPK=J.itemPK;}if(
J.offset){G.offset=J.offset;}if(J.length){G.length=J.length;}this.sendToken(G,J);}return C;},findItemRandom:function(l,J){var C=
this.checkConnected();if(0==C.code){var G={ns:jws.ItemStoragePlugIn.NS,type:"findItemRandom",collectionName:l};this.sendToken(G,J);}
return C;},findItems:function(l,bh,aY,J){var C=this.checkConnected();if(0==C.code){var G={ns:jws.ItemStoragePlugIn.NS,type:
"findItems",collectionName:l,attrName:bh,attrValue:aY,offset:J["offset"]||0,length:J["length"]||10};this.sendToken(G,J);}return C;},
registerItemDefinition:function(bj,aN,bw,J){var C=this.checkConnected();if(0==C.code){var G={ns:jws.ItemStoragePlugIn.NS,type:
"registerDefinition",itemType:bj,itemPK:aN,attributes:bw};this.sendToken(G,J);}return C;},removeItemDefinition:function(bj,J){var C=
this.checkConnected();if(0==C.code){var G={ns:jws.ItemStoragePlugIn.NS,type:"removeDefinition",itemType:bj};this.sendToken(G,J);}
return C;},clearItemStorageDB:function(J){var C=this.checkConnected();if(0==C.code){var G={ns:jws.ItemStoragePlugIn.NS,type:
"clearDatabase"};this.sendToken(G,J);}return C;}};jws.oop.addPlugIn(jws.jWebSocketTokenClient,jws.ItemStoragePlugIn); 