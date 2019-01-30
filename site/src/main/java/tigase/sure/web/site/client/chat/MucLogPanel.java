/**
 * Sure.IM site - bootstrap configuration for all Tigase projects
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
package tigase.sure.web.site.client.chat;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.utils.delay.XmppDelay;
import tigase.sure.web.site.client.ClientFactory;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class MucLogPanel
		extends Composite {

	private final ClientFactory factory;
	private final String jid;
	private final String name;
	private final Room room;
	private final ScrollPanel scroll;
	private HTMLPanel log;
	private DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss");

	public MucLogPanel(ClientFactory factory, Room room) {
		this.name = room.getRoomJid().toString();
		this.jid = room.getRoomJid().toString();
		this.room = room;
		this.factory = factory;
		log = new HTMLPanel("<div id=\"Chat-" + jid + "\"></div>");
		scroll = new ScrollPanel(log);
		initWidget(scroll);
	}

	public void appendMessage(Message msg) {
		try {
			String body = msg.getBody();

			if (msg == null || body == null) {
				return;
			}

			SafeHtmlBuilder sb = new SafeHtmlBuilder();

			String nick = msg.getFrom().getResource();
			boolean mine = room.getNickname().equals(nick);
			boolean mark = body.contains(room.getNickname());

			XmppDelay delay = XmppDelay.extract(msg);
			Date time = delay != null ? delay.getStamp() : null;//new Date();
			if (time == null) {
				time = new Date();
			}
			Logger.getLogger(MucLogPanel.class.getName())
					.log(Level.FINE,
						 "time: " + time + ", nick: " + nick + ", body: " + body + ", mark: " + mark + ", mine: " + mine

					);
			sb.appendHtmlConstant("<div class=\"mucEntry\">");
			sb.appendHtmlConstant("<div class=\"mucEntryNick muc" + (mine ? "Mine" : "His") + "EntryNick\">[");
			sb.appendEscaped(timeFormat.format(time));
			sb.appendEscaped("] <");
			sb.appendEscaped((nick != null ? nick : " *** "));
			sb.appendEscaped("> ");
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("<div class=\"mucEntry" + (mark ? "Mark" : "") + "Text\">");
			sb.appendEscaped(body);
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("</div>");

			HTML html = new HTML(sb.toSafeHtml());
			html.setWordWrap(true);
			log.add(html, "Chat-" + jid);

			scroll.scrollToBottom();
		} catch (XMLException ex) {
			Logger.getLogger("LogPanel").log(Level.WARNING, "Exception processing message", ex);
		}
	}

	public void activated() {
		scroll.scrollToBottom();
	}

}
