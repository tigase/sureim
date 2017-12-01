/*
 * MessagesDataProvider.java
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
package tigase.sure.web.site.client.archive;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import tigase.sure.web.site.client.ClientFactory;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ChatResultSet;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ResultSet;

/**
 *
 * @author andrzej
 */
public class MessagesDataProvider extends AsyncDataProvider implements MessageArchivingListener {

        private final Controller controller;

        public MessagesDataProvider(Controller controller) {
                this.controller = controller;
                controller.addMessageArchivingListener(this);
        }

        @Override
        protected void onRangeChanged(HasData display) {
                Range range = display.getVisibleRange();

                Integer start = range.getStart();
                if (start == 0) {
                        start = null;
                } else {
                        start--;
                }

                controller.getMessages(null, start);
        }

        @Override
        public void onReceiveChat(ChatResultSet rs) {
                if (rs == null) {
                        updateRowCount(0, true);
                        return;
                }

                Integer first = 0;
                if (rs.getIndex() != null) {
                        first = rs.getIndex();
                }
                updateRowData(first, rs.getItems());
                updateRowCount(rs.getCount(), true);

//        messages.setVisible(true);
//        messages.glass.hide();
        }

//    @Override
//    public void onReceiveSetChat(Packet iq, ChatResultSet rs) {
//        // Not used
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
        @Override
        public void onReceiveCollections(ResultSet<Chat> rs) {
                // Not used
                //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dispose() {
                controller.removeMessageArchivingListener(this);
        }
}
