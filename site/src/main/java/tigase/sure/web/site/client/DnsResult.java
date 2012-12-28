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
   
   public final native JsArray<DnsEntry> getEntries() /*-{
     return this.entries;
   }-*/;        
        
}
