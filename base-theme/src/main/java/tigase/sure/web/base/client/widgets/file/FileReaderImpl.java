/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.widgets.file;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author andrzej
 */
public class FileReaderImpl extends JavaScriptObject {

	protected FileReaderImpl() {
	}
	public static final native FileReaderImpl newInstance() /*-{
         return new FileReader();
        }-*/;
	public final native short getReadyState() /*-{
      return this.readyState;
	}-*/; 
	public final native String getStringResult() /*-{
      return this.result;
	}-*/; 
//	public final native ArrayBuffer getArrayBufferResult() /*-{
//    return this.result;
//	}-*/; 
//	public final native FileError getError() /*-{
//      return this.error;
//	}-*/; 
	public final native void abort() /*-{
	  this.abort();
	}-*/; 
	public final native void readAsBinaryString(File fileBlob) /*-{
	  this.readAsBinaryString(fileBlob);
	}-*/; 
	public final native void readAsText(File fileBlob) /*-{
	  this.readAsText(fileBlob);
	}-*/; 
	public final native void readAsText(File fileBlob, String encoding) /*-{
	  this.readAsText(fileBlob, encoding);
	}-*/; 
	public final native void readAsDataURL(File fileBlob) /*-{
	  this.readAsDataURL(fileBlob);
	}-*/;
	public final native void readAsArrayBuffer(File fileBlob) /*-{
	  this.readAsArrayBuffer(fileBlob);
	}-*/;
        
}