/*
 * MessageDialog.java
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
package tigase.sure.web.site.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author andrzej
 */
public class MessageDialog extends DialogBox {
        
        private final ClientFactory factory;
        
        public MessageDialog(ClientFactory factory_, String title, String msg) {
                super(true);
                factory = factory_;
   
                setStyleName("dialogBox");
                setTitle(title);

                FlexTable table = new FlexTable();
                Label label = new Label(title);
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                table.setWidget(0, 0, label);
                
                label = new Label(msg);
                table.setWidget(1, 0, label);
                                
                Button close = new Button(factory.baseI18n().close());
                close.setStyleName(factory.theme().style().button());
                close.addStyleName(factory.theme().style().buttonDefault());
                close.addStyleName(factory.theme().style().right());
                table.setWidget(2, 0, close);
                close.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                hide();
                        }
                        
                });
                
                setWidget(table);                
        }
        
}
