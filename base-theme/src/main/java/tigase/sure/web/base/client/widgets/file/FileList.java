/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.widgets.file;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author andrzej
 */
public class FileList extends JavaScriptObject {
        
        public final native int getLength() /*-{
                return this.length;
        }-*/;
        
        public final native File getItem(int index) /*-{
                return this[index];
        }-*/;
}
