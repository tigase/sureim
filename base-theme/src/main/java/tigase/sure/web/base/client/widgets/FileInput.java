/*
 * FileInput.java
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
