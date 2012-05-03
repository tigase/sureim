/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.archive;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import eu.hilow.xode.web.client.ClientFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule.RosterEvent;

/**
 *
 * @author andrzej
 */
public class ContactList extends ResizeComposite {

        private final ClientFactory factory;
        
        private final ScrollPanel scroll;
        private final ListDataProvider<RosterItem> roster;
        private final CellList<RosterItem> widget;
        
        public ContactList(ClientFactory factory_) {
                factory = factory_;
                
                roster = new ListDataProvider<RosterItem>();
                widget = new CellList<RosterItem>(new RosterItemCell());
                widget.setSelectionModel(new SingleSelectionModel());
                widget.setRowCount(300);
                widget.setVisibleRange(0, 1000);
                
                roster.addDataDisplay(widget);
        
                scroll = new ScrollPanel(widget);
                
                RosterModule rosterModule = factory.jaxmpp().getModulesManager().getModule(RosterModule.class);
                rosterModule.addListener(RosterModule.ItemAdded, new Listener<RosterEvent>() {
                        public void handleEvent(RosterEvent be) throws JaxmppException {
                                if (be.getItem() != null) {
                                        addRosterItem(be.getItem());
                                }
                        }                        
                });
                rosterModule.addListener(RosterModule.ItemRemoved, new Listener<RosterEvent>() {
                        public void handleEvent(RosterEvent be) throws JaxmppException {
                                if (be.getItem() != null) {
                                        removeRosterItem(be.getItem());
                                }
                        }                        
                });
                rosterModule.addListener(RosterModule.ItemUpdated, new Listener<RosterEvent>() {
                        public void handleEvent(RosterEvent be) throws JaxmppException {
                                if (be.getItem() != null) {
                                        removeRosterItem(be.getItem());
                                        addRosterItem(be.getItem());
                                }
                        }                        
                });
                
                initWidget(scroll);
        }
        
        public void addRosterItem(RosterItem ri) {
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
        
        public void removeRosterItem(RosterItem ri) {
                if (ri == null)
                        return;
                
                roster.getList().remove(ri);
                
        }
                
        public void addSelectionChangeHandler(final SelectionHandler handler) {
                widget.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                        public void onSelectionChange(SelectionChangeEvent event) {
                                RosterItem ri = (RosterItem) ((SingleSelectionModel) widget.getSelectionModel()).getSelectedObject();
                                handler.itemSelected(ri != null ? ri.getJid() : null);
                        }
                });
        }
        
        public static interface SelectionHandler {
                
                void itemSelected(BareJID jid);
                
        }
        
        private class RosterItemCell extends AbstractCell<RosterItem> {

                @Override
                public void render(Context context, RosterItem value, SafeHtmlBuilder sb) {
                        if (value != null) {
                                sb.appendHtmlConstant("<table class='" + factory.theme().style().rosterItem() 
                                        + "'><tr><td class='" + factory.theme().style().rosterItemName() + "'>");
                                sb.appendEscaped(value.getName());
                                sb.appendHtmlConstant("</td></tr><tr><td class='" + factory.theme().style().rosterItemStatus() + "'>");
                                sb.appendEscaped("<" + value.getJid() + ">");
                                sb.appendHtmlConstant("</td></tr></table>");
                        }
                }
                
        }
}
