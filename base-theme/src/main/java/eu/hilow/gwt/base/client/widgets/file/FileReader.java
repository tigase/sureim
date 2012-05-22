/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.widgets.file;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author andrzej
 */
public class FileReader {
	public static final short EMPTY = 0;
	public static final short LOADING = 1;
	public static final short DONE = 2;

        private AsyncCallback<String> handler;

        private final FileReaderImpl impl;
        
	protected FileReader() {
                impl = FileReaderImpl.newInstance();
	}
        
	public static final FileReader newInstance() {
                return new FileReader();
	};
        
	public final short getReadyState() {
                return impl.getReadyState();
	}
        
	public final String getStringResult() {
                return impl.getStringResult();
        }

        public final void abort() {
                impl.abort();
        }
        
	public final void readAsBinaryString(File fileBlob) {
                impl.readAsBinaryString(fileBlob);
	} 
        
	public final void readAsText(File fileBlob) {
                impl.readAsText(fileBlob);
	}
        
	public final void readAsText(File fileBlob, String encoding) {
                impl.readAsText(fileBlob, encoding);
	} 
        
	public final void readAsDataURL(File fileBlob) {
                impl.readAsDataURL(fileBlob);
	}
        
	public final void readAsArrayBuffer(File fileBlob) {
                impl.readAsArrayBuffer(fileBlob);
	}
        
	public final native void registerEvent(FileReaderImpl reader, String eventType) /*-{
                console.log('setting onloadend');
                var x = this;
		var handler = function(evt) {
                        console.log(reader);
	                x.@eu.hilow.gwt.base.client.widgets.file.FileReader::dispatch(Lcom/google/gwt/dom/client/NativeEvent;)(evt);
		};
		reader["on" + eventType] = handler;
                console.log('onloadend set');
	}-*/;

        public void dispatch(NativeEvent event) {
                this.handler.onSuccess(getResultFromEvent(event));
        }        

        private final native String getResultFromEvent(NativeEvent event) /*-{
                return event.target.result;
        }-*/;
        
	public void addLoadEndHandler(AsyncCallback<String> handler) {
	    this.handler = handler;
            registerEvent(this.impl, "loadend");
	}        
}