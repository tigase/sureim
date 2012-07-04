/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author andrzej
 */
public class DnsEntry extends JavaScriptObject {

   protected DnsEntry() {}
   
   public final native int getPort() /*-{
           return this.port;
   }-*/;
     
   public final native String getIp() /*-{
           return this.ip;
   }-*/;

   public final native String getResultHost() /*-{
           return this.resultHost;
   }-*/;
   
}
