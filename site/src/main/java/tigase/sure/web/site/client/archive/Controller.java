/*
 * Controller.java
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

import com.google.gwt.user.datepicker.client.CalendarUtil;
import tigase.sure.web.site.client.ClientFactory;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ChatResultSet;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.MessageArchivingModule;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.MessageArchivingModule.CollectionAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.MessageArchivingModule.ItemsAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ResultSet;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class Controller {

        private final ClientFactory factory;
        private JID jid;
        private final MessagesDataProvider provider;
        private final Set<MessageArchivingListener> listeners = new HashSet<MessageArchivingListener>();
        private Date date = null;
        private int pageSize = 0;
        private final CollectionAsyncCallback collectionCallback = new CollectionAsyncCallback() {

                @Override
                protected void onCollectionReceived(ResultSet<Chat> rs) throws XMLException {
                        for (MessageArchivingListener listener : listeners) {
                                listener.onReceiveCollections(rs);
                        }
                }

                @Override
                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                        for (MessageArchivingListener listener : listeners) {
                                listener.onReceiveCollections(null);
                        }
                }

                @Override
                public void onTimeout() throws JaxmppException {
                        for (MessageArchivingListener listener : listeners) {
                                listener.onReceiveCollections(null);
                        }
                }
        };
        private final ItemsAsyncCallback itemsCallback = new ItemsAsyncCallback() {

                @Override
                protected void onItemsReceived(ChatResultSet rs) throws XMLException {
                        for (MessageArchivingListener listener : listeners) {
                                listener.onReceiveChat(rs);
                        }
                }

                @Override
                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                        for (MessageArchivingListener listener : listeners) {
                                listener.onReceiveChat(null);
                        }
                }

                @Override
                public void onTimeout() throws JaxmppException {
                        for (MessageArchivingListener listener : listeners) {
                                listener.onReceiveChat(null);
                        }
                }
        };

        public Controller(ClientFactory factory) {
                this.factory = factory;

//        factory.getSession().getMessageArchivingPlugin().addMessageArchivingListener(this);
                this.provider = new MessagesDataProvider(this);
        }

        public void setJid(JID jid) {
                this.jid = jid;
                try {
                        this.collectionCallback.onError(null, ErrorCondition.gone);
                } catch (JaxmppException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public void setPageSize(int size) {
                this.pageSize = size;
        }

        void getMessages(Date date, Integer index) {
                if (date != null) {
                        this.date = date;
                }

                if (this.date == null) {
                        return;
                }

                Date end = CalendarUtil.copyDate(this.date);
                CalendarUtil.addDaysToDate(end, 1);

                try {
                        factory.jaxmpp().getModulesManager().getModule(MessageArchivingModule.class).retriveCollection(this.jid, this.date, end, null, index, pageSize, itemsCallback);
                } catch (Exception ex) {
                        Logger.getLogger("MessageArchivingController").log(Level.WARNING, "exception requesting messages", ex);
                }
        }

        public void listCollections(Date date, String afterId) {
                if (jid == null)
                        return;
                
                Date end = CalendarUtil.copyDate(date);
                CalendarUtil.addMonthsToDate(end, 1);
                CalendarUtil.addDaysToDate(end, -1);

                try {
                        factory.jaxmpp().getModulesManager().getModule(MessageArchivingModule.class).listCollections(jid, date, end, null, collectionCallback);
                } catch (Exception ex) {
                        Logger.getLogger("MessageArchivingController").log(Level.WARNING, "exception requesting collections", ex);
                }
        }

        public ClientFactory getFactory() {
                return factory;
        }

        public MessagesDataProvider getMessageProvider() {
                return provider;
        }

        public void dispose() {
                provider.dispose();
        }

//    @Override
//    public void onReceiveSetChat(Packet iq, ChatResultSet rs) {
//        for (MessageArchivingListener l : listeners) {
//            l.onReceiveChat(iq, rs);
//        }
//    }
        public void addMessageArchivingListener(MessageArchivingListener listener) {
                if (!listeners.contains(listener)) {
                        listeners.add(listener);
                }
        }

        public void removeMessageArchivingListener(MessageArchivingListener listener) {
                listeners.remove(listener);
        }
}
