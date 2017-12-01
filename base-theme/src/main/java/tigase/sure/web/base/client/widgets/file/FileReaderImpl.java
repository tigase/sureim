/*
 * FileReaderImpl.java
 *
 * Tigase XMPP Web Client
 * Copyright (C) 2012-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.sure.web.base.client.widgets.file;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author andrzej
 */
public class FileReaderImpl
		extends JavaScriptObject {

	public static final native FileReaderImpl newInstance() /*-{
         return new FileReader();
        }-*/;

	protected FileReaderImpl() {
	}

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