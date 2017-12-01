/*
 * MucRoomWidget.java
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
package tigase.sure.web.site.client.chat;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Occupant;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.sure.web.site.client.ClientFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class MucRoomWidget
		extends ResizeComposite {

	private static final OccupantsComparator occupantsComparator = new OccupantsComparator();
	private final ClientFactory factory;
	private final TextArea input;
	private final DockLayoutPanel layout;
	private final MucLogPanel log;
	private final ListDataProvider<Occupant> occupantsList = new ListDataProvider<Occupant>();
	private final CellList<Occupant> presences;
	private final Room room;

	public static boolean isVisible(Widget w) {
		if (w.isAttached() && w.isVisible()) {
			if (w.getParent() != null) {
				return isVisible(w.getParent());
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public MucRoomWidget(ClientFactory factory_, Room room_) {
		this.factory = factory_;

		layout = new DockLayoutPanel(Unit.EM);
		room = room_;

		presences = new CellList<Occupant>(new OccupantCell());
		layout.addEast(new ScrollPanel(presences), 16);
		occupantsList.addDataDisplay(presences);

		input = new TextArea();
		input.setWidth("99%");
		input.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					input.setFocus(false);
					try {
						String text = input.getText();
						room.sendMessage(text);
					} catch (Exception ex) {
						Logger.getLogger("MucRoomWidget").log(Level.WARNING, "sending message exception", ex);
					}
					input.setFocus(true);
					event.stopPropagation();
				}
			}
		});
		input.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					input.setFocus(false);
					try {
						// clearing input field after sending message
						input.setText(null);
					} catch (Exception ex) {
					}
					input.setFocus(true);
					event.stopPropagation();
				}
			}
		});
		layout.addSouth(input, 2.0);

		log = new MucLogPanel(factory, room);
		layout.add(log);

		initWidget(layout);

		refreshOccupantsList();
	}

	public Room getRoom() {
		return room;
	}

	@Override
	public String getTitle() {
		return room.getRoomJid().toString();
	}

	public boolean handleMessage(Message m) {
		log.appendMessage(m);

		return isVisible(this);
	}

	public void refreshOccupantsList() {
		Map<String, Occupant> occupants = room.getPresences();
		List<Occupant> sortedOccupants = new ArrayList<Occupant>(occupants.values());
		Collections.sort(sortedOccupants, occupantsComparator);

		occupantsList.getList().clear();
		occupantsList.getList().addAll(sortedOccupants);
	}

	private static class OccupantsComparator
			implements Comparator<Occupant> {

		public int compare(Occupant o1, Occupant o2) {
			if (o1 == null || o2 == null) {
				return -1;
			}
			try {
				return o1.getNickname().compareTo(o2.getNickname());
			} catch (XMLException ex) {
				return -1;
			}
		}
	}

	private class OccupantCell
			extends AbstractCell<Occupant> {

		@Override
		public void render(Cell.Context context, Occupant value, SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}
			try {
				sb.appendHtmlConstant("<table class=\"mucOccupants\">");
				sb.appendHtmlConstant("<tr><td>");

				Presence p = value.getPresence();
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

				sb.appendHtmlConstant("</td><td class=\"mucOccupants-");
				String role = null;
				Element x = p.getChildrenNS("x", "http://jabber.org/protocol/muc#user");
				if (x != null) {
					List<Element> items = x.getChildren("item");
					if (items != null && !items.isEmpty()) {
						role = items.get(0).getAttribute("role");
					}
				}
				if (role == null) {
					role = "visitor";
				} else {
					role = role.toLowerCase();
				}
				sb.appendHtmlConstant(role);
				sb.appendHtmlConstant("\">");
				sb.appendEscaped(value.getNickname());
				sb.appendHtmlConstant("</td></tr>");
				sb.appendHtmlConstant("</table>");
			} catch (XMLException ex) {
				Logger.getLogger(MucRoomWidget.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
