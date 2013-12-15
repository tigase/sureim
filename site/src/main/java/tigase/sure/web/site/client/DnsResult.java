/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author andrzej
 */
public class DnsResult extends JavaScriptObject {

   protected DnsResult() {}

   public final native String getDomain() /*-{
     return this.domain;
   }-*/;
   
   public final native JsArray<DnsEntry> getC2S() /*-{
     return this.c2s;
   }-*/;        

   public final native JsArray<DnsEntry> getBosh() /*-{
	 return this.bosh;
   }-*/;
   
   public final native JsArray<DnsEntry> getWebSocket() /*-{
	 return this.websocket;
   }-*/;   
   
   public final native String next() /*-{
	   var urls = [];
	   for (var i=0; i<this.websocket.length; i++) {
		   if((!this.websocket[i].failed) && (this.websocket[i].url.indexOf("wss://") == 0)) {
			   urls.push(this.websocket[i].url);
		   }
	   }
	   if (urls.length > 0) {
		   return urls[Math.floor(Math.random()*urls.length)];
	   }
	   for (var i=0; i<this.websocket.length; i++) {
		   if(!this.websocket[i].failed) {
			   urls.push(this.websocket[i].url);
		   }
	   }
	   if (urls.length > 0) {
		   return urls[Math.floor(Math.random()*urls.length)];
	   }
	   for (var i=0; i<this.bosh.length; i++) {
		   if (!this.bosh[i].failed) {
			   urls.push(this.bosh[i].url);
		   }
	   }	
	   if (urls.length > 0) {
		   return urls[Math.floor(Math.random()*urls.length)];
	   }
	   return null;
   }-*/;
   
   public final native boolean hasMore() /*-{
	   for (var i=0; i<this.websocket.length; i++) {
		   if(!this.websocket[i].failed) {
			   return true;
		   }
	   }
	   for (var i=0; i<this.bosh.length; i++) {
		   if (!this.bosh[i].failed) {
			   return true;
		   }
	   }
	   return false;
   }-*/;
   
   public final native void connectionFailed(String url) /*-{
	   for (var i=0; i<this.websocket.length; i++) {
	       if (this.websocket[i].url == url) {
		       this.websocket[i].failed = true;
		   }
	   }
	   for (var i=0; i<this.bosh.length; i++) {
	       if (this.bosh[i].url == url) {
		       this.bosh[i].failed = true;
		   }
	   }
   }-*/;
   
   public final native String getUrlForHost(String host) /*-{
	   for (var i=0; i<this.websocket.length; i++) {
	       if (this.websocket[i].url.indexOf("wss://"+host) >= 0)
		       return this.websocket[i].url;
	   }
	   for (var i=0; i<this.websocket.length; i++) {
	       if (this.websocket[i].url.indexOf("ws://"+host) >= 0)
		       return this.websocket[i].url;
	   }
	   for (var i=0; i<this.bosh.length; i++) {
	       if (this.bosh[i].url.indexOf("://"+host) >= 0) {
		       return this.bosh[i].url;
		   }
	   }
	   return null;
   }-*/;
}
