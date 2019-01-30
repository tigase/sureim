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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import tigase.jaxmpp.core.client.xmpp.modules.chat.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import tigase.sure.web.site.client.ClientFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class ChatWidget
		extends ResizeComposite {

	private final Chat chat;
	private final ClientFactory factory;
	private final TextArea input;
	private final ChatLogPanel log;

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

	public ChatWidget(ClientFactory factory_, Chat chat_) {
		super();

		this.factory = factory_;
		this.chat = chat_;

		DockLayoutPanel layout = new DockLayoutPanel(Unit.EM);

		input = new TextArea();
		input.setWidth("99%");
		input.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					input.setFocus(false);
					try {
						String text = input.getText();
						Message msg = Message.create();
						msg.setType(StanzaType.chat);
						msg.setTo(chat.getJid());
						msg.setThread(chat.getThreadId());
						msg.setBody(text);

						// input is cleared in keyuphandler
//                                                input.setText(null);
						handleMessage(msg);
						factory.jaxmpp().send(msg);
					} catch (Exception ex) {
						Logger.getLogger("Chat").log(Level.WARNING, "sending message exception", ex);
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

		log = new ChatLogPanel(factory, chat.getJid().toString(), getTitle());
		layout.add(log);

		initWidget(layout);
	}

	public Chat getChat() {
		return chat;
	}

	@Override
	public String getTitle() {
		RosterItem ri = RosterModule.getRosterStore(chat.getSessionObject()).get(chat.getJid().getBareJid());
		return ri != null ? ri.getName() : chat.getJid().toString();
	}

	public boolean handleMessage(Message message) {
		log.appendMessage(message);
		return isVisible(this);
	}
}
