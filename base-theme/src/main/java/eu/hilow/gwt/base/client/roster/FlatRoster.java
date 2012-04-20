/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.roster;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import eu.hilow.gwt.base.client.AvatarChangedHandler;
import eu.hilow.gwt.base.client.ClientFactory;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.PresenceEvent;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 *
 * @author andrzej
 */
public class FlatRoster extends ResizeComposite implements Listener<PresenceEvent>, AvatarChangedHandler {
        
        public static interface RosterItemClickHandler {
                
                void itemClicked(RosterItem ri);
                
        }
        
        private final ClientFactory factory;
        
        private final ScrollPanel scroll;
        private final ListDataProvider<RosterItem> roster;
        private final CellList<RosterItem> widget;
        
        private final HashMap<String,RosterItemClickHandler> handlers;
        private final HashSet<String> itemEvents;
        
        public FlatRoster(ClientFactory factory) {
                this.factory = factory;
                
                this.handlers = new HashMap<String,RosterItemClickHandler>();
                this.itemEvents = new HashSet<String>();
                itemEvents.add("click");
                itemEvents.add("mouseup");
                itemEvents.add("contextmenu");
                
                Jaxmpp jaxmpp = factory.jaxmpp();
                                
                roster = new ListDataProvider<RosterItem>();
                widget = new CellList<RosterItem>(new RosterItemCell());
                widget.setRowCount(300);
//                widget.sinkEvents(Event.ONCONTEXTMENU);
//                widget.sinkEvents(Event.ONMOUSEUP);
                
                roster.addDataDisplay(widget);
                
                jaxmpp.getModulesManager().getModule(PresenceModule.class).addListener(PresenceModule.ContactChangedPresence, this);
                jaxmpp.getModulesManager().getModule(PresenceModule.class).addListener(PresenceModule.ContactAvailable, this);
                jaxmpp.getModulesManager().getModule(PresenceModule.class).addListener(PresenceModule.ContactUnavailable, this);
               
                scroll = new ScrollPanel(widget);
                
                initWidget(scroll);
        }

        public void handleEvent(PresenceEvent be) throws JaxmppException {
                updateItem(be.getJid().getBareJid());
        }
        
        public void updateItem(BareJID jid) {
                RosterItem ri = factory.jaxmpp().getRoster().get(jid);
                              
                if (ri == null)
                        return;
                
                roster.getList().remove(ri);
                
                try {
                        if (!factory.jaxmpp().getPresence().isAvailable(jid))
                                return;
                } catch (XMLException ex) {
                        Logger.getLogger(FlatRoster.class.getName()).log(Level.SEVERE, null, ex);
                }
                                
                List<RosterItem> list = new ArrayList<RosterItem>(roster.getList());//factory.jaxmpp().getRoster().getAll();
                list.add(ri);
                Collections.sort(list, new Comparator<RosterItem>() {

                        public int compare(RosterItem r1, RosterItem r2) {
                                if (r1 == null)
                                        return -1;
                                if (r2 == null)
                                        return 1;
                                
                                return r1.getName().compareToIgnoreCase(r2.getName());
                        }
                });
                
                int idx = list.indexOf(ri);
                if (idx < 0)
                        idx  = 0;
                if (idx >= 0) {
                        roster.getList().add(idx, ri);
                }
                roster.refresh();
        }

        public void avatarChanged(JID jid) {
                updateItem(jid.getBareJid());
        }

        public void addClickHandler(String action, RosterItemClickHandler handler) {
                handlers.put(action, handler);
        }
        
        public void removeClickHandler(String action, RosterItemClickHandler handler) {
                handlers.remove(action);
        }
        
        private class RosterItemCell extends AbstractCell<RosterItem> {

                @Override
                public void render(Context context, RosterItem value, SafeHtmlBuilder sb) {
                        if (value != null) {
                                try {
                                        sb.appendHtmlConstant("<table class='" + factory.theme().style().rosterItem() 
                                                + "'><tr><td colspan='2' class='" + factory.theme().style().rosterItemName() + "'>");
                                        sb.appendEscaped(value.getName());
                                        sb.appendHtmlConstant("</td><td rowspan='2' width='32px'>");
                                        Image avatar = factory.avatarFactory().getAvatarForJid(value.getJid());
                                        avatar.setSize("32px", "32px");
                                        sb.appendHtmlConstant(avatar.toString());
                                        sb.appendHtmlConstant("</td></tr><tr><td width='16px'>");

                                        Presence p = factory.jaxmpp().getPresence().getBestPresence(value.getJid());
                                        if (p != null && p.getShow() != null) {
                                                ImageResource res = null;
                                                switch (p.getShow()) {
                                                        case online:
                                                        case chat:
                                                                res = factory.theme().statusAvailable();
                                                                break;
                                                        case away:                                                                
                                                        case xa:
                                                                res = factory.theme().statusAway();
                                                        case dnd:
                                                                res = factory.theme().statusBusy();
                                                                break;                                                                
                                                }
                                                Image status = new Image(res);
                                                status.setSize("16px", "16px");
                                                sb.appendHtmlConstant(status.toString());
                                        }
                                        sb.appendHtmlConstant("</td><td class='" + factory.theme().style().rosterItemStatus() + "'>");
                                        if (p != null && p.getStatus() != null) {
                                                String desc = p.getStatus();
                                                if (desc.length() > 30) {
                                                        desc = desc.substring(0, 27) + "...";
                                                }
                                                sb.appendEscaped(desc);
                                        }
                                        sb.appendHtmlConstant("</td></tr></table>");
                                } catch (XMLException ex) {
                                        
                                }
                        }
                }

                @Override
                public Set<String> getConsumedEvents() {
                        return itemEvents;
                }
                
                @Override
                public void onBrowserEvent(Context context, Element parent, RosterItem value, NativeEvent event, ValueUpdater<RosterItem> valueUpdater) {
                        RosterItemClickHandler handler = handlers.get(event.getType());
                        Logger.getLogger("FlatRoster").warning("received event of type = " + event.getType());
                        if (handler != null) {
                                handler.itemClicked(value);
                                event.stopPropagation();
                                event.preventDefault();
                        }
                        else {
                                super.onBrowserEvent(context, parent, value, event, valueUpdater);
                        }
                }
                
        }
        
}
