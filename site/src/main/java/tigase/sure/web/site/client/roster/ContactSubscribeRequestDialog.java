/*
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
package tigase.sure.web.site.client.roster;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.chat.ChatViewImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class ContactSubscribeRequestDialog
		extends DialogBox {

	private final ClientFactory factory;

	public ContactSubscribeRequestDialog(ClientFactory factory_, final BareJID jid) {
		super(true);
		factory = factory_;

		setStyleName("dialogBox");
		setTitle(factory.i18n().subscriptionRequestContact());

		FlexTable table = new FlexTable();
		Label label = new Label(factory.i18n().subscriptionRequestContact());
		label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
		label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		table.setWidget(0, 0, label);

		label = new Label(factory.i18n().subscriptionRequestMessageContact() + " " + jid.toString());
		table.setWidget(1, 0, label);

		Button cancel = new Button(factory.baseI18n().cancel());
		cancel.setStyleName(factory.theme().style().button());
		cancel.addStyleName(factory.theme().style().left());
		table.setWidget(2, 0, cancel);
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					factory.jaxmpp()
							.getModulesManager()
							.getModule(PresenceModule.class)
							.unsubscribed(JID.jidInstance(jid));
					hide();
				} catch (XMLException ex) {
					Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				} catch (JaxmppException ex) {
					Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		Button ok = new Button(factory.baseI18n().accept());
		ok.setStyleName(factory.theme().style().button());
		ok.addStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().right());
		table.setWidget(2, 1, ok);
		ok.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					factory.jaxmpp()
							.getModulesManager()
							.getModule(PresenceModule.class)
							.subscribed(JID.jidInstance(jid));
					hide();
				} catch (XMLException ex) {
					Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				} catch (JaxmppException ex) {
					Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

		setWidget(table);
	}
}
