/*
 * ContactList.java
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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import tigase.sure.web.site.client.ClientFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;

/**
 *
 * @author andrzej
 */
public class ContactList extends ResizeComposite {

        private final ClientFactory factory;
        
        private final ScrollPanel scroll;
        private final ListDataProvider<RosterItem> roster;
        private final CellList<RosterItem> widget;
		private final RosterHandler rosterHandler;
        
        public ContactList(ClientFactory factory_) {
                factory = factory_;
                
                roster = new ListDataProvider<RosterItem>();
                widget = new CellList<RosterItem>(new RosterItemCell());
                widget.setSelectionModel(new SingleSelectionModel());
                widget.setRowCount(300);
                widget.setVisibleRange(0, 1000);
                
                roster.addDataDisplay(widget);
        
                scroll = new ScrollPanel(widget);
                
				rosterHandler = new RosterHandler();
				factory.jaxmpp().getEventBus().addHandler(RosterModule.ItemAddedHandler.ItemAddedEvent.class, rosterHandler);
				factory.jaxmpp().getEventBus().addHandler(RosterModule.ItemRemovedHandler.ItemRemovedEvent.class, rosterHandler);
				factory.jaxmpp().getEventBus().addHandler(RosterModule.ItemUpdatedHandler.ItemUpdatedEvent.class, rosterHandler);
                
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
		
		private class RosterHandler implements RosterModule.ItemAddedHandler, RosterModule.ItemRemovedHandler, RosterModule.ItemUpdatedHandler {

		@Override
		public void onItemAdded(SessionObject sessionObject, RosterItem item, Set<String> modifiedGroups) {
			addRosterItem(item);
		}

		@Override
		public void onItemRemoved(SessionObject sessionObject, RosterItem item, Set<String> modifiedGroups) {
			removeRosterItem(item);
		}

		@Override
		public void onItemUpdated(SessionObject sessionObject, RosterItem item, RosterModule.Action action, Set<String> modifiedGroups) {
			removeRosterItem(item);
			addRosterItem(item);
		}
			
		}
}
