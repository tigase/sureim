/**
 * Sure.IM base theme library - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author andrzej
 */
public class FileReader {

	public static final short EMPTY = 0;
	public static final short LOADING = 1;
	public static final short DONE = 2;
	private final FileReaderImpl impl;
	private AsyncCallback<String> handler;

	public static final FileReader newInstance() {
		return new FileReader();
	}

	protected FileReader() {
		impl = FileReaderImpl.newInstance();
	}

	;

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
	                x.@tigase.sure.web.base.client.widgets.file.FileReader::dispatch(Lcom/google/gwt/dom/client/NativeEvent;)(evt);
		};
		reader["on" + eventType] = handler;
                console.log('onloadend set');
	}-*/;

	public void dispatch(NativeEvent event) {
		this.handler.onSuccess(getResultFromEvent(event));
	}

	public void addLoadEndHandler(AsyncCallback<String> handler) {
		this.handler = handler;
		registerEvent(this.impl, "loadend");
	}

	private final native String getResultFromEvent(NativeEvent event) /*-{
                return event.target.result;
        }-*/;
}