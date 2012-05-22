/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.widgets.file;

/**
 *
 * @author andrzej
 */
public class File {

	public final long getSize() {
		String size = getSize_();
		return Long.parseLong(size);
	}

	private final native String getSize_() /*-{
                return "" + this.size;
	}-*/;   
        
	public final native String getType() /*-{
                return this.type;
	}-*/;        
        
}
