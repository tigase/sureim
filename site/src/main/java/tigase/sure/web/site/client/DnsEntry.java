/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 *
 * @author andrzej
 */
public class DnsEntry extends JavaScriptObject {

   protected DnsEntry() {}
   
   public final native int getPort() /*-{
           return this.port;
   }-*/;

   public final native JsArrayString getIps() /*-{
           return this.ip;
   }-*/;

   public final native String getHost() /*-{
           return this.host;
   }-*/;
   
   public final native String getUrl() /*-{
	       return this.url;
   }-*/;
}
