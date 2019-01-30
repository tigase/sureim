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
package tigase.sure.web.base.client.roster;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem.Subscription;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.sure.web.base.client.AvatarChangedHandler;
import tigase.sure.web.base.client.ClientFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class FlatRoster
		extends ResizeComposite
		implements AvatarChangedHandler {

	private final ClientFactory factory;
	private final HashMap<String, RosterItemClickHandler> handlers;
	private final HashSet<String> itemEvents;
	private final PresenceHandler presenceListener = new PresenceHandler();
	private final ListDataProvider<RosterItem> roster;
	private final RosterHandler rosterListener = new RosterHandler();
	private final ScrollPanel scroll;
	private final CellList<RosterItem> widget;
	public FlatRoster(ClientFactory factory) {
		this.factory = factory;

		this.handlers = new HashMap<String, RosterItemClickHandler>();
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

		jaxmpp.getModulesManager().getModule(PresenceModule.class).addContactAvailableHandler(presenceListener);
		jaxmpp.getModulesManager().getModule(PresenceModule.class).addContactUnavailableHandler(presenceListener);
		jaxmpp.getModulesManager().getModule(PresenceModule.class).addContactChangedPresenceHandler(presenceListener);

		jaxmpp.getEventBus().addHandler(RosterModule.ItemAddedHandler.ItemAddedEvent.class, rosterListener);
		jaxmpp.getEventBus().addHandler(RosterModule.ItemRemovedHandler.ItemRemovedEvent.class, rosterListener);
		jaxmpp.getEventBus().addHandler(RosterModule.ItemUpdatedHandler.ItemUpdatedEvent.class, rosterListener);

		scroll = new ScrollPanel(widget);

		initWidget(scroll);
	}

	public void updateItem(BareJID jid) {
		RosterItem ri = RosterModule.getRosterStore(factory.sessionObject()).get(jid);

		if (ri == null) {
			return;
		}

		updateItem(ri);
	}

	public void updateItem(RosterItem ri) {
		roster.getList().remove(ri);

		if (ri.getSubscription() == Subscription.remove) {
			roster.refresh();
			return;
		}

		try {
			if (!PresenceModule.getPresenceStore(factory.sessionObject()).isAvailable(ri.getJid())) {
				return;
			}
		} catch (XMLException ex) {
			Logger.getLogger(FlatRoster.class.getName()).log(Level.SEVERE, null, ex);
		}

		List<RosterItem> list = new ArrayList<RosterItem>(roster.getList());//factory.jaxmpp().getRoster().getAll();
		list.add(ri);
		Collections.sort(list, new Comparator<RosterItem>() {

			public int compare(RosterItem r1, RosterItem r2) {
				if (r1 == null) {
					return -1;
				}
				if (r2 == null) {
					return 1;
				}

				String name1 = r1.getName();
				if (name1 == null) {
					name1 = r1.getJid().toString();
				}
				String name2 = r2.getName();
				if (name2 == null) {
					name2 = r2.getJid().toString();
				}

				return name1.compareToIgnoreCase(name2);
			}
		});

		int idx = list.indexOf(ri);
		if (idx < 0) {
			idx = 0;
		}
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

	public static interface RosterItemClickHandler {

		void itemClicked(RosterItem ri, int left, int top);

	}

	private class PresenceHandler
			implements PresenceModule.ContactChangedPresenceHandler,
					   PresenceModule.ContactAvailableHandler,
					   PresenceModule.ContactUnavailableHandler {

		@Override
		public void onContactChangedPresence(SessionObject sessionObject, Presence stanza, JID jid, Presence.Show show,
											 String status, Integer priority) throws JaxmppException {
			updateItem(jid.getBareJid());
		}

		@Override
		public void onContactAvailable(SessionObject sessionObject, Presence stanza, JID jid, Presence.Show show,
									   String status, Integer priority) throws JaxmppException {
			updateItem(jid.getBareJid());
		}

		@Override
		public void onContactUnavailable(SessionObject sessionObject, Presence stanza, JID jid, String status) {
			updateItem(jid.getBareJid());
		}

	}

	private class RosterHandler
			implements RosterModule.ItemAddedHandler, RosterModule.ItemRemovedHandler, RosterModule.ItemUpdatedHandler {

		@Override
		public void onItemAdded(SessionObject sessionObject, RosterItem item, Set<String> modifiedGroups) {
			updateItem(item);
		}

		@Override
		public void onItemRemoved(SessionObject sessionObject, RosterItem item, Set<String> modifiedGroups) {
			updateItem(item);
		}

		@Override
		public void onItemUpdated(SessionObject sessionObject, RosterItem item, RosterModule.Action action,
								  Set<String> modifiedGroups) {
			updateItem(item);
		}

	}

	private class RosterItemCell
			extends AbstractCell<RosterItem> {

		@Override
		public void render(Context context, RosterItem value, SafeHtmlBuilder sb) {
			if (value != null) {
				try {
					sb.appendHtmlConstant("<table class='" + factory.theme().style().rosterItem() +
												  "'><tr><td rowspan='2' width='40px' style='padding:0px;font-size:0px;'>");
					Image avatar = factory.avatarFactory().getAvatarForJid(value.getJid());
					avatar.setSize("40px", "40px");
					sb.appendHtmlConstant(avatar.toString());
					sb.appendHtmlConstant(
							"</td><td colspan='2' class='" + factory.theme().style().rosterItemName() + "'>");
					sb.appendEscaped(value.getName());
					sb.appendHtmlConstant("</td></tr><tr><td width='16px'>");

					Presence p = PresenceModule.getPresenceStore(factory.sessionObject())
							.getBestPresence(value.getJid());
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
		public void onBrowserEvent(Context context, Element parent, RosterItem value, NativeEvent event,
								   ValueUpdater<RosterItem> valueUpdater) {
			RosterItemClickHandler handler = handlers.get(event.getType());
			Logger.getLogger("FlatRoster").warning("received event of type = " + event.getType());
			if (handler != null) {
				handler.itemClicked(value, event.getClientX(), event.getClientY());
				event.stopPropagation();
				event.preventDefault();
			} else {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
			}
		}

	}

}
