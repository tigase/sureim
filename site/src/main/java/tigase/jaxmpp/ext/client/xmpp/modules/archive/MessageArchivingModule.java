/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.jaxmpp.ext.client.xmpp.modules.archive;

import tigase.jaxmpp.ext.client.xmpp.modules.archive.Item.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.PacketWriter;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.XmppModule;
import tigase.jaxmpp.core.client.criteria.Criteria;
import tigase.jaxmpp.core.client.criteria.ElementCriteria;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Observable;
import tigase.jaxmpp.core.client.xml.DefaultElement;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.AbstractIQModule;
import tigase.jaxmpp.core.client.xmpp.modules.ObservableAware;
import tigase.jaxmpp.core.client.xmpp.modules.PacketWriterAware;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import tigase.jaxmpp.core.client.xmpp.utils.DateTimeFormat;

/**
 *
 * @author andrzej
 */
public class MessageArchivingModule implements XmppModule, PacketWriterAware {

        private static final String ARCHIVE_XMLNS = "urn:xmpp:archive";                
        
        private static final Criteria CRIT = ElementCriteria.name("iq").add(ElementCriteria.xmlns(ARCHIVE_XMLNS));
        private static final DateTimeFormat format = new DateTimeFormat();

        private PacketWriter writer = null;
        
        public void setPacketWriter(PacketWriter packetWriter) {
                this.writer = packetWriter;
        }

        public void process(Element element) throws XMPPException, XMLException, JaxmppException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public static abstract class CollectionAsyncCallback implements AsyncCallback {

                @Override
                public void onSuccess(final Stanza stanza) throws XMLException {
                        ResultSet<Chat> rs = new ResultSet<Chat>();

                        Element rsm = stanza.getChildrenNS("set", "http://jabber.org/protocol/rsm");
                        if (rsm != null) {
                                rs.process(rsm);
                        }

                        for (Element it : stanza.getChildrenNS("list", ARCHIVE_XMLNS).getChildren()) {
                                if ("chat".equals(it.getName())) {
                                        Chat chat = new Chat();
                                        chat.process(it, format);
                                        rs.getItems().add(chat);
                                }
                        }
                        onCollectionReceived(rs);
                }

                protected abstract void onCollectionReceived(final ResultSet<Chat> vcard) throws XMLException;
        }

        public static abstract class ItemsAsyncCallback implements AsyncCallback {

                @Override
                public void onSuccess(final Stanza iq) throws XMLException {
                        ChatResultSet rs = new ChatResultSet();

                        Element chat = iq.getChildrenNS("chat", ARCHIVE_XMLNS);
                        if (chat != null) {
                                rs.getChat().process(chat, format);
                        }

                        Element rsm = chat.getChildrenNS("set", "http://jabber.org/protocol/rsm");
                        if (rsm != null) {
                                rs.process(rsm);
                        }

                        List<Item> resultItems = new ArrayList<Item>();
                        rs.setItems(resultItems);

                        Date cd = rs.getChat().getStart();

                        for (Element item : chat.getChildren()) {
                                String body = getChildValue(item, "body");
                                String secsTmp = item.getAttribute("secs");
                                String utcTmp = item.getAttribute("utc");
                                Date time = null;

                                if (secsTmp != null) {
                                        long msecs = Long.parseLong(secsTmp) * 1000;
                                        time = new Date(cd.getTime() + msecs);
                                }
                                else if (utcTmp != null) {
                                        try {
                                                time = format.parse(utcTmp);
                                        }
                                        catch (Exception e) {
                                        }
                                }

                                if ("from".equals(item.getName())) {
                                        Item it = new Item(Type.FROM, time, body);
                                        resultItems.add(it);
                                }
                                else if ("to".equals(item.getName())) {
                                        Item it = new Item(Type.TO, time, body);
                                        resultItems.add(it);
                                }
                        }
                        
                        onItemsReceived(rs);
                }
                
                private String getChildValue(Element packet, String name) throws XMLException {
                        List<Element> vlist = packet.getChildren(name);
                        if (vlist == null || vlist.isEmpty())
                                return null;
                        
                        Element v = vlist.get(0);
                        if (v != null) {
                                return v.getValue();
                        }
                        else {
                                return null;
                        }
                }

                protected abstract void onItemsReceived(final ChatResultSet chat) throws XMLException;
        }

        public static abstract class SettingsAsyncCallback implements AsyncCallback {

                public void onSuccess(Stanza stanza) throws JaxmppException {
                        Element pref = stanza.getChildrenNS("pref", ARCHIVE_XMLNS);
                        List<Element> children = pref.getChildren("auto");
                        boolean auto = false;
                        if (children != null && !children.isEmpty()) {
                                auto = Boolean.parseBoolean(children.get(0).getAttribute("save"));
                        }
                        onSuccess(auto);
                }
                
                public abstract void onSuccess(boolean autoArchive);
        }
        
        public MessageArchivingModule() {
        }

        public void setAutoArchive(boolean enable) throws JaxmppException {
                IQ iq = IQ.create();
                iq.setType(StanzaType.get);
                
                Element autoEl = new DefaultElement("auto", null, ARCHIVE_XMLNS);
                autoEl.setAttribute("save", String.valueOf(enable));
                iq.addChild(autoEl);
                
                writer.write(iq);
                
        }
        
        public void getSettings(final SettingsAsyncCallback callback) throws XMLException, JaxmppException {
                IQ iq = IQ.create();
                iq.setType(StanzaType.get);
                
                Element prefEl = new DefaultElement("pref", null, ARCHIVE_XMLNS);
                iq.addChild(prefEl);
                
                writer.write(iq, null, callback);
        }
        
        public void listCollections(final JID withJid, final Date startTime, final Date endTime, final String afterId, final CollectionAsyncCallback callback) throws XMLException, JaxmppException {
 		IQ iq = IQ.create();
                iq.setType(StanzaType.get);
                
 		Element retrieve = new DefaultElement("list", null, ARCHIVE_XMLNS);
                iq.addChild(retrieve);
 		retrieve.setAttribute("with", withJid.toString());
 		retrieve.setAttribute("start", format.format(startTime));

                if (endTime != null)
                    retrieve.setAttribute("end", format.format(endTime));

 		Element set = new DefaultElement("set", null, "http://jabber.org/protocol/rsm");
                retrieve.addChild(set);

 		set.addChild(new DefaultElement("max", "100", null));

                if (afterId != null) {
                    set.addChild(new DefaultElement("after", afterId, null));
                }

                writer.write(iq, null, callback);
        }

	public void retriveCollection(final JID withJid, final Date startTime, final Date endTime, String afterId, Integer maxCount, final ItemsAsyncCallback callback) throws XMLException, JaxmppException {
		IQ iq = IQ.create();
                iq.setType(StanzaType.get);

		Element retrieve = new DefaultElement("retrieve", null, ARCHIVE_XMLNS);
                iq.addChild(retrieve);
		retrieve.setAttribute("with", withJid.toString());
                retrieve.setAttribute("start", format.format(startTime));

		Element set = new DefaultElement("set", null, "http://jabber.org/protocol/rsm");

		set.addChild(new DefaultElement("max", (maxCount != null ? Integer.toString(maxCount) : "100"), null));

                if (afterId != null) {
                    set.addChild(new DefaultElement("after", afterId, null));
                }

                writer.write(iq, null, callback);                
	}
        
        @Override
        public Criteria getCriteria() {
                return CRIT;
        }

        @Override
        public String[] getFeatures() {
                return null;
        }
}
