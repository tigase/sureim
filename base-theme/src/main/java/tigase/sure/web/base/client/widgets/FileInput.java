/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.widgets;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import tigase.sure.web.base.client.widgets.file.FileList;

/**
 *
 * @author andrzej
 */
public class FileInput extends FileUpload {
        private ChangeHandler handler;
        
        public FileList getFiles() {
                return getFiles(getElement());
        }
        
        public void dispatch(NativeEvent event) {
                if (this.handler != null) {
                        this.handler.onChange(null);
                }
        }
        
        public void setChangeHandler(ChangeHandler handler) {
                this.handler = handler;
                registerEvent(this, getElement());
        }
        
        private static final native FileList getFiles(Element element) /*-{
                return element.files;
        }-*/;
        
        private static final native void registerEvent(FileInput i, Element e) /*-{
                var x = i;
                e.addEventListener('change', function(evt) {
                        console.log('changed!!');
                        x.@tigase.sure.web.base.client.widgets.FileInput::dispatch(Lcom/google/gwt/dom/client/NativeEvent;)(evt);
                }, false);
        }-*/;
        
}
