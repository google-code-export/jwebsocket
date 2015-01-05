
//	---------------------------------------------------------------------------
//	jWebSocket JavaScript/Browser Client (Community Edition, CE)
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
if(window.MozWebSocket){window.WebSocket=window.MozWebSocket;}var jws={VERSION:"1.0.0 RC3 (build 50105)",NS_BASE:"org.jwebsocket",
NS_SYSTEM:"org.jwebsocket.plugins.system",MSG_WS_NOT_SUPPORTED:
"Unfortunately your browser does neither natively support WebSockets\n"+"nor you have the Adobe Flash-PlugIn 10+ installed.\n"+
"Please download the last recent Adobe Flash Player at http://get.adobe.com/flashplayer, "+
"or use a native WebSocket compliant browser.",CUR_TOKEN_ID:0,JWS_SERVER_SCHEMA:"ws",JWS_SERVER_SSL_SCHEMA:"wss",JWS_SERVER_HOST:(
self.location.hostname?self.location.hostname:"127.0.0.1"),JWS_SERVER_PORT:8787,JWS_SERVER_SSL_PORT:9797,JWS_SERVER_CONTEXT:
"/jWebSocket",JWS_SERVER_SERVLET:"/jWebSocket",JWS_SERVER_URL:"ws://"+(self.location.hostname?self.location.hostname:"127.0.0.1")+
":8787/jWebSocket/jWebSocket",CONNECTING:0,OPEN:1,CLOSING:2,CLOSED:3,RECONNECTING:1000,OPEN_TIMED_OUT:1001,RO_OFF:{autoReconnect:
false,reconnectDelay: -1},RO_ON:{autoReconnect:true,reconnectDelay:3000},WS_SUBPROT_JSON:"org.jwebsocket.json",WS_SUBPROT_XML:
"org.jwebsocket.xml",WS_SUBPROT_CSV:"org.jwebsocket.csv",WS_SUBPROT_CUSTOM:"org.jwebsocket.text",WS_SUBPROT_TEXT:
"org.jwebsocket.text",WS_SUBPROT_BINARY:"org.jwebsocket.binary",SCOPE_PRIVATE:"private",SCOPE_PUBLIC:"public",DEF_RESP_TIMEOUT:
30000,BT_UNKNOWN:0,BT_FIREFOX:1,BT_NETSCAPE:2,BT_OPERA:3,BT_IEXPLORER:4,BT_SAFARI:5,BT_CHROME:6,BROWSER_NAMES:["Unknown","Firefox",
"Netscape","Opera","Internet Explorer","Safari","Chrome"],GUEST_USER_LOGINNAME:"guest",GUEST_USER_PASSWORD:"guest",
DEMO_ROOT_LOGINNAME:"root",DEMO_ROOT_PASSWORD:"root",$:function(aT){return document.getElementById(aT);},getServerURL:function(fm,
bM,bv,dL,dc){var el=fm+"://"+bM+(bv?":"+bv:"");if(dL&&dL.length>0){el+=dL;if(dc&&dc.length>0){el+=dc;}}return el;},getWebAppURL:
function(dL,dc){var is=dL||self.location.pathname;var jF=dc||jws.JWS_SERVER_SERVLET;return jws.getServerURL("https"===
self.location.protocol?"wss":"ws",self.location.hostname,self.location.port,is,jF);},getDefaultServerURL:function(){return(
this.getServerURL(jws.JWS_SERVER_SCHEMA,jws.JWS_SERVER_HOST,jws.JWS_SERVER_PORT,jws.JWS_SERVER_CONTEXT,jws.JWS_SERVER_SERVLET));},
getDefaultServerCometURL:function(){return this.getDefaultServerURL()+"Comet";},getDefaultSSLServerURL:function(){return(
this.getServerURL(jws.JWS_SERVER_SSL_SCHEMA,jws.JWS_SERVER_HOST,jws.JWS_SERVER_SSL_PORT,jws.JWS_SERVER_CONTEXT,
jws.JWS_SERVER_SERVLET));},getDefaultSSLServerCometURL:function(){return this.getDefaultSSLServerURL()+"Comet";},getAutoServerURL:
function(){var iL=location.protocol&&location.protocol.indexOf("https")>=0;return(this.getServerURL((iL?jws.JWS_SERVER_SSL_SCHEMA:
jws.JWS_SERVER_SCHEMA),jws.JWS_SERVER_HOST,(iL?jws.JWS_SERVER_SSL_PORT:jws.JWS_SERVER_PORT),jws.JWS_SERVER_CONTEXT,
jws.JWS_SERVER_SERVLET));},browserSupportsWebSockets:function(){return(window.WebSocket!==null&&window.WebSocket!==undefined);},
enableCometSupportForWebSockets:function(){window.WebSocket=XHRWebSocket;},kQ:function(){window.WebSocket=ka;},
browserSupportsNativeWebSockets:(function(){if(window.WEB_SOCKET_FORCE_FLASH){return false;}return(window.WebSocket!==null&&
window.WebSocket!==undefined);})(),browserSupportsJSON:function(){return(window.JSON!==null&&window.JSON!==undefined);},
browserSupportsNativeJSON:(function(){return(window.JSON!==null&&window.JSON!==undefined);})(),browserSupportsWebWorkers:(function()
{return(window.Worker!==null&&window.Worker!==undefined);})(),getOptions:function(ax,gx){ax=(ax?ax:{});if(gx){for(var dR in gx){if(
undefined===ax[dR]){ax[dR]=gx[dR];}}}return ax;},loadScript:function(dr,ax){if(!ax){ax={};}var gp=document.getElementsByTagName(
"head")[0];bi=document.createElement("script");bi.type="text/javascript";if(ax.id){bi.id=ax.id;}gp.appendChild(bi);if(ax.OnSuccess){
bi.onload=ax.OnSuccess;}if(ax.OnFailure){bi.onerror=ax.OnFailure;}bi.src=dr;},runAsThread:function(ax){if(!
this.browserSupportsWebWorkers){return{code: -1,msg:"Browser does not (yet) support WebWorkers."};}if(!ax){ax={};}var dv=null;
var dG=null;var dw=jws.SCRIPT_PATH+"jwsWorker.js";var bu=null;var bC=[];if(ax.OnMessage&&"function"===typeof ax.OnMessage){dv=
ax.OnMessage;}if(ax.OnError&&"function"===typeof ax.OnError){dG=ax.OnError;}if(ax.file&&"String"===typeof ax.file){dw=ax.file;}if(
ax.method&&"function"===typeof ax.method){bu=ax.method;}if(ax.args){bC=ax.args;}var dJ=this;if(!jws.worker){jws.worker=new Worker(
dw);jws.worker.onmessage=function(cz){if(null!==dv){dv.call(dJ,{data:cz.data});}};jws.worker.onerror=function(cz){if(null!==dG){
dG.call(dJ,{message:cz.message});}};}jws.worker.postMessage({method:bu.toString(),args:bC});return{code:0,msg:"ok",worker:
jws.worker};},SCRIPT_PATH:(function(){var bf=document.getElementsByTagName("script");for(var db=0,dB=bf.length;db<dB;db++){var bi=
bf[db];var ad=bi.src;if(!ad){ad=bi.getAttribute("src");}if(ad){var bg=ad.lastIndexOf("jWebSocket");if(bg>0){return ad.substr(0,bg);}
}}return null;})(),isIE:(function(){var aS=navigator.userAgent;var bJ=aS.indexOf("MSIE");return(bJ>=0);})(),getBrowserName:function(
){return this.eJ;},getBrowserVersion:function(){return this.fd;},getBrowserVersionString:function(){return this.dO;},isFirefox:
function(){return this.fh;},isOpera:function(){return this.eg;},isChrome:function(){return this.fL;},isIExplorer:function(){
return this.dZ;},isIE_LE6:function(){return(this.isIExplorer()&&this.getBrowserVersion()<7);},isIE_LE7:function(){return(
this.isIExplorer()&&this.getBrowserVersion()<8);},isIE_GE8:function(){return(this.isIExplorer()&&this.getBrowserVersion()>=8);},
isSafari:function(){return this.eo;},isNetscape:function(){return this.fj;},isPocketIE:function(){return this.hw;},console:{eZ:
false,fy:2,iY:512,ALL:0,DEBUG:1,INFO:2,WARN:3,ERROR:4,FATAL:5,isDebugEnabled:function(){return(window.console&&jws.console.eZ&&
jws.console.fy<=jws.console.DEBUG);},isInfoEnabled:function(){return(window.console&&jws.console.eZ&&jws.console.fy<=
jws.console.INFO);},log:function(cW){if(window.console&&jws.console.eZ){console.log(cW);}},debug:function(cW){if(window.console&&
jws.console.eZ&&jws.console.fy<=jws.console.DEBUG){if(console.debug){console.debug(cW);}else{console.log("[debug]: "+cW);}}},info:
function(cW){if(window.console&&jws.console.eZ&&jws.console.fy<=jws.console.INFO){if(console.info){console.info(cW);}else{
console.log("[info]: "+cW);}}},warn:function(cW){if(window.console&&jws.console.eZ&&jws.console.fy<=jws.console.WARN){if(
console.warn){console.warn(cW);}else{console.log("[warn]: "+cW);}}},error:function(cW){if(window.console&&jws.console.eZ&&
jws.console.fy<=jws.console.ERROR){if(console.error){console.error(cW);}else{console.log("[error]: "+cW);}}},fatal:function(cW){if(
window.console&&jws.console.eZ&&jws.console.fy<=jws.console.FATAL){if(console.fatal){console.fatal(cW);}else{console.log(
"[fatal]: "+cW);}}},getMaxLogLineLen:function(){return jws.console.iY;},getLevel:function(){return jws.console.fy;},setLevel:
function(fo){jws.console.fy=fo;},isActive:function(){return jws.console.eZ;},setActive:function(eq){jws.console.eZ=eq;}}};(function(
){jws.eJ="unknown";jws.eT=jws.BT_UNKNOWN;jws.fd=undefined;jws.dZ=false;jws.fh=false;jws.fj=false;jws.eg=false;jws.eo=false;jws.fL=
false;var fN=navigator.userAgent;jws.fL=fN.indexOf("Chrome")>=0;if(jws.fL){jws.eT=jws.BT_CHROME;}else{jws.eo=fN.indexOf("Safari")>=
0;if(jws.eo){jws.eT=jws.BT_SAFARI;}else{jws.fj=fN.indexOf("Netscape")>=0;if(jws.fj){jws.eT=jws.BT_NETSCAPE;}else{jws.eg="Opera"===
navigator.appName;if(jws.eg){jws.eT=jws.BT_OPERA;}else{jws.hw="Microsoft Pocket Internet Explorer"===navigator.appName;if(jws.hw){
ws.eT=jws.BT_IEXPLORER;}else{jws.dZ="Microsoft Internet Explorer"===navigator.appName|| ! !fN.match(/Trident.*rv[ :]./);if(jws.dZ){
jws.eT=jws.BT_IEXPLORER;}else{jws.fh="Netscape"===navigator.appName;if(jws.fh){jws.eT=jws.BT_FIREFOX;}}}}}}}var p,db;var aK;var fi;
var cf;if(jws.dZ){jws.eJ=jws.BROWSER_NAMES[jws.BT_IEXPLORER];cf=fN.match(/MSIE.*/i);if(cf){aK=cf[0].substr(5);p=aK.indexOf(";");
jws.dO=p>0?aK.substr(0,p):aK;jws.fd=parseFloat(jws.dO);}else{cf=fN.match(/Trident.*rv[ :]./);if(cf){p=cf[0].indexOf(";");if(p>0){
jws.dO=cf[0].substr(0,p);}else{jws.dO="Trident";}cf=fN.match(/rv[ :].*/i);if(cf){p=cf[0].indexOf(")");jws.fd=parseFloat(p>=3?
cf[0].substr(3,p-3):cf[0].substr(3,4));}}}}else if(jws.fh){jws.eJ=jws.BROWSER_NAMES[jws.BT_FIREFOX];cf=fN.match(/Firefox\/.*/i);if(
cf){aK=cf[0].substr(8);p=aK.indexOf(" ");if(p>0){jws.dO=aK.substring(0,p);}else{jws.dO=aK;}fi=0;db=0;while(db<aK.length){if('.'===
aK.charAt(db)){fi++;}if(fi>=2){break;}db++;}aK=aK.substring(0,db);jws.fd=parseFloat(aK);}}else if(jws.fj){jws.eJ=
jws.BROWSER_NAMES[jws.BT_NETSCAPE];cf=fN.match(/Netscape\/.*/i);if(cf){aK=cf[0].substr(9);p=aK.indexOf(" ");if(p>0){jws.dO=
aK.substring(0,p);}else{jws.dO=aK;}fi=0;db=0;while(db<aK.length){if('.'===aK.charAt(db)){fi++;}if(fi>=2){break;}db++;}aK=
aK.substring(0,db);jws.fd=parseFloat(aK);}}else if(jws.eg){jws.eJ=jws.BROWSER_NAMES[jws.BT_OPERA];cf=fN.match(/Opera\/.*/i);if(cf){
aK=cf[0].substr(6);p=aK.indexOf(" ");jws.dO=p>0?aK.substr(0,p):aK;jws.fd=parseFloat(aK);cf=fN.match(/Version\/.*/i);aK=cf[0].substr(
8);if(cf){p=aK.indexOf(" ");jws.dO=(p>0?aK.substr(0,p):aK)+"/"+jws.dO;jws.fd=parseFloat(aK);}}}else if(jws.fL){jws.eJ=
jws.BROWSER_NAMES[jws.BT_CHROME];cf=fN.match(/Chrome\/.*/i);if(cf){aK=cf[0].substr(7);p=aK.indexOf(" ");jws.dO=p>0?aK.substr(0,p):
aK;jws.fd=parseFloat(aK);}}else if(jws.eo){jws.eJ=jws.BROWSER_NAMES[jws.BT_SAFARI];cf=fN.match(/Version\/.*/i);if(cf){aK=
cf[0].substr(8);p=aK.indexOf(" ");jws.dO=p>0?aK.substr(0,p):aK;fi=0;db=0;while(db<aK.length){if('.'===aK.charAt(db)){fi++;}if(fi>=2)
{break;}db++;}aK=aK.substring(0,db);jws.fd=parseFloat(aK);cf=fN.match(/Safari\/.*/i);if(cf){aK="."+cf[0].substr(7);p=aK.indexOf(" ")
;jws.dO+=p>0?aK.substr(0,p):aK;}}}}());jws.events={addEventListener:(jws.isIE?function(as,cz,aA){as.attachEvent("on"+cz,aA);}:
function(as,cz,aA){as.addEventListener(cz,aA,false);}),removeEventListener:(jws.isIE?function(as,cz,aA){as.detachEvent("on"+cz,aA);}
:function(as,cz,aA){as.removeEventListener(cz,aA,false);}),getTarget:(jws.isIE?function(cz){return cz.srcElement;}:function(cz){
return cz.target;}),preventDefault:(jws.isIE?function(cz){cz=window.event;if(cz){cz.returnValue=false;}}:function(cz){
return cz.preventDefault();}),stopEvent:(jws.isIE?function(cz){if(cz&&cz.preventDefault){return cz.preventDefault();}}:function(cz){
return cz.stopPropagation();})};
/**
 * Copyright (c) 2012 Florian H., https://github.com/js-coder
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
!function(document,undefined){var cookie=function(){return cookie.get.apply(cookie,arguments);};var utils=cookie.utils={isArray:Array.isArray||function(value){return Object.prototype.toString.call(value)==='[object Array]';},isPlainObject:function(value){return! !value&&Object.prototype.toString.call(value)==='[object Object]';},toArray:function(value){return Array.prototype.slice.call(value);},getKeys:Object.keys||function(obj){var keys=[],key='';for(key in obj){if(obj.hasOwnProperty(key))keys.push(key);}return keys;},escape:function(value){return String(value).replace(/[,;"\\=\s%]/g,function(character){return encodeURIComponent(character);});},retrieve:function(value,fallback){return value==null?fallback:value;}};cookie.defaults={};cookie.expiresMultiplier=60*60*24;cookie.set=function(key,value,options){if(utils.isPlainObject(key)){for(var k in key){if(key.hasOwnProperty(k))this.set(k,key[k],value);}}else{options=utils.isPlainObject(options)?options:{expires:options};var expires=options.expires!==undefined?options.expires:(this.defaults.expires||''),expiresType=typeof(expires);if(expiresType==='string'&&expires!=='')expires=new Date(expires);else if(expiresType==='number')expires=new Date(+new Date+1000*this.expiresMultiplier*expires);if(expires!==''&&'toGMTString'in expires)expires=';expires='+expires.toGMTString();var path=options.path||this.defaults.path;path=path?';path='+path:'';var domain=options.domain||this.defaults.domain;domain=domain?';domain='+domain:'';var secure=options.secure||this.defaults.secure?';secure':'';document.cookie=utils.escape(key)+'='+utils.escape(value)+expires+path+domain+secure;}return this;};cookie.remove=function(keys){keys=utils.isArray(keys)?keys:utils.toArray(arguments);for(var i=0,l=keys.length;i<l;i++){this.set(keys[i],'',-1);}return this;};cookie.empty=function(){return this.remove(utils.getKeys(this.all()));};cookie.get=function(keys,fallback){fallback=fallback||undefined;var cookies=this.all();if(utils.isArray(keys)){var result={};for(var i=0,l=keys.length;i<l;i++){var value=keys[i];result[value]=utils.retrieve(cookies[value],fallback);}return result;}else return utils.retrieve(cookies[keys],fallback);};cookie.all=function(){if(document.cookie==='')return{};var cookies=document.cookie.split('; '),result={};for(var i=0,l=cookies.length;i<l;i++){var item=cookies[i].split('=');result[decodeURIComponent(item[0])]=decodeURIComponent(item[1]);}return result;};cookie.enabled=function(){if(navigator.cookieEnabled)return true;var ret=cookie.set('_','_').get('_')==='_';cookie.remove('_');return ret;};window.cookie=cookie;}(document);

/*
 * A JavaScript implementation of the RSA Data Security, Inc. MD5 Message
 * Digest Algorithm, as defined in RFC 1321.
 * Version 2.2 Copyright (C) Paul Johnston 1999 - 2009
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for more info.
 * For full source please refer to: md5.js
 */
var hexcase=0;var b64pad="";function hex_md5(s){return rstr2hex(rstr_md5(str2rstr_utf8(s)));};function b64_md5(s){return rstr2b64(rstr_md5(str2rstr_utf8(s)));};function any_md5(s,e){return rstr2any(rstr_md5(str2rstr_utf8(s)),e);};function hex_hmac_md5(k,d){return rstr2hex(rstr_hmac_md5(str2rstr_utf8(k),str2rstr_utf8(d)));};function b64_hmac_md5(k,d){return rstr2b64(rstr_hmac_md5(str2rstr_utf8(k),str2rstr_utf8(d)));};function any_hmac_md5(k,d,e){return rstr2any(rstr_hmac_md5(str2rstr_utf8(k),str2rstr_utf8(d)),e);};function md5_vm_test(){return hex_md5("abc").toLowerCase()=="900150983cd24fb0d6963f7d28e17f72";};function rstr_md5(s){return binl2rstr(binl_md5(rstr2binl(s),s.length*8));};function rstr_hmac_md5(key,data){var bkey=rstr2binl(key);if(bkey.length>16)bkey=binl_md5(bkey,key.length*8);var ipad=Array(16),opad=Array(16);for(var i=0;i<16;i++){ipad[i]=bkey[i]^0x36363636;opad[i]=bkey[i]^0x5C5C5C5C;}var hash=binl_md5(ipad.concat(rstr2binl(data)),512+data.length*8);return binl2rstr(binl_md5(opad.concat(hash),512+128));};function rstr2hex(input){try{hexcase}catch(e){hexcase=0;}var hex_tab=hexcase?"0123456789ABCDEF":"0123456789abcdef";var output="";var x;for(var i=0;i<input.length;i++){x=input.charCodeAt(i);output+=hex_tab.charAt((x>>>4)&0x0F)+hex_tab.charAt(x&0x0F);}return output;};function rstr2b64(input){try{b64pad}catch(e){b64pad='';}var tab="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";var output="";var len=input.length;for(var i=0;i<len;i+=3){var triplet=(input.charCodeAt(i)<<16)|(i+1<len?input.charCodeAt(i+1)<<8:0)|(i+2<len?input.charCodeAt(i+2):0);for(var j=0;j<4;j++){if(i*8+j*6>input.length*8)output+=b64pad;else output+=tab.charAt((triplet>>>6*(3-j))&0x3F);}}return output;};function rstr2any(input,encoding){var divisor=encoding.length;var i,j,q,x,quotient;var dividend=Array(Math.ceil(input.length/2));for(i=0;i<dividend.length;i++){dividend[i]=(input.charCodeAt(i*2)<<8)|input.charCodeAt(i*2+1);}var full_length=Math.ceil(input.length*8/(Math.log(encoding.length)/Math.log(2)));var remainders=Array(full_length);for(j=0;j<full_length;j++){quotient=Array();x=0;for(i=0;i<dividend.length;i++){x=(x<<16)+dividend[i];q=Math.floor(x/divisor);x-=q*divisor;if(quotient.length>0||q>0)quotient[quotient.length]=q;}remainders[j]=x;dividend=quotient;}var output="";for(i=remainders.length-1;i>=0;i--)output+=encoding.charAt(remainders[i]);return output;};function str2rstr_utf8(input){var output="";var i= -1;var x,y;while(++i<input.length){x=input.charCodeAt(i);y=i+1<input.length?input.charCodeAt(i+1):0;if(0xD800<=x&&x<=0xDBFF&&0xDC00<=y&&y<=0xDFFF){x=0x10000+((x&0x03FF)<<10)+(y&0x03FF);i++;}if(x<=0x7F)output+=String.fromCharCode(x);else if(x<=0x7FF)output+=String.fromCharCode(0xC0|((x>>>6)&0x1F),0x80|(x&0x3F));else if(x<=0xFFFF)output+=String.fromCharCode(0xE0|((x>>>12)&0x0F),0x80|((x>>>6)&0x3F),0x80|(x&0x3F));else if(x<=0x1FFFFF)output+=String.fromCharCode(0xF0|((x>>>18)&0x07),0x80|((x>>>12)&0x3F),0x80|((x>>>6)&0x3F),0x80|(x&0x3F));}return output;};function str2rstr_utf16le(input){var output="";for(var i=0;i<input.length;i++)output+=String.fromCharCode(input.charCodeAt(i)&0xFF,(input.charCodeAt(i)>>>8)&0xFF);return output;};function str2rstr_utf16be(input){var output="";for(var i=0;i<input.length;i++)output+=String.fromCharCode((input.charCodeAt(i)>>>8)&0xFF,input.charCodeAt(i)&0xFF);return output;};function rstr2binl(input){var output=Array(input.length>>2);for(var i=0;i<output.length;i++)output[i]=0;for(var i=0;i<input.length*8;i+=8)output[i>>5]|=(input.charCodeAt(i/8)&0xFF)<<(i%32);return output;};function binl2rstr(input){var output="";for(var i=0;i<input.length*32;i+=8)output+=String.fromCharCode((input[i>>5]>>>(i%32))&0xFF);return output;};function binl_md5(x,len){x[len>>5]|=0x80<<((len)%32);x[(((len+64)>>>9)<<4)+14]=len;var a=1732584193;var b= -271733879;var c= -1732584194;var d=271733878;for(var i=0;i<x.length;i+=16){var olda=a;var oldb=b;var oldc=c;var oldd=d;a=md5_ff(a,b,c,d,x[i+0],7,-680876936);d=md5_ff(d,a,b,c,x[i+1],12,-389564586);c=md5_ff(c,d,a,b,x[i+2],17,606105819);b=md5_ff(b,c,d,a,x[i+3],22,-1044525330);a=md5_ff(a,b,c,d,x[i+4],7,-176418897);d=md5_ff(d,a,b,c,x[i+5],12,1200080426);c=md5_ff(c,d,a,b,x[i+6],17,-1473231341);b=md5_ff(b,c,d,a,x[i+7],22,-45705983);a=md5_ff(a,b,c,d,x[i+8],7,1770035416);d=md5_ff(d,a,b,c,x[i+9],12,-1958414417);c=md5_ff(c,d,a,b,x[i+10],17,-42063);b=md5_ff(b,c,d,a,x[i+11],22,-1990404162);a=md5_ff(a,b,c,d,x[i+12],7,1804603682);d=md5_ff(d,a,b,c,x[i+13],12,-40341101);c=md5_ff(c,d,a,b,x[i+14],17,-1502002290);b=md5_ff(b,c,d,a,x[i+15],22,1236535329);a=md5_gg(a,b,c,d,x[i+1],5,-165796510);d=md5_gg(d,a,b,c,x[i+6],9,-1069501632);c=md5_gg(c,d,a,b,x[i+11],14,643717713);b=md5_gg(b,c,d,a,x[i+0],20,-373897302);a=md5_gg(a,b,c,d,x[i+5],5,-701558691);d=md5_gg(d,a,b,c,x[i+10],9,38016083);c=md5_gg(c,d,a,b,x[i+15],14,-660478335);b=md5_gg(b,c,d,a,x[i+4],20,-405537848);a=md5_gg(a,b,c,d,x[i+9],5,568446438);d=md5_gg(d,a,b,c,x[i+14],9,-1019803690);c=md5_gg(c,d,a,b,x[i+3],14,-187363961);b=md5_gg(b,c,d,a,x[i+8],20,1163531501);a=md5_gg(a,b,c,d,x[i+13],5,-1444681467);d=md5_gg(d,a,b,c,x[i+2],9,-51403784);c=md5_gg(c,d,a,b,x[i+7],14,1735328473);b=md5_gg(b,c,d,a,x[i+12],20,-1926607734);a=md5_hh(a,b,c,d,x[i+5],4,-378558);d=md5_hh(d,a,b,c,x[i+8],11,-2022574463);c=md5_hh(c,d,a,b,x[i+11],16,1839030562);b=md5_hh(b,c,d,a,x[i+14],23,-35309556);a=md5_hh(a,b,c,d,x[i+1],4,-1530992060);d=md5_hh(d,a,b,c,x[i+4],11,1272893353);c=md5_hh(c,d,a,b,x[i+7],16,-155497632);b=md5_hh(b,c,d,a,x[i+10],23,-1094730640);a=md5_hh(a,b,c,d,x[i+13],4,681279174);d=md5_hh(d,a,b,c,x[i+0],11,-358537222);c=md5_hh(c,d,a,b,x[i+3],16,-722521979);b=md5_hh(b,c,d,a,x[i+6],23,76029189);a=md5_hh(a,b,c,d,x[i+9],4,-640364487);d=md5_hh(d,a,b,c,x[i+12],11,-421815835);c=md5_hh(c,d,a,b,x[i+15],16,530742520);b=md5_hh(b,c,d,a,x[i+2],23,-995338651);a=md5_ii(a,b,c,d,x[i+0],6,-198630844);d=md5_ii(d,a,b,c,x[i+7],10,1126891415);c=md5_ii(c,d,a,b,x[i+14],15,-1416354905);b=md5_ii(b,c,d,a,x[i+5],21,-57434055);a=md5_ii(a,b,c,d,x[i+12],6,1700485571);d=md5_ii(d,a,b,c,x[i+3],10,-1894986606);c=md5_ii(c,d,a,b,x[i+10],15,-1051523);b=md5_ii(b,c,d,a,x[i+1],21,-2054922799);a=md5_ii(a,b,c,d,x[i+8],6,1873313359);d=md5_ii(d,a,b,c,x[i+15],10,-30611744);c=md5_ii(c,d,a,b,x[i+6],15,-1560198380);b=md5_ii(b,c,d,a,x[i+13],21,1309151649);a=md5_ii(a,b,c,d,x[i+4],6,-145523070);d=md5_ii(d,a,b,c,x[i+11],10,-1120210379);c=md5_ii(c,d,a,b,x[i+2],15,718787259);b=md5_ii(b,c,d,a,x[i+9],21,-343485551);a=safe_add(a,olda);b=safe_add(b,oldb);c=safe_add(c,oldc);d=safe_add(d,oldd);}return Array(a,b,c,d);};function md5_cmn(q,a,b,x,s,t){return safe_add(bit_rol(safe_add(safe_add(a,q),safe_add(x,t)),s),b);};function md5_ff(a,b,c,d,x,s,t){return md5_cmn((b&c)|((~b)&d),a,b,x,s,t);};function md5_gg(a,b,c,d,x,s,t){return md5_cmn((b&d)|(c&(~d)),a,b,x,s,t);};function md5_hh(a,b,c,d,x,s,t){return md5_cmn(b^c^d,a,b,x,s,t);};function md5_ii(a,b,c,d,x,s,t){return md5_cmn(c^(b|(~d)),a,b,x,s,t);};function safe_add(x,y){var lsw=(x&0xFFFF)+(y&0xFFFF);var msw=(x>>16)+(y>>16)+(lsw>>16);return(msw<<16)|(lsw&0xFFFF);};function bit_rol(num,cnt){return(num<<cnt)|(num>>>(32-cnt));}
;if(!('lastIndexOf'in Array.prototype)){Array.prototype.lastIndexOf=function(iS,ef){if(ef===undefined){ef=this.length-1;}if(ef<0){
ef+=this.length;}if(ef>this.length-1){ef=this.length-1;}for(ef++;ef-- >0;){if(ef in this&&this[ef]===iS){return ef;}}return-1;};}
String.prototype.getBytes=function(){var hs=[];for(var cI=0;cI<this.length;++cI){hs.push(this.charCodeAt(cI));}return hs;};
jws.tools={lg:function(jH,lq,lc){lq=lq||'';lc=lc||512;var kl=atob(jH);var lh=[];for(var ja=0;ja<kl.length;ja+=lc){var jO=kl.slice(
ja,ja+lc);var kA=new Array(jO.length);for(var cI=0;cI<jO.length;cI++){kA[cI]=jO.charCodeAt(cI);}var kP=new kj(kA);lh.push(kP);}
var kC=new Blob(lh,{type:lq});return kC;},str2bytes:function(gL){var hs=[];for(var cI=0;cI<gL.length;cI++){hs.push(gL.charCodeAt(cI)
);}return hs;},bytes2str:function(gM){var gR="";for(var cI=0;cI<gM.length;cI++){gR+=String.fromCharCode(gM[cI]);}return gR;},
getUniqueInteger:function(){if(undefined===this.gg||2147483647===this.gg){this.gg=1;}return this.gg++;},zerofill:function(ay,am){
var bj=ay.toFixed(0);if(bj.length>am){bj=bj.substring(bj.length-am);}else{while(bj.length<am){bj="0"+bj;}}return bj;},parseQuery:
function(dr){var gc={};var iO=dr.split("?");if(1===iO.length){return gc;}var gH=iO[1].split(",");for(var cI in gH){var ir=
gH[cI].split("=");gc[ir[0]]=ir[1];}return gc;},calcMD5:function(gX){return(hex_md5(gX));},escapeSQL:function(hG){if(hG&&typeof ck===
"string"){}return hG;},date2ISO:function(du){var fJ= -du.getTimezoneOffset();var dE=Math.abs(fJ);var bj=du.getUTCFullYear()+"-"+
this.zerofill(du.getUTCMonth()+1,2)+"-"+this.zerofill(du.getUTCDate(),2)+"T"+this.zerofill(du.getUTCHours(),2)+":"+this.zerofill(
du.getUTCMinutes(),2)+":"+this.zerofill(du.getUTCSeconds(),2)+"."+this.zerofill(du.getUTCMilliseconds(),3)+"Z";return bj;},ISO2Date:
function(bp,bd){var dK=new Date();dK.setUTCFullYear(bp.substr(0,4));dK.setUTCMonth(bp.substr(5,2)-1);dK.setUTCDate(bp.substr(8,2));
dK.setUTCHours(bp.substr(11,2));dK.setUTCMinutes(bp.substr(14,2));dK.setUTCSeconds(bp.substr(17,2));dK.setUTCMilliseconds(bp.substr(
20,3));return dK;},date2String:function(du){var bj=du.getUTCFullYear()+this.zerofill(du.getUTCMonth()+1,2)+this.zerofill(
du.getUTCDate(),2)+this.zerofill(du.getUTCHours(),2)+this.zerofill(du.getUTCMinutes(),2)+this.zerofill(du.getUTCSeconds(),2)+
this.zerofill(du.getUTCMilliseconds(),2);return bj;},string2Date:function(bp){var dK=new Date();dK.setUTCFullYear(bp.substr(0,4));
dK.setUTCMonth(bp.substr(4,2)-1);dK.setUTCDate(bp.substr(6,2));dK.setUTCHours(bp.substr(8,2));dK.setUTCMinutes(bp.substr(10,2));
dK.setUTCSeconds(bp.substr(12,2));dK.setUTCMilliseconds(bp.substr(14,3));return dK;},generateSharedUTID:function(aR){var string=
JSON.stringify(aR);var chars=string.split('');chars.sort();return hex_md5("{"+chars.toString()+"}");},getType:function(ey){var dM=
ey;var bj=typeof dM;if("number"===bj){if((parseFloat(dM)===parseInt(dM))){bj="integer";}else{bj="double";}}else if(
Object.prototype.toString.call(dM)==="[object Array]"){bj="array";}else if(dM===null){bj="null";}return bj;},isArrayOf:function(fE,
bU){if(!Ext.isArray(fE)){return false;}for(var cI in fE){if(this.getType(fE[cI])!==bU){return false;}}return true;},setProperties:
function(ey,gl,gT){var gi=gT||"";var er=null;var eH=null;for(eH in gl){er="set"+eH.substr(0,1).toUpperCase()+eH.substr(1);if(
"function"===typeof(ey[er])){ey[er](gl[eH]);}else{ey[gi+eH]=gl[eH];}}return ey;},zip:function(gL,jj){if(!JSZip){throw new Error(
'JSZip library is missing. Class not found!');}var il=jj||false;var iH=new JSZip();iH.file("temp.zip",gL);var iI=iH.generate({
compression:"DEFLATE",base64:il});return iI;},unzip:function(gL,ic){if(!JSZip){throw new Error(
'JSZip library is missing. Class not found!');}var il=ic||false;var iH=new JSZip(gL,{base64:il});var dw=iH.file("temp.zip");
return dw.asBinary();},intersect:function(js,jb){var hV=[];if(js&&jb){for(var cI=0;cI<js.length;cI++){if(-1<jb.lastIndexOf(js[cI])){
hV.push(js[cI]);}}}return hV;},clone:function(ey){if(null===ey||"object"!==typeof(cQ)){return ey;}if("function"===typeof(
ey["clone"])){return ey.clone();}if(ey instanceof Date){return new Date(ey.getTime());}var cloneArray=function(fE){var fq=fE.length;
var hD=[];if(fq>0){for(var cI=0;cI<fq;cI++){hD[cI]=jws.tools.clone(fE[cI]);}}return hD;};var dH=new ey.constructor();for(
var dX in ey){var dM=ey[dX];if(dM instanceof Array)dH[dX]=cloneArray(dM);else{dH[dX]=jws.tools.clone(dM);}}return dH;},createUUID:
function(){var dT=[];var je="0123456789abcdef";for(var db=0;db<36;db++){dT[db]=je.substr(Math.floor(Math.random()*0x10),1);}dT[14]=
"4";dT[19]=je.substr((dT[19]&0x3)|0x8,1);dT[8]=dT[13]=dT[18]=dT[23]="-";var iC=dT.join("");return iC;}};if(!
jws.browserSupportsNativeWebSockets){
	// --- original code, please refer to swfobject.js in folder flash-bridge ---
	// SWFObject v2.2 <http://code.google.com/p/swfobject/> 
	// released under the MIT License <http://www.opensource.org/licenses/mit-license.php> 
	var swfobject=function(){var D="undefined",r="object",S="Shockwave Flash",W="ShockwaveFlash.ShockwaveFlash",q="application/x-shockwave-flash",R="SWFObjectExprInst",x="onreadystatechange",O=window,j=document,t=navigator,T=false,U=[h],o=[],N=[],I=[],l,Q,E,B,J=false,a=false,n,G,m=true,M=function(){var aa=typeof j.getElementById!=D&&typeof j.getElementsByTagName!=D&&typeof j.createElement!=D,ah=t.userAgent.toLowerCase(),Y=t.platform.toLowerCase(),ae=Y?/win/.test(Y):/win/.test(ah),ac=Y?/mac/.test(Y):/mac/.test(ah),af=/webkit/.test(ah)?parseFloat(ah.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,X=!+"\v1",ag=[0,0,0],ab=null;if(typeof t.plugins!=D&&typeof t.plugins[S]==r){ab=t.plugins[S].description;if(ab&&!(typeof t.mimeTypes!=D&&t.mimeTypes[q]&&!t.mimeTypes[q].enabledPlugin)){T=true;X=false;ab=ab.replace(/^.*\s+(\S+\s+\S+$)/,"$1");ag[0]=parseInt(ab.replace(/^(.*)\..*$/,"$1"),10);ag[1]=parseInt(ab.replace(/^.*\.(.*)\s.*$/,"$1"),10);ag[2]=/[a-zA-Z]/.test(ab)?parseInt(ab.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0}}else{if(typeof O.ActiveXObject!=D){try{var ad=new ActiveXObject(W);if(ad){ab=ad.GetVariable("$version");if(ab){X=true;ab=ab.split(" ")[1].split(",");ag=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}}catch(Z){}}}return{w3:aa,pv:ag,wk:af,ie:X,win:ae,mac:ac}}(),k=function(){if(!M.w3){return}if((typeof j.readyState!=D&&j.readyState=="complete")||(typeof j.readyState==D&&(j.getElementsByTagName("body")[0]||j.body))){f()}if(!J){if(typeof j.addEventListener!=D){j.addEventListener("DOMContentLoaded",f,false)}if(M.ie&&M.win){j.attachEvent(x,function(){if(j.readyState=="complete"){j.detachEvent(x,arguments.callee);f()}});if(O==top){(function(){if(J){return}try{j.documentElement.doScroll("left")}catch(X){setTimeout(arguments.callee,0);return}f()})()}}if(M.wk){(function(){if(J){return}if(!/loaded|complete/.test(j.readyState)){setTimeout(arguments.callee,0);return}f()})()}s(f)}}();function f(){if(J){return}try{var Z=j.getElementsByTagName("body")[0].appendChild(C("span"));Z.parentNode.removeChild(Z)}catch(aa){return}J=true;var X=U.length;for(var Y=0;Y<X;Y++){U[Y]()}}function K(X){if(J){X()}else{U[U.length]=X}}function s(Y){if(typeof O.addEventListener!=D){O.addEventListener("load",Y,false)}else{if(typeof j.addEventListener!=D){j.addEventListener("load",Y,false)}else{if(typeof O.attachEvent!=D){i(O,"onload",Y)}else{if(typeof O.onload=="function"){var X=O.onload;O.onload=function(){X();Y()}}else{O.onload=Y}}}}}function h(){if(T){V()}else{H()}}function V(){var X=j.getElementsByTagName("body")[0];var aa=C(r);aa.setAttribute("type",q);var Z=X.appendChild(aa);if(Z){var Y=0;(function(){if(typeof Z.GetVariable!=D){var ab=Z.GetVariable("$version");if(ab){ab=ab.split(" ")[1].split(",");M.pv=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}else{if(Y<10){Y++;setTimeout(arguments.callee,10);return}}X.removeChild(aa);Z=null;H()})()}else{H()}}function H(){var ag=o.length;if(ag>0){for(var af=0;af<ag;af++){var Y=o[af].id;var ab=o[af].callbackFn;var aa={success:false,id:Y};if(M.pv[0]>0){var ae=c(Y);if(ae){if(F(o[af].swfVersion)&&!(M.wk&&M.wk<312)){w(Y,true);if(ab){aa.success=true;aa.ref=z(Y);ab(aa)}}else{if(o[af].expressInstall&&A()){var ai={};ai.data=o[af].expressInstall;ai.width=ae.getAttribute("width")||"0";ai.height=ae.getAttribute("height")||"0";if(ae.getAttribute("class")){ai.styleclass=ae.getAttribute("class")}if(ae.getAttribute("align")){ai.align=ae.getAttribute("align")}var ah={};var X=ae.getElementsByTagName("param");var ac=X.length;for(var ad=0;ad<ac;ad++){if(X[ad].getAttribute("name").toLowerCase()!="movie"){ah[X[ad].getAttribute("name")]=X[ad].getAttribute("value")}}P(ai,ah,Y,ab)}else{p(ae);if(ab){ab(aa)}}}}}else{w(Y,true);if(ab){var Z=z(Y);if(Z&&typeof Z.SetVariable!=D){aa.success=true;aa.ref=Z}ab(aa)}}}}}function z(aa){var X=null;var Y=c(aa);if(Y&&Y.nodeName=="OBJECT"){if(typeof Y.SetVariable!=D){X=Y}else{var Z=Y.getElementsByTagName(r)[0];if(Z){X=Z}}}return X}function A(){return !a&&F("6.0.65")&&(M.win||M.mac)&&!(M.wk&&M.wk<312)}function P(aa,ab,X,Z){a=true;E=Z||null;B={success:false,id:X};var ae=c(X);if(ae){if(ae.nodeName=="OBJECT"){l=g(ae);Q=null}else{l=ae;Q=X}aa.id=R;if(typeof aa.width==D||(!/%$/.test(aa.width)&&parseInt(aa.width,10)<310)){aa.width="310"}if(typeof aa.height==D||(!/%$/.test(aa.height)&&parseInt(aa.height,10)<137)){aa.height="137"}j.title=j.title.slice(0,47)+" - Flash Player Installation";var ad=M.ie&&M.win?"ActiveX":"PlugIn",ac="MMredirectURL="+O.location.toString().replace(/&/g,"%26")+"&MMplayerType="+ad+"&MMdoctitle="+j.title;if(typeof ab.flashvars!=D){ab.flashvars+="&"+ac}else{ab.flashvars=ac}if(M.ie&&M.win&&ae.readyState!=4){var Y=C("div");X+="SWFObjectNew";Y.setAttribute("id",X);ae.parentNode.insertBefore(Y,ae);ae.style.display="none";(function(){if(ae.readyState==4){ae.parentNode.removeChild(ae)}else{setTimeout(arguments.callee,10)}})()}u(aa,ab,X)}}function p(Y){if(M.ie&&M.win&&Y.readyState!=4){var X=C("div");Y.parentNode.insertBefore(X,Y);X.parentNode.replaceChild(g(Y),X);Y.style.display="none";(function(){if(Y.readyState==4){Y.parentNode.removeChild(Y)}else{setTimeout(arguments.callee,10)}})()}else{Y.parentNode.replaceChild(g(Y),Y)}}function g(ab){var aa=C("div");if(M.win&&M.ie){aa.innerHTML=ab.innerHTML}else{var Y=ab.getElementsByTagName(r)[0];if(Y){var ad=Y.childNodes;if(ad){var X=ad.length;for(var Z=0;Z<X;Z++){if(!(ad[Z].nodeType==1&&ad[Z].nodeName=="PARAM")&&!(ad[Z].nodeType==8)){aa.appendChild(ad[Z].cloneNode(true))}}}}}return aa}function u(ai,ag,Y){var X,aa=c(Y);if(M.wk&&M.wk<312){return X}if(aa){if(typeof ai.id==D){ai.id=Y}if(M.ie&&M.win){var ah="";for(var ae in ai){if(ai[ae]!=Object.prototype[ae]){if(ae.toLowerCase()=="data"){ag.movie=ai[ae]}else{if(ae.toLowerCase()=="styleclass"){ah+=' class="'+ai[ae]+'"'}else{if(ae.toLowerCase()!="classid"){ah+=" "+ae+'="'+ai[ae]+'"'}}}}}var af="";for(var ad in ag){if(ag[ad]!=Object.prototype[ad]){af+='<param name="'+ad+'" value="'+ag[ad]+'" />'}}aa.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+ah+">"+af+"</object>";N[N.length]=ai.id;X=c(ai.id)}else{var Z=C(r);Z.setAttribute("type",q);for(var ac in ai){if(ai[ac]!=Object.prototype[ac]){if(ac.toLowerCase()=="styleclass"){Z.setAttribute("class",ai[ac])}else{if(ac.toLowerCase()!="classid"){Z.setAttribute(ac,ai[ac])}}}}for(var ab in ag){if(ag[ab]!=Object.prototype[ab]&&ab.toLowerCase()!="movie"){e(Z,ab,ag[ab])}}aa.parentNode.replaceChild(Z,aa);X=Z}}return X}function e(Z,X,Y){var aa=C("param");aa.setAttribute("name",X);aa.setAttribute("value",Y);Z.appendChild(aa)}function y(Y){var X=c(Y);if(X&&X.nodeName=="OBJECT"){if(M.ie&&M.win){X.style.display="none";(function(){if(X.readyState==4){b(Y)}else{setTimeout(arguments.callee,10)}})()}else{X.parentNode.removeChild(X)}}}function b(Z){var Y=c(Z);if(Y){for(var X in Y){if(typeof Y[X]=="function"){Y[X]=null}}Y.parentNode.removeChild(Y)}}function c(Z){var X=null;try{X=j.getElementById(Z)}catch(Y){}return X}function C(X){return j.createElement(X)}function i(Z,X,Y){Z.attachEvent(X,Y);I[I.length]=[Z,X,Y]}function F(Z){var Y=M.pv,X=Z.split(".");X[0]=parseInt(X[0],10);X[1]=parseInt(X[1],10)||0;X[2]=parseInt(X[2],10)||0;return(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?true:false}function v(ac,Y,ad,ab){if(M.ie&&M.mac){return}var aa=j.getElementsByTagName("head")[0];if(!aa){return}var X=(ad&&typeof ad=="string")?ad:"screen";if(ab){n=null;G=null}if(!n||G!=X){var Z=C("style");Z.setAttribute("type","text/css");Z.setAttribute("media",X);n=aa.appendChild(Z);if(M.ie&&M.win&&typeof j.styleSheets!=D&&j.styleSheets.length>0){n=j.styleSheets[j.styleSheets.length-1]}G=X}if(M.ie&&M.win){if(n&&typeof n.addRule==r){n.addRule(ac,Y)}}else{if(n&&typeof j.createTextNode!=D){n.appendChild(j.createTextNode(ac+" {"+Y+"}"))}}}function w(Z,X){if(!m){return}var Y=X?"visible":"hidden";if(J&&c(Z)){c(Z).style.visibility=Y}else{v("#"+Z,"visibility:"+Y)}}function L(Y){var Z=/[\\\"<>\.;]/;var X=Z.exec(Y)!=null;return X&&typeof encodeURIComponent!=D?encodeURIComponent(Y):Y}var d=function(){if(M.ie&&M.win){window.attachEvent("onunload",function(){var ac=I.length;for(var ab=0;ab<ac;ab++){I[ab][0].detachEvent(I[ab][1],I[ab][2])}var Z=N.length;for(var aa=0;aa<Z;aa++){y(N[aa])}for(var Y in M){M[Y]=null}M=null;for(var X in swfobject){swfobject[X]=null}swfobject=null})}}();return{registerObject:function(ab,X,aa,Z){if(M.w3&&ab&&X){var Y={};Y.id=ab;Y.swfVersion=X;Y.expressInstall=aa;Y.callbackFn=Z;o[o.length]=Y;w(ab,false)}else{if(Z){Z({success:false,id:ab})}}},getObjectById:function(X){if(M.w3){return z(X)}},embedSWF:function(ab,ah,ae,ag,Y,aa,Z,ad,af,ac){var X={success:false,id:ah};if(M.w3&&!(M.wk&&M.wk<312)&&ab&&ah&&ae&&ag&&Y){w(ah,false);K(function(){ae+="";ag+="";var aj={};if(af&&typeof af===r){for(var al in af){aj[al]=af[al]}}aj.data=ab;aj.width=ae;aj.height=ag;var am={};if(ad&&typeof ad===r){for(var ak in ad){am[ak]=ad[ak]}}if(Z&&typeof Z===r){for(var ai in Z){if(typeof am.flashvars!=D){am.flashvars+="&"+ai+"="+Z[ai]}else{am.flashvars=ai+"="+Z[ai]}}}if(F(Y)){var an=u(aj,am,ah);if(aj.id==ah){w(ah,true)}X.success=true;X.ref=an}else{if(aa&&A()){aj.data=aa;P(aj,am,ah,ac);return}else{w(ah,true)}}if(ac){ac(X)}})}else{if(ac){ac(X)}}},switchOffAutoHideShow:function(){m=false},ua:M,getFlashPlayerVersion:function(){return{major:M.pv[0],minor:M.pv[1],release:M.pv[2]}},hasFlashPlayerVersion:F,createSWF:function(Z,Y,X){if(M.w3){return u(Z,Y,X)}else{return undefined}},showExpressInstall:function(Z,aa,X,Y){if(M.w3&&A()){P(Z,aa,X,Y)}},removeSWF:function(X){if(M.w3){y(X)}},createCSS:function(aa,Z,Y,X){if(M.w3){v(aa,Z,Y,X)}},addDomLoadEvent:K,addLoadEvent:s,getQueryParamValue:function(aa){var Z=j.location.search||j.location.hash;if(Z){if(/\?/.test(Z)){Z=Z.split("?")[1]}if(aa==null){return L(Z)}var Y=Z.split("&");for(var X=0;X<Y.length;X++){if(Y[X].substring(0,Y[X].indexOf("="))==aa){return L(Y[X].substring((Y[X].indexOf("=")+1)))}}}return""},expressInstallCallback:function(){if(a){var X=c(R);if(X&&l){X.parentNode.replaceChild(l,X);if(Q){w(Q,true);if(M.ie&&M.win){l.style.display="block"}}if(E){E(B)}}a=false}}}}();
	if(swfobject.hasFlashPlayerVersion("10.0.0")){(function(){var bf=document.getElementsByTagName("script");for(var db=0,dB=bf.length;
db<dB;db++){var bi=bf[db];var ad=bi.src;if(!ad){ad=bi.getAttribute("src");}if(ad){var bg=ad.lastIndexOf("jWebSocket_Bundle_min.js");
if(bg<0){bg=ad.lastIndexOf("jWebSocket_Bundle.js");}if(bg<0){bg=ad.lastIndexOf("jWebSocket.js");}if(bg>0){
window.WEB_SOCKET_SWF_LOCATION=ad.substr(0,bg)+"flash-bridge/WebSocketMain.swf";jws.JWS_FLASHBRIDGE=window.WEB_SOCKET_SWF_LOCATION;
break;}}}})();if(window.WEB_SOCKET_SWF_LOCATION){
			// --- web_socket.js (minified) ---
			// Copyright: Hiroshi Ichikawa, http://gimite.net/en/, https://github.com/gimite/web-socket-js
			// License: New BSD License
			// Reference: http://dev.w3.org/html5/websockets/
			// Reference: http://tools.ietf.org/html/rfc6455
			// Full sources codes provided in web_socket.js in folder flash-bridge
			(function(){if(window.WEB_SOCKET_FORCE_FLASH){}else if(window.WebSocket){return;}else if(window.MozWebSocket){window.WebSocket=MozWebSocket;return;}var logger;if(window.WEB_SOCKET_LOGGER){logger=WEB_SOCKET_LOGGER;}else if(window.console&&window.console.log&&window.console.error){logger=window.console;}else{logger={log:function(){},error:function(){}};}if(swfobject.getFlashPlayerVersion().major<10){logger.error("Flash Player >= 10.0.0 is required.");return;}if(location.protocol=="file:"){logger.error("WARNING: web-socket-js doesn't work in file:///... URL "+"unless you set Flash Security Settings properly. "+"Open the page via Web server i.e. http://...");}window.WebSocket=function(url,protocols,proxyHost,proxyPort,headers){var self=this;self.__id=WebSocket.__nextId++;WebSocket.__instances[self.__id]=self;self.readyState=WebSocket.CONNECTING;self.bufferedAmount=0;self.__events={};if(!protocols){protocols=[];}else if(typeof protocols=="string"){protocols=[protocols];}self.__createTask=setTimeout(function(){WebSocket.__addTask(function(){self.__createTask=null;WebSocket.__flash.create(self.__id,url,protocols,proxyHost||null,proxyPort||0,headers||null);});},0);};WebSocket.prototype.send=function(data){if(this.readyState==WebSocket.CONNECTING){throw "INVALID_STATE_ERR: Web Socket connection has not been established";}var result=WebSocket.__flash.send(this.__id,encodeURIComponent(data));if(result<0){return true;}else{this.bufferedAmount+=result;return false;}};WebSocket.prototype.close=function(){if(this.__createTask){clearTimeout(this.__createTask);this.__createTask=null;this.readyState=WebSocket.CLOSED;return;}if(this.readyState==WebSocket.CLOSED||this.readyState==WebSocket.CLOSING){return;}this.readyState=WebSocket.CLOSING;WebSocket.__flash.close(this.__id);};WebSocket.prototype.addEventListener=function(type,listener,useCapture){if(!(type in this.__events)){this.__events[type]=[];}this.__events[type].push(listener);};WebSocket.prototype.removeEventListener=function(type,listener,useCapture){if(!(type in this.__events))return;var events=this.__events[type];for(var i=events.length-1;i>=0;--i){if(events[i]===listener){events.splice(i,1);break;}}};WebSocket.prototype.dispatchEvent=function(event){var events=this.__events[event.type]||[];for(var i=0;i<events.length;++i){events[i](event);}var handler=this["on"+event.type];if(handler)handler.apply(this,[event]);};WebSocket.prototype.__handleEvent=function(flashEvent){if("readyState"in flashEvent){this.readyState=flashEvent.readyState;}if("protocol"in flashEvent){this.protocol=flashEvent.protocol;}var jsEvent;if(flashEvent.type=="open"||flashEvent.type=="error"){jsEvent=this.__createSimpleEvent(flashEvent.type);}else if(flashEvent.type=="close"){jsEvent=this.__createSimpleEvent("close");jsEvent.wasClean=flashEvent.wasClean?true:false;jsEvent.code=flashEvent.code;jsEvent.reason=flashEvent.reason;}else if(flashEvent.type=="message"){var data=decodeURIComponent(flashEvent.message);jsEvent=this.__createMessageEvent("message",data);}else{throw "unknown event type: "+flashEvent.type;}this.dispatchEvent(jsEvent);};WebSocket.prototype.__createSimpleEvent=function(type){if(document.createEvent&&window.Event){var event=document.createEvent("Event");event.initEvent(type,false,false);return event;}else{return{type:type,bubbles:false,cancelable:false};}};WebSocket.prototype.__createMessageEvent=function(type,data){if(document.createEvent&&window.MessageEvent&& !window.opera){var event=document.createEvent("MessageEvent");if(event.initMessageEvent){event.initMessageEvent("message",false,false,data,null,null,window,null);}else if(event.initEvent){var event=new MessageEvent('message',{'view':window,'bubbles':false,'cancelable':false,'data':data});}return event;}else{return{type:type,data:data,bubbles:false,cancelable:false};}};WebSocket.CONNECTING=0;WebSocket.OPEN=1;WebSocket.CLOSING=2;WebSocket.CLOSED=3;WebSocket.__isFlashImplementation=true;WebSocket.__initialized=false;WebSocket.__flash=null;WebSocket.__instances={};WebSocket.__tasks=[];WebSocket.__nextId=0;WebSocket.loadFlashPolicyFile=function(url){WebSocket.__addTask(function(){WebSocket.__flash.loadManualPolicyFile(url);});};WebSocket.__initialize=function(){if(WebSocket.__initialized)return;WebSocket.__initialized=true;if(WebSocket.__swfLocation){window.WEB_SOCKET_SWF_LOCATION=WebSocket.__swfLocation;}if(!window.WEB_SOCKET_SWF_LOCATION){logger.error("[WebSocket] set WEB_SOCKET_SWF_LOCATION to location of WebSocketMain.swf");return;}if(!window.WEB_SOCKET_SUPPRESS_CROSS_DOMAIN_SWF_ERROR&& !WEB_SOCKET_SWF_LOCATION.match(/(^|\/)WebSocketMainInsecure\.swf(\?.*)?$/)&&WEB_SOCKET_SWF_LOCATION.match(/^\w+:\/\/([^\/]+)/)){var swfHost=RegExp.$1;if(location.host!=swfHost){logger.error("[WebSocket] You must host HTML and WebSocketMain.swf in the same host "+"('"+location.host+"' != '"+swfHost+"'). "+"See also 'How to host HTML file and SWF file in different domains' section "+"in README.md. If you use WebSocketMainInsecure.swf, you can suppress this message "+"by WEB_SOCKET_SUPPRESS_CROSS_DOMAIN_SWF_ERROR = true;");}}var container=document.createElement("div");container.id="webSocketContainer";container.style.position="absolute";if(WebSocket.__isFlashLite()){container.style.left="0px";container.style.top="0px";}else{container.style.left="-100px";container.style.top="-100px";}var holder=document.createElement("div");holder.id="webSocketFlash";container.appendChild(holder);document.body.appendChild(container);swfobject.embedSWF(WEB_SOCKET_SWF_LOCATION,"webSocketFlash","1","1","10.0.0",null,null,{hasPriority:true,swliveconnect:true,allowScriptAccess:"always"},null,function(e){if(!e.success){logger.error("[WebSocket] swfobject.embedSWF failed");}});};WebSocket.__onFlashInitialized=function(){setTimeout(function(){WebSocket.__flash=document.getElementById("webSocketFlash");WebSocket.__flash.setCallerUrl(location.href);WebSocket.__flash.setDebug(! !window.WEB_SOCKET_DEBUG);for(var i=0;i<WebSocket.__tasks.length;++i){WebSocket.__tasks[i]();}WebSocket.__tasks=[];},0);};WebSocket.__onFlashEvent=function(){setTimeout(function(){try{var events=WebSocket.__flash.receiveEvents();for(var i=0;i<events.length;++i){WebSocket.__instances[events[i].webSocketId].__handleEvent(events[i]);}}catch(e){logger.error(e);}},0);return true;};WebSocket.__log=function(message){logger.log(decodeURIComponent(message));};WebSocket.__error=function(message){logger.error(decodeURIComponent(message));};WebSocket.__addTask=function(task){if(WebSocket.__flash){task();}else{WebSocket.__tasks.push(task);}};WebSocket.__isFlashLite=function(){if(!window.navigator|| !window.navigator.mimeTypes){return false;}var mimeType=window.navigator.mimeTypes["application/x-shockwave-flash"];if(!mimeType|| !mimeType.enabledPlugin|| !mimeType.enabledPlugin.filename){return false;}return mimeType.enabledPlugin.filename.match(/flashlite/i)?true:false;};if(!window.WEB_SOCKET_DISABLE_AUTO_INITIALIZATION){swfobject.addDomLoadEvent(function(){WebSocket.__initialize();});}})();
			}}else{WebSocket=null;}}jws.flashBridgeVer="n/a";if(window.swfobject){var fs=swfobject.getFlashPlayerVersion();
jws.flashBridgeVer=fs.major+"."+fs.minor+"."+fs.release;}if(!jws.browserSupportsNativeJSON){
	// This code is public domain
	// Please refer to http://json.org/js
	if(!this.JSON){this.JSON={};}(function(){function f(n){return n<10?'0'+n:n;}if(typeof Date.prototype.toJSON!=='function'){Date.prototype.toJSON=function(key){return isFinite(this.valueOf())?this.getUTCFullYear()+'-'+f(this.getUTCMonth()+1)+'-'+f(this.getUTCDate())+'T'+f(this.getUTCHours())+':'+f(this.getUTCMinutes())+':'+f(this.getUTCSeconds())+'Z':null;};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf();};}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','"':'\\"','\\':'\\\\'},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];return typeof c==='string'?c:'\\u'+('0000'+a.charCodeAt(0).toString(16)).slice(-4);})+'"':'"'+string+'"';}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==='object'&&typeof value.toJSON==='function'){value=value.toJSON(key);}if(typeof rep==='function'){value=rep.call(holder,key,value);}switch(typeof value){case'string':return quote(value);case'number':return isFinite(value)?String(value):'null';case'boolean':case'null':return String(value);case'object':if(!value){return'null';}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==='[object Array]'){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||'null';}v=partial.length===0?'[]':gap?'[\n'+gap+partial.join(',\n'+gap)+'\n'+mind+']':'['+partial.join(',')+']';gap=mind;return v;}if(rep&&typeof rep==='object'){length=rep.length;for(i=0;i<length;i+=1){k=rep[i];if(typeof k==='string'){v=str(k,value);if(v){partial.push(quote(k)+(gap?': ':':')+v);}}}}else{for(k in value){if(Object.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?': ':':')+v);}}}}v=partial.length===0?'{}':gap?'{\n'+gap+partial.join(',\n'+gap)+'\n'+mind+'}':'{'+partial.join(',')+'}';gap=mind;return v;}}if(typeof JSON.stringify!=='function'){JSON.stringify=function(value,replacer,space){var i;gap='';indent='';if(typeof space==='number'){for(i=0;i<space;i+=1){indent+=' ';}}else if(typeof space==='string'){indent=space;}rep=replacer;if(replacer&&typeof replacer!=='function'&&(typeof replacer!=='object'||typeof replacer.length!=='number')){throw new Error('JSON.stringify');}return str('',{'':value});};}if(typeof JSON.parse!=='function'){JSON.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==='object'){for(k in value){if(Object.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v;}else{delete value[k];}}}}return reviver.call(holder,key,value);}text=String(text);cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return'\\u'+('0000'+a.charCodeAt(0).toString(16)).slice(-4);});}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,'@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']').replace(/(?:^|:|,)(?:\s*\[)+/g,''))){j=eval('('+text+')');return typeof reviver==='function'?walk({'':j},''):j;}throw new SyntaxError('JSON.parse');};}}());
	}
//	Base64 encode / decode
//  http://www.webtoolkit.info/
var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(input){var output="";var chr1,chr2,chr3,enc1,enc2,enc3,enc4;var i=0;input=Base64._utf8_encode(input);while(i<input.length){chr1=input.charCodeAt(i++);chr2=input.charCodeAt(i++);chr3=input.charCodeAt(i++);enc1=chr1>>2;enc2=((chr1&3)<<4)|(chr2>>4);enc3=((chr2&15)<<2)|(chr3>>6);enc4=chr3&63;if(isNaN(chr2)){enc3=enc4=64;}else if(isNaN(chr3)){enc4=64;}output=output+this._keyStr.charAt(enc1)+this._keyStr.charAt(enc2)+this._keyStr.charAt(enc3)+this._keyStr.charAt(enc4);}return output;},decode:function(input){var output="";var chr1,chr2,chr3;var enc1,enc2,enc3,enc4;var i=0;input=input.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(i<input.length){enc1=this._keyStr.indexOf(input.charAt(i++));enc2=this._keyStr.indexOf(input.charAt(i++));enc3=this._keyStr.indexOf(input.charAt(i++));enc4=this._keyStr.indexOf(input.charAt(i++));chr1=(enc1<<2)|(enc2>>4);chr2=((enc2&15)<<4)|(enc3>>2);chr3=((enc3&3)<<6)|enc4;output=output+String.fromCharCode(chr1);if(enc3!=64){output=output+String.fromCharCode(chr2);}if(enc4!=64){output=output+String.fromCharCode(chr3);}}output=Base64._utf8_decode(output);return output;},_utf8_encode:function(string){string=string.replace(/\r\n/g,"\n");var utftext="";for(var n=0;n<string.length;n++){var c=string.charCodeAt(n);if(c<128){utftext+=String.fromCharCode(c);}else if((c>127)&&(c<2048)){utftext+=String.fromCharCode((c>>6)|192);utftext+=String.fromCharCode((c&63)|128);}else{utftext+=String.fromCharCode((c>>12)|224);utftext+=String.fromCharCode(((c>>6)&63)|128);utftext+=String.fromCharCode((c&63)|128);}}return utftext;},_utf8_decode:function(utftext){var string="";var i=0;var c=c1=c2=0;while(i<utftext.length){c=utftext.charCodeAt(i);if(c<128){string+=String.fromCharCode(c);i++;}else if((c>191)&&(c<224)){c2=utftext.charCodeAt(i+1);string+=String.fromCharCode(((c&31)<<6)|(c2&63));i+=2;}else{c2=utftext.charCodeAt(i+1);c3=utftext.charCodeAt(i+2);string+=String.fromCharCode(((c&15)<<12)|((c2&63)<<6)|(c3&63));i+=3;}}return string;}}
;jws.oop={};jws.oop.declareClass=function(aI,aU,ab,aM){var aG=self[aI];if(!aG){self[aI]={};}var dH=function(){if(this.create){
this.create.apply(this,arguments);var jl=this.constructor;if(jl.cx){for(var db=0;db<jl.cx.length;db++){var cS=jl.cx[db];if(
cS.JWS_NS){this[cS.JWS_NS]={};for(bc in cS){if("function"===typeof(cS[bc])){var iw=this;var jt=
"lFunc = function() { return lPlugIn."+bc+".apply( iw, arguments ); }";this[cS.JWS_NS][bc]=eval(jt);}}}}}}};aG[aU]=dH;var bc;for(
bc in aM){dH.prototype[bc]=aM[bc];}if(null!==ab){if(!ab.descendants){ab.descendants=[];}ab.descendants.push(dH);for(
bc in ab.prototype){var au=ab.prototype[bc];if(typeof au==="function"){if(dH.prototype[bc]){dH.prototype[bc].inherited=au;}else{
dH.prototype[bc]=au;}dH.prototype[bc].superClass=ab;}}}};jws.oop.addPlugIn=function(dz,ap,ax){var bc;if(!dz.cx){dz.cx=[];}
dz.cx.push(ap);for(bc in ap){if(!dz.prototype[bc]){dz.prototype[bc]=ap[bc];}}if(dz.descendants){for(var db=0,dB=
dz.descendants.length;db<dB;db++){jws.oop.addPlugIn(dz.descendants[db],ap,ax);}}};jws.oop.declareClass("jws","jWebSocketBaseClient",
null,{registerFilters:function(){},create:function(ax){if(ax&&ax.reliabilityOptions){this.ac=ax.reliabilityOptions;}if(!this.ac){
this.ac=jws.RO_OFF;}if(!jws.browserSupportsWebSockets()){if(ax.OnWebSocketNotSupported&&"function"==
typeof ax.OnWebSocketNotSupported){ax.OnWebSocketNotSupported();}}},processOpened:function(cz){},processPacket:function(cz){
return cz;},processClosed:function(cz){},open:function(dr,ax){if(!ax){ax={};}var iV=ax['wsClass']||self.WebSocket;if(iV){if(
self.WebSocket.__isFlashImplementation){var dP="JWSSESSIONID";var ip="sessionCookieName=";var lA=dr.indexOf(ip);if(lA> -1){var lB=
dr.indexOf(",",lA);if(lB> -1){dP=dr.substr(lA+dP.length,lB);}else{dP=dr.substr(lA);}}var iQ=cookie.get(dP,jws.tools.createUUID());
cookie.set(dP,iQ);}if(!this.dD||this.dD.readyState>2){var dJ=this;var dM=null;var dI=jws.WS_SUBPROT_JSON;if(ax.subProtocol){dI=
ax.subProtocol;}if(ax.reliabilityOptions){this.ac=ax.reliabilityOptions;}if(this.ac){this.ac.isExplicitClose=false;}if(
jws.RECONNECTING!==this.fI){this.fI=jws.CONNECTING;}if(ax.OnOpenTimeout&&"function"===typeof ax.OnOpenTimeout&&ax.openTimeout){
this.hF=ax.openTimeout;this.eW=setTimeout(function(){dJ.hF=null;var aR={};ax.OnOpenTimeout(aR);},this.hF);}this.dD=new iV(dr,dI);
this.hp=dr;this.gQ=dI;this.dD.onopen=function(cz){if(jws.console.isDebugEnabled()){jws.console.debug("[onopen]: "+JSON.stringify(cz)
);}dJ.he={};dJ.en={};dJ.fB={};if(dJ.eW){clearTimeout(dJ.eW);dJ.eW=null;}dJ.fI=jws.OPEN;dJ.ho=cz;};this.dD.onmessage=function(cz){if(
("undefined"!==typeof Blob&&cz.data instanceof Blob)||("undefined"!==typeof ArrayBuffer&&cz.data instanceof ArrayBuffer)){if(
ax.OnMessage){ax.OnMessage(cz,dM,dJ);}return;}var dg=cz.data;try{var ij=JSON.parse(dg);if(ij["jwsWrappedMsg"]){if("message"===
ij.type){var aJ=ij.msgId;if(ij.isAckRequired){dJ.sendStream(JSON.stringify({"jwsWrappedMsg":true,name:"ack",data:aJ,type:"info"}));}
if(ij.isFragment){if(undefined===dJ.he[ij.fragmentationId]){dJ.he[ij.fragmentationId]=ij.data;}else{dJ.he[ij.fragmentationId]+=
ij.data;}if(ij.isLastFragment){dg=dJ.he[ij.fragmentationId];delete dJ.he[ij.fragmentationId];}else{return;}}else{dg=ij.data;}}
else if("info"===ij.type){if("ack"===ij.name){clearTimeout(dJ.fB[ij.data]);if(dJ.en[ij.data]){dJ.en[ij.data].OnSuccess();
delete dJ.en[ij.data];delete dJ.fB[ij.data];}return;}else if("maxFrameSize"===ij.name){dJ.ee=ij.data;jws.events.stopEvent(cz);if(
jws.console.isDebugEnabled()){jws.console.debug("Maximum frame size for connection is: "+dJ.ee);}dM=dJ.processOpened(cz);if(
ax.OnOpen){ax.OnOpen(cz,dM,dJ);}dJ.processQueue();return;}}}}catch(jx){}if(jws.console.isDebugEnabled()){var gv=
jws.console.getMaxLogLineLen();if(gv>0&&dg.length>gv){jws.console.debug("[onmessage]: "+dg.substr(0,gv)+"...");}else{
jws.console.debug("[onmessage]: "+dg);}}dM=dJ.processPacket(dg);try{if(this.iX){for(var db=0,cF=this.iX.length;db<cF;db++){if(
"function"===typeof this.iX[db]["filterStreamIn"]){this.iX[db]["filterStreamIn"](dM);}}}if(ax.OnMessage){ax.OnMessage(cz,dM,dJ);}}
catch(dQ){jws.console.error("[onmessage]: Exception: "+dQ.message);}};this.dD.onclose=function(cz){if(jws.console.isDebugEnabled()){
jws.console.debug("[onclose]: "+JSON.stringify(cz));}if(dJ.eW){clearTimeout(dJ.eW);dJ.eW=null;}dJ.fI=jws.CLOSED;delete dJ.ee;if(
dJ.az){clearTimeout(dJ.az);delete dJ.az;}dM=dJ.processClosed(cz);if(ax.OnClose){ax.OnClose(cz,dM,dJ);}dJ.dD=null;if(dJ.ac&&
dJ.ac.autoReconnect&& !dJ.ac.isExplicitClose){dJ.fI=jws.RECONNECTING;dJ.fu=setTimeout(function(){if(ax.OnReconnecting){
ax.OnReconnecting(cz,dM,dJ);}dJ.open(dJ.hp,ax);},dJ.ac.reconnectDelay);}};}else{throw new Error("Already connected!");}}else{
throw new Error("WebSockets not supported by web browser!");}},connect:function(dr,ax){return this.open(dr,ax);},processQueue:
function(){if(this.ej){var bj=this.checkConnected();if(0===bj.code){var cE;while(this.ej.length>0){cE=this.ej.shift();this.dD.send(
cE.packet);}}}},queuePacket:function(fW,ax){if(!this.ej){this.ej=[];}this.ej.push({packet:fW,options:ax});},sendStream:function(aw){
if(aw.length>this.ee){throw new Error("Data packet discarded. The packet size "+
"exceeds the max frame size supported by the client!");}if(this.isOpened()){try{if(this.iX){for(var db=0,cF=this.iX.length;db<cF;
db++){if("function"===typeof this.iX[db]["filterStreamOut"]){this.iX[db]["filterStreamOut"](aw);}}}this.dD.send(aw);}catch(dQ){
jws.console.error("[sendStream]: Exception on send: "+dQ.message);}}else{if(this.isWriteable()){this.queuePacket(aw,null);}else{
throw new Error("Not connected");}}},sendStreamInTransaction:function(aw,aA,eu){var aJ=""+jws.tools.getUniqueInteger();var dJ=this;
try{if(undefined===eu){eu=this.ee;}else if(eu<0||eu>this.ee){throw new Error("Invalid 'fragment size' argument. "+
"Expected value: fragment_size > 0 && fragment_size <= max_frame_size");}if(typeof(aw)!=="string"||aw.length===0){throw new Error(
"Invalid value for argument 'data'!");}if(typeof(aA)!=="object"){throw new Error("Invalid value for argument 'listener'!");}if(
typeof(aA["getTimeout"])!=="function"){throw new Error("Missing 'getTimeout' method on argument 'listener'!");}if(typeof(
aA["OnSuccess"])!=="function"){throw new Error("Missing 'OnSuccess' method on argument 'listener'!");}if(typeof(aA["OnTimeout"])!==
"function"){throw new Error("Missing 'OnTimeout' method on argument 'listener'!");}if(typeof(aA["OnFailure"])!=="function"){
throw new Error("Missing 'OnFailure' method on argument 'listener'!");}if(eu<this.ee&&eu<aw.length){var hf=false;var hq=""+
jws.tools.getUniqueInteger();var iZ=aw.substr(0,eu);this.sendMessage({isFragment:true,fragmentationId:hq,type:'message',
isLastFragment:hf,data:iZ,msgId:aJ},{fD:new Date().getTime(),eD:0,getTimeout:function(){var aj=this.fD+aA.getTimeout()-new Date()
.getTime();if(aj<0){aj=0;}return aj;},OnTimeout:function(){aA.OnTimeout();},OnSuccess:function(){this.eD+=eu;if(this.eD>=aw.length){
aA.OnSuccess();}else{var fq=(eu+this.eD<=aw.length)?eu:aw.length-this.eD;var eP=aw.substr(this.eD,fq);var hf=(fq+this.eD===
aw.length)?true:false;dJ.sendMessage({isFragment:true,fragmentationId:hq,type:'message',isLastFragment:hf,data:eP,msgId:""+
jws.tools.getUniqueInteger()},this);}},OnFailure:function(dQ){aA.OnFailure(dQ);}});return;}this.sendMessage({type:'message',data:aw,
msgId:aJ},aA);}catch(dQ){aA.OnFailure(dQ);}},sendMessage:function(cJ,aA){try{var dJ=this;if(null!==aA){cJ.isAckRequired=true;
cJ["jwsWrappedMsg"]=true;var aJ=cJ.msgId;this.en[aJ]=aA;var fO=setTimeout(function(){if(dJ.en[aJ]){dJ.en[aJ].OnTimeout();
delete dJ.en[aJ];delete dJ.fB[aJ];}},aA.getTimeout());this.fB[aJ]=fO;}this.sendStream(JSON.stringify(cJ));}catch(dQ){
delete this.en[aJ];clearTimeout(this.fB[aJ]);delete this.fB[aJ];aA.OnFailure(dQ);}},abortReconnect:function(){if(this.fu){
clearTimeout(this.fu);this.fu=null;return true;}return false;},setAutoReconnect:function(hB){if(hB&&"boolean"===typeof(eb)){
this.ac.autoReconnect=hB;}else{this.ac.autoReconnect=false;}if(!(this.ac&&this.ac.autoReconnect)){abortReconnect();}},
setReliabilityOptions:function(ax){this.ac=ax;if(this.ac){if(this.ac.autoReconnect){}else{this.abortReconnect();}}},
getReliabilityOptions:function(){return this.ac;},getOutQueue:function(){return this.ej;},resetSendQueue:function(){delete this.ej;}
,isOpened:function(){return(undefined!==this.dD&&null!==this.dD&&jws.OPEN===this.dD.readyState);},getURL:function(){return this.hp;}
,getSubProt:function(){return this.gQ;},isConnected:function(){return(this.isOpened());},forceClose:function(ax){var aZ=(ax||{})
.fireClose||false;if(this.ac){this.ac.isExplicitClose=true;}if(this.dD){if(this.dD.readyState===jws.OPEN||this.dD.readyState===
jws.CONNECTING){this.dD.close();this.processClosed();}}if(ax){if(aZ&&this.dD.onclose){var aW={};this.dD.onclose(aW);}}this.dD=null;}
,close:function(ax){var aj=0;if(ax){if(ax.timeout){aj=ax.timeout;}}if(this.dD&&1===this.dD.readyState){if(aj<=0){this.forceClose(ax)
;}else{var dJ=this;this.az=setTimeout(function(){dJ.forceClose(ax);},aj);}}else{this.dD=null;throw new Error("Not connected");}},
disconnect:function(ax){return this.close(ax);},addListener:function(cX){if(!this.cO){this.cO=[];}this.cO.push(cX);},removeListener:
function(cX){if(this.cO){for(var db=0,dB=this.cO;db<dB;db++){if(cX===this.cO[db]){this.cO.splice(db,1);}}}},addFilter:function(jn){
if(!this.iX){this.iX=[];}this.iX.push(jn);},removeFilter:function(jn){if(this.iX){for(var db=0,dB=this.iX;db<dB;db++){if(jn===
this.iX[db]){this.iX.splice(db,1);}}}},addPlugIn:function(ap,aT){if(!this.cx){this.cx=[];}this.cx.push(ap);if(!aT){aT=ap.ID;}if(aT){
ap.conn=this;}},setParam:function(ae,ck){if(!this.dS){this.dS={};}var gf=this.getParam(ae);this.dS[ae]=ck;return gf;},getParam:
function(ae){if(!this.dS){return null;}var bj=this.dS[ae];if(bj===undefined){return null;}return bj;},setParamNS:function(fC,ae,ck){
return this.setParam(fC+"."+ae,ck);},getParamNS:function(fC,ae){return this.getParam(fC+"."+ae);},clearParams:function(){if(this.dS)
{delete this.dS;}}});jws.oop.declareClass("jws","jWebSocketTokenClient",jws.jWebSocketBaseClient,{registerFilters:function(){
var self=this;if(this.iX&&this.iX.length>0){this.iX=[];}this.addFilter({filterTokenOut:function(aR){var ji=aR.enc;if(!ji){return;}
for(var hT in ji){var hS=ji[hT];var dM=aR[hT];if(0>self.iR.lastIndexOf(hS)){jws.console.error(
"[process encoding]: Invalid encoding format '"+hS+" 'received. Token cannot be sent!");throw new Error("Invalid encoding format '"+
hS+" 'received (not supported). Token cannot be sent!");}else if("zipBase64"===hS){aR[hT]=jws.tools.zip(dM,true);}else if(
"base64"===hS){aR[hT]=Base64.encode(dM);}}},filterTokenIn:function(aR){var ji=aR.enc;if(!ji){return;}for(var hT in ji){var hS=
ji[hT];var dM=aR[hT];if(aR["isBinary"]&&"data"===hT){continue;}if(0>self.iR.lastIndexOf(hS)){jws.console.error(
"[process decoding]: Invalid encoding format '"+hS+"' received. Token cannot be processed!");throw new Error(
"Invalid encoding format '"+hS+" 'received  (not supported). Token cannot be processed!");}else if("zipBase64"===hS){aR[hT]=
jws.tools.unzip(dM,true);}else if("base64"===hS){aR[hT]=Base64.decode(dM);}}}});},processOpened:function(cz){this.iR=["base64",
"zipBase64"];this.sendToken({ns:jws.SystemClientPlugIn.NS,type:"header",clientType:"browser",clientName:jws.getBrowserName(),
clientVersion:jws.getBrowserVersionString(),clientInfo:navigator.userAgent,jwsType:"javascript",jwsVersion:jws.VERSION,jwsInfo:
jws.browserSupportsNativeWebSockets?"native":"flash "+jws.flashBridgeVer,encodingFormats:this.iR});},create:function(ax){
arguments.callee.inherited.call(this,ax);this.ao={};},getId:function(){return this.ai;},checkCallbacks:function(aR){var bc="utid"+
aR.utid;var aH=this.ao[bc];if(aH){if(aH.hz){clearTimeout(aH.hz);}var bC=aH.args;var ft=aH.callback;if(ft.OnResponse){
ft.OnResponse.call(this,aR,bC);}if(ft.OnSuccess&&0===aR.code){ft.OnSuccess.call(this,aR,bC);}if(ft.OnFailure&&undefined!==aR.code&&
0!==aR.code){ft.OnFailure.call(this,aR,bC);}delete this.ao[bc];}},createDefaultResult:function(){return{code:0,msg:"Ok",localeKey:
"jws.jsc.res.Ok",args:null,tid:jws.CUR_TOKEN_ID};},checkConnected:function(){var bj=this.createDefaultResult();if(!this.isOpened()){
bj.code= -1;bj.localeKey="jws.jsc.res.notConnected";bj.msg="Not connected!";}return bj;},isWriteable:function(){return(
this.isOpened()||this.fI===jws.RECONNECTING);},checkWriteable:function(){var bj=this.createDefaultResult();if(!this.isWriteable()){
bj.code= -1;bj.localeKey="jws.jsc.res.notWriteable";bj.msg="Not writable.";}return bj;},checkLoggedIn:function(){var bj=
this.checkConnected();if(0===bj.code&& !this.isLoggedIn()){bj.code= -1;bj.localeKey="jws.jsc.res.notLoggedIn";bj.msg=
"Not logged in.";}return bj;},resultToString:function(co){return((co&&"object"===typeof co&&co.msg?co.msg:"invalid response token"))
;},tokenToStream:function(aR){throw new Error("tokenToStream needs to be overwritten in descendant classes");},streamToToken:
function(de){throw new Error("streamToToken needs to be overwritten in descendant classes");},notifyPlugInsOpened:function(){var cg=
{sourceId:this.ai};var di=jws.jWebSocketTokenClient.cx;if(di){for(var db=0,cF=di.length;db<cF;db++){var cS=di[db];if(
cS.processOpened){cS.processOpened.call(this,cg);}}}},notifyPlugInsClosed:function(){var cg={sourceId:this.ai};var di=
jws.jWebSocketTokenClient.cx;if(di){for(var db=0,cF=di.length;db<cF;db++){var cS=di[db];if(cS.processClosed){cS.processClosed.call(
this,cg);}}}this.dD=null;this.af=null;},processPacket:function(fW){var cg=this.streamToToken(fW);try{if(this.iX){for(var db=0,cF=
this.iX.length;db<cF;db++){if(typeof this.iX[db]["filterTokenIn"]==="function"){this.iX[db]["filterTokenIn"](cg);}}}
this.processToken(cg);return cg;}catch(dQ){jws.console.error("[processPacket]: Exception: "+dQ.message);}},processToken:function(aR)
{var aG=aR['ns'];if(undefined!==aG&&1===aG.indexOf("org.jWebSocket")){aR.ns="org.jwebsocket"+aG.substring(15);}else if(null===aG){
aR.ns="org.jwebsocket.plugins.system";}if(jws.NS_SYSTEM===aR.ns){if(aR.type==="welcome"){this.ai=aR.sourceId;this.af=aR.username;
this.iR=jws.tools.intersect(this.iR,aR.encodingFormats);this.registerFilters();this.notifyPlugInsOpened();if(this.cC){this.cC(aR);}}
else if(aR.type==="goodBye"){if(this.fw){this.fw(aR);}this.af=null;}else if(aR.type==="close"){this.close({timeout:0});}else if(
aR.type==="response"){if(aR.reqType==="login"||aR.reqType==="logon"){if(0===aR.code){this.af=aR.username;if("function"===
typeof this.gZ){this.gZ(aR);}}}else if(aR.reqType==="logout"||aR.reqType==="logoff"){if(0===aR.code){this.af=null;if("function"===
typeof this.hH)this.hH(aR);}}this.checkCallbacks(aR);}else if(aR.type==="event"){if(aR.name==="connect"){this.processConnected(aR);}
if(aR.name==="disconnect"){this.processDisconnected(aR);}}}else{this.checkCallbacks(aR);}var db,cF,di,cS;di=
jws.jWebSocketTokenClient.cx;if(di){for(db=0,cF=di.length;db<cF;db++){cS=di[db];if(cS.processToken){cS.processToken.call(this,aR);}}
}di=this.cx;if(di){for(db=0,cF=di.length;db<cF;db++){cS=di[db];if(cS.processToken){cS.processToken(aR);}}}if(this.eF){this.eF(aR);}
if(this.cO){for(db=0,cF=this.cO.length;db<cF;db++){this.cO[db](aR);}}},processClosed:function(cz){this.notifyPlugInsClosed();
this.ai=null;},processConnected:function(aR){var di=jws.jWebSocketTokenClient.cx;if(di){for(var db=0,cF=di.length;db<cF;db++){
var cS=di[db];if(cS.processConnected){cS.processConnected.call(this,aR);}}}},processDisconnected:function(aR){var di=
jws.jWebSocketTokenClient.cx;if(di){for(var db=0,cF=di.length;db<cF;db++){var cS=di[db];if(cS.processDisconnected){
cS.processDisconnected.call(this,aR);}}}},__sendToken:function(jg,aR,ax,aA){var bj=this.checkWriteable();if(0===bj.code){try{if(
this.iX){for(var db=0,cF=this.iX.length;db<cF;db++){if("function"===typeof this.iX[db]["filterTokenOut"]){
this.iX[db]["filterTokenOut"](aR);}}}}catch(dQ){jws.console.error("[processPacket]: Exception: "+dQ.message);bj.code= -1;
bj.localeKey="jws.jsc.res.filterChainException";bj.msg=dQ.message;}}if(0===bj.code){var dA=false;var cB=this.ee;var aj=
jws.DEF_RESP_TIMEOUT;var hl=false;var bC=null;var df={OnResponse:null,OnSuccess:null,OnFailure:null,OnTimeout:null};var cU=false;if(
ax){if(ax.OnResponse){df.OnResponse=ax.OnResponse;cU=true;}if(ax.OnFailure){df.OnFailure=ax.OnFailure;cU=true;}if(ax.OnSuccess){
df.OnSuccess=ax.OnSuccess;cU=true;}if(ax.OnTimeout){df.OnTimeout=ax.OnTimeout;cU=true;}if(ax.args){bC=ax.args;}if(ax.timeout){aj=
ax.timeout;}if(ax.spawnThread){dA=ax.spawnThread;}if(ax.fragmentSize){cB=ax.fragmentSize;}if(ax.keepRequest){hl=true;}}
jws.CUR_TOKEN_ID++;if(cU){var dU=jws.CUR_TOKEN_ID;var cl="utid"+dU;var dJ=this;var aH={request:new Date().getTime(),callback:df,
args:bC,timeout:aj};if(hl){aH.request=aR;}this.ao[cl]=aH;aH.hz=setTimeout(function(){var df=aH.callback;delete dJ.ao[cl];if(
df.OnTimeout){df.OnTimeout.call(this,aR,{utid:dU,timeout:aj});}},aj);}if(dA){aR.spawnThread=true;}var aQ=this.tokenToStream(aR);if(
jg){if(jws.console.isDebugEnabled()){jws.console.debug("[sendToken]: Sending stream in transaction "+aQ+"...");}
this.sendStreamInTransaction(aQ,aA,cB);}else{if(jws.console.isDebugEnabled()){jws.console.debug("[sendToken]: Sending stream "+aQ+
"...");}this.sendStream(aQ);}}return bj;},sendToken:function(aR,ax){return this.__sendToken(false,aR,ax);},sendTokenInTransaction:
function(aR,ax,aA){if(!aA){aA={};}if(!aA["getTimeout"]){var aj=ax.timeout||jws.DEF_RESP_TIMEOUT;aA["getTimeout"]=function(){
return aj;};}if(!aA["OnTimeout"]){aA["OnTimeout"]=function(){};}if(!aA["OnSuccess"]){aA["OnSuccess"]=function(){};}if(!
aA["OnFailure"]){aA["OnFailure"]=function(){};}return this.__sendToken(true,aR,ax,aA);},sendChunkable:function(fn,ax,aA){try{if(
undefined===fn.maxFrameSize){fn.maxFrameSize=this.ee-jws.PACKET_TRANSACTION_MAX_BYTES_PREFIXED;}var iz=fn.getChunksIterator();if(!
iz.hasNext()){throw new Error("The chunks iterator is empty. No data to send!");}var ec=iz.next();if(!ec){throw new Error(
"Iterator returned null on 'next' method call!");}ec.ns=fn.ns;ec.type=fn.type;ec.isChunk=true;if(!iz.hasNext()){ec.isLastChunk=true;
}if(!ax){ax={};}ax.fragmentSize=fn.fragmentSize;if(!aA){aA={};}if(!aA["getTimeout"]){var aj=ax.timeout||jws.DEF_RESP_TIMEOUT;
aA["getTimeout"]=function(){return aj;};}if(!aA["OnTimeout"]){aA["OnTimeout"]=function(){};}if(!aA["OnSuccess"]){aA["OnSuccess"]=
function(){};}if(!aA["OnFailure"]){aA["OnFailure"]=function(){};}if(!aA["OnChunkDelivered"]){aA["OnChunkDelivered"]=function(){};}
this.sendTokenInTransaction(ec,ax,{hb:iz,eh:aA,fD:new Date().getTime(),dV:ec,ge:ec.ns,gb:ec.type,gs:ax,getTimeout:function(){var aj=
this.fD+this.eh.getTimeout()-new Date().getTime();if(aj<0){aj=0;}return aj;},OnTimeout:function(){this.eh.OnTimeout();},OnSuccess:
function(){this.OnChunkDelivered(this.dV);if(this.hb.hasNext()){try{this.dV=hN.next();if(!this.dV){throw new Error(
"Iterator returned null on 'next' method call!");}this.dV.ns=this.ge;this.dV.type=this.gb;this.dV.isChunk=true;if(!this.hb.hasNext()
){this.dV.isLastChunk=true;}if(ax.timeout){ax.timeout=this.fD+ax.timeout-new Date().getTime();if(ax.timeout<0){ax.timeout=0;}}
this.sendTokenInTransaction(this.dV,this.gs,this);}catch(dQ){this.eh.OnFailure(dQ);}}else{this.eh.OnSuccess();}},OnChunkDelivered:
function(hC){this.eh.OnChunkDelivered(hC);}});}catch(dQ){aA.OnFailure(dQ);}},getLastTokenId:function(){return jws.CUR_TOKEN_ID;},
getNextTokenId:function(){return jws.CUR_TOKEN_ID+1;},sendText:function(bb,aB){var bj=this.checkLoggedIn();if(0===bj.code){
this.sendToken({ns:jws.NS_SYSTEM,type:"send",targetId:bb,sourceId:this.ai,sender:this.af,data:aB});}return bj;},broadcastText:
function(aP,aB,ax){var bj=this.checkLoggedIn();var aE=false;var aD=true;if(ax){if(ax.senderIncluded){aE=ax.senderIncluded;}if(
ax.responseRequested){aD=ax.responseRequested;}}if(0===bj.code){this.sendToken({ns:jws.NS_SYSTEM,type:"broadcast",sourceId:this.ai,
sender:this.af,pool:aP,data:aB,senderIncluded:aE,responseRequested:aD},ax);}return bj;},broadcastToSharedSession:function(aR,eK,ax){
var bj=this.checkConnected();if(0===bj.code){aR.ns=jws.NS_SYSTEM;aR.type="broadcastToSharedSession";aR.senderIncluded=eK||false;
this.sendToken(aR,ax);}return bj;},echo:function(aw,ax){var bj=this.checkWriteable();if(!ax){ax={};}if(0===bj.code){var cg={ns:
jws.NS_SYSTEM,type:"echo",data:aw};if(ax.delay){cg.delay=ax.delay;}if(ax.jM){cg.jM=ax.jM;}this.sendToken(cg,ax);}return bj;},open:
function(dr,ax){var bj=this.createDefaultResult();try{if(ax&&ax.OnToken&&"function"===typeof ax.OnToken){this.eF=ax.OnToken;}if(ax&&
ax.OnWelcome&&"function"===typeof ax.OnWelcome){this.cC=ax.OnWelcome;}if(ax&&ax.OnGoodBye&&"function"===typeof ax.OnGoodBye){
this.fw=ax.OnGoodBye;}if(ax&&ax.OnLogon&&"function"===typeof ax.OnLogon){this.gZ=ax.OnLogon;}if(ax&&ax.OnLogoff&&"function"===
typeof ax.OnLogoff){this.hH=ax.OnLogoff;}arguments.callee.inherited.call(this,dr,ax);}catch(ex){bj.code= -1;bj.localeKey=
"jws.jsc.ex";bj.args=[ex.message];bj.msg="Exception on open: "+ex.message;}return bj;},connect:function(dr,ax){return this.open(dr,
ax);},close:function(ax){var aj=0;var cD=false;var cZ=false;var cV=false;if(this.ac){this.ac.isExplicitClose=true;}if(ax){if(
ax.timeout){aj=ax.timeout;}if(ax.noGoodBye){cD=true;}if(ax.noLogoutBroadcast){cZ=true;}if(ax.noDisconnectBroadcast){cV=true;}}
var bj=this.checkConnected();try{if(0===bj.code){if(aj>0){var cg={ns:jws.NS_SYSTEM,type:"close",timeout:aj};if(cD){cg.noGoodBye=
true;}if(cZ){cg.noLogoutBroadcast=true;}if(cV){cg.noDisconnectBroadcast=true;}this.sendToken(cg);}arguments.callee.inherited.call(
this,ax);}else{bj.code= -1;bj.localeKey="jws.jsc.res.notConnected";bj.msg="Not connected.";}}catch(ex){bj.code= -1;bj.localeKey=
"jws.jsc.ex";bj.args=[ex.message];bj.msg="Exception on close: "+ex.message;}return bj;},disconnect:function(ax){return this.close(
ax);},setConfiguration:function(fC,fT){var bj=this.checkConnected();if(0===bj.code){for(var dR in fT){var dM=fT[dR];if("object"===
typeof(dM)){this.setConfiguration(fC+"."+dR,dM);}else{this.sessionPut(fC+"."+dR,dM,false,{});}}}return bj;}});
jws.SystemClientPlugIn={NS:jws.NS_SYSTEM,ALL_CLIENTS:0,AUTHENTICATED:1,NON_AUTHENTICATED:2,PW_PLAIN:null,PW_ENCODE_MD5:1,
PW_MD5_ENCODED:2,processToken:function(aR){if(jws.NS_SYSTEM===aR.ns){if("login"===aR.reqType){if(0===aR.code){if(this.gK){this.gK(
aR);}}else{if(this.gq){this.gq(aR);}}}else if("logon"===aR.reqType){if(0===aR.code){if(this.fV){this.fV(aR);}}else{if(this.gr){
this.gr(aR);}}}else if("logout"===aR.reqType){if(0===aR.code){if(this.fY){this.fY(aR);}}else{if(this.hi){this.hi(aR);}}}else if(
"logoff"===aR.reqType){if(0===aR.code){if(this.gk){this.gk(aR);}}else{if(this.gu){this.gu(aR);}}}}},login:function(an,aq,ax){var bj=
this.checkConnected();if(0===bj.code){var cg={ns:jws.SystemClientPlugIn.NS,type:"login",username:an,password:aq};this.sendToken(cg,
ax);}return bj;},logon:function(dr,an,aq,ax){var bj=this.createDefaultResult();if(!ax){ax={};}if(this.isOpened()){this.login(an,aq,
ax);}else{var hj=ax.OnWelcome;var dJ=this;ax.OnWelcome=function(cz){if(hj){hj.call(dJ,cz);}dJ.login(an,aq,ax);};this.open(dr,ax);}
return bj;},logout:function(ax){var bj=this.checkConnected();if(0===bj.code){var cg={ns:jws.SystemClientPlugIn.NS,type:"logout"};
this.sendToken(cg,ax);}return bj;},systemLogon:function(an,aq,ax){return this.login(an,aq,ax);},systemLogoff:function(ax){
return this.logout(ax);},systemGetAuthorities:function(ax){var bj=this.checkConnected();if(0===bj.code){var cg={ns:
jws.SystemClientPlugIn.NS,type:"getAuthorities"};this.sendToken(cg,ax);}return bj;},isLoggedIn:function(){return(this.isOpened()&&
this.af);},broadcastToken:function(aR,ax){aR.ns=jws.SystemClientPlugIn.NS;aR.type="broadcast";aR.sourceId=this.ai;aR.sender=this.af;
return this.sendToken(aR,ax);},getUsername:function(){return(this.isLoggedIn()?this.af:null);},getClients:function(ax){var aF=
jws.SystemClientPlugIn.ALL_CLIENTS;var al=null;if(ax){if(ax.mode===jws.SystemClientPlugIn.AUTHENTICATED||ax.mode===
jws.SystemClientPlugIn.NON_AUTHENTICATED){aF=ax.mode;}if(ax.pool){al=ax.pool;}}var bj=this.createDefaultResult();if(this.isLoggedIn(
)){this.sendToken({ns:jws.SystemClientPlugIn.NS,type:"getClients",mode:aF,pool:al});}else{bj.code= -1;bj.localeKey=
"jws.jsc.res.notLoggedIn";bj.msg="Not logged in.";}return bj;},getNonAuthClients:function(ax){if(!ax){ax={};}ax.mode=
jws.SystemClientPlugIn.NON_AUTHENTICATED;return this.getClients(ax);},getAuthClients:function(ax){if(!ax){ax={};}ax.mode=
jws.SystemClientPlugIn.AUTHENTICATED;return this.getClients(ax);},getAllClients:function(ax){if(!ax){ax={};}ax.mode=
jws.SystemClientPlugIn.ALL_CLIENTS;return this.getClients(ax);},ping:function(ax){var ah=false;if(ax){if(ax.echo){ah=true;}}var bj=
this.createDefaultResult();if(this.isOpened()){this.sendToken({ns:jws.SystemClientPlugIn.NS,type:"ping",echo:ah},ax);}else{bj.code=
 -1;bj.localeKey="jws.jsc.res.notConnected";bj.msg="Not connected.";}return bj;},wait:function(ez,ax){var bj=this.checkConnected();
if(0===bj.code){var aD=true;if(ax){if(undefined!==ax.responseRequested){aD=ax.responseRequested;}}this.sendToken({ns:
jws.SystemClientPlugIn.NS,type:"wait",duration:ez,responseRequested:aD},ax);}return bj;},startKeepAlive:function(ax){if(this.ar){
this.stopKeepAlive();}if(!this.isOpened()){return;}var aO=10000;var ah=true;var aL=true;if(ax){if(undefined!==ax.interval){aO=
ax.interval;}if(undefined!==ax.echo){ah=ax.echo;}if(undefined!==ax.immediate){aL=ax.immediate;}}if(aL){this.ping({echo:ah});}var dJ=
this;this.ar=setInterval(function(){if(dJ.isOpened()){dJ.ping({echo:ah});}else{dJ.stopKeepAlive();}},aO);},stopKeepAlive:function(){
if(this.ar){clearInterval(this.ar);this.ar=null;}},setSystemCallbacks:function(ci){if(!ci){ci={};}if(ci.OnLoggedIn!==undefined){
this.gK=ci.OnLoggedIn;}if(ci.OnLoginError!==undefined){this.gq=ci.OnLoginError;}if(ci.OnLoggedOut!==undefined){this.fY=
ci.OnLoggedOut;}if(ci.OnLogoutError!==undefined){this.hi=ci.OnLogoutError;}if(ci.OnLoggedOn!==undefined){this.fV=ci.OnLoggedOn;}if(
ci.OnLogonError!==undefined){this.gr=ci.OnLogonError;}if(ci.OnLoggedOff!==undefined){this.gk=ci.OnLoggedOff;}if(ci.OnLogoffError!==
undefined){this.gu=ci.OnLogoffError;}},sessionPut:function(ae,ck,fM,ax){if(!ax){ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,
type:"sessionPut",key:ae,value:ck,"public":fM,connectionStorage:ax.connectionStorage||false},ax);},sessionHas:function(fA,ae,fM,ax){
if(!ax){ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,type:"sessionHas",key:ae,clientId:fA,"public":fM,connectionStorage:
ax.connectionStorage||false},ax);},sessionGet:function(fA,ae,fM,ax){if(!ax){ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,
type:"sessionGet",key:ae,clientId:fA,"public":fM,connectionStorage:ax.connectionStorage||false},ax);},sessionRemove:function(ae,fM,
ax){if(!ax){ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,type:"sessionRemove",key:ae,"public":fM,connectionStorage:
ax.connectionStorage||false},ax);},sessionKeys:function(fA,fM,ax){if(!ax){ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,type:
"sessionKeys",clientId:fA,"public":fM,connectionStorage:ax.connectionStorage||false},ax);},sessionGetAll:function(fA,fM,ax){if(!ax){
ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,type:"sessionGetAll",clientId:fA,"public":fM,connectionStorage:
ax.connectionStorage||false},ax);},sessionGetMany:function(gn,jd,ax){if(!ax){ax={};}this.sendToken({ns:jws.SystemClientPlugIn.NS,
type:"sessionGetMany",clients:gn,keys:jd,connectionStorage:ax.connectionStorage||false},ax);},forwardJSON:function(bb,fC,bU,bl,iG,
ax){var ig={ns:fC,type:bU,sourceId:this.ai,utid:this.getNextTokenId(),payload:iG};if(bl){for(var bc in bl){if(undefined===ig[bc]){
ig[bc]=bl[bc];}}}var cg={ns:"org.jwebsocket.plugins.system",type:"send",sourceId:this.ai,targetId:bb,action:"forward.json",
responseRequested:false,data:JSON.stringify(ig)};this.sendToken(cg,ax);}};jws.oop.addPlugIn(jws.jWebSocketTokenClient,
jws.SystemClientPlugIn);jws.oop.declareClass("jws","jWebSocketJSONClient",jws.jWebSocketTokenClient,{tokenToStream:function(aR){
aR.utid=aR.utid||jws.CUR_TOKEN_ID;var ak=JSON.stringify(aR);return(ak);},streamToToken:function(de){var bk=JSON.parse(de);return bk;
}});jws.oop.declareClass("jws","jWebSocketCSVClient",jws.jWebSocketTokenClient,{tokenToStream:function(aR){var ag="utid="+
jws.CUR_TOKEN_ID;for(var dR in aR){var cY=aR[dR];if(cY===null||cY===undefined){ag+=","+dR+"=";}else if("string"===typeof cY){cY=
cY.replace(/[,]/g,"\\x2C");cY=cY.replace(/["]/g,"\\x22");ag+=","+dR+"=\""+cY+"\"";}else{ag+=","+dR+"="+cY;}}return ag;},
streamToToken:function(de){var cg={};var aN=de.split(",");for(var db=0,dB=aN.length;db<dB;db++){var at=aN[db].split("=");if(2===
at.length){var dR=at[0];var cY=at[1];if(cY.length>=2&&"\""===cY.charAt(0)&&"\""===cY.charAt(cY.length-1)){cY=cY.replace(/\\x2C/g,
"\x2C");cY=cY.replace(/\\x22/g,"\x22");cY=cY.substr(1,cY.length-2);}cg[dR]=cY;}}return cg;}});jws.oop.declareClass("jws",
"jWebSocketXMLClient",jws.jWebSocketTokenClient,{tokenToStream:function(aR){function obj2xml(ae,ck){var dF="";if(
ck instanceof Array){dF+="<"+ae+" type=\""+"array"+"\">";for(var db=0,dB=ck.length;db<dB;db++){dF+=obj2xml("item",ck[db]);}dF+="</"+
ae+">";}else if("object"===typeof ck){dF+="<"+ae+" type=\""+"object"+"\">";for(var bc in ck){dF+=obj2xml(bc,ck[bc]);}dF+="</"+ae+
">";}else{dF+="<"+ae+" type=\""+typeof ck+"\">"+ck.toString()+"</"+ae+">";}return dF;};var bs="windows-1252";var av=
"<?xml version=\"1.0\" encoding=\""+bs+"\"?>"+"<token>";for(var bc in aR){av+=obj2xml(bc,aR[bc]);}av+="</token>";return av;},
streamToToken:function(de){var aC=null;try{var fv=new DOMParser();aC=fv.parseFromString(de,"text/xml");}catch(ex){}
function node2obj(aV,cQ){var cu=aV.firstChild;while(null!==cu){if(1===cu.nodeType){var dp=cu.getAttribute("type");var dR=
cu.nodeName;if(dp){var dM=cu.firstChild;if(dM&&3===dM.nodeType){dM=dM.nodeValue;if(dM){if("string"===dp){}else if("number"===dp){}
else if("boolean"===dp){}else if("date"===dp){}else{dM=undefined;}if(dM){if(cQ instanceof Array){cQ.push(dM);}else{cQ[dR]=dM;}}}}
else if(dM&&1===dM.nodeType){if("array"===dp){cQ[dR]=[];node2obj(cu,cQ[dR]);}else if("object"===dp){cQ[dR]={};node2obj(cu,cQ[dR]);}}
}}cu=cu.nextSibling;}};var cg={};if(aC){node2obj(aC.firstChild,cg);}return cg;}}); 