/*
 * PersonalInformationView.java
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
package tigase.sure.web.site.client.settings;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCard;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule.VCardAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import tigase.sure.web.base.client.widgets.FileInput;
import tigase.sure.web.base.client.widgets.View;
import tigase.sure.web.base.client.widgets.file.File;
import tigase.sure.web.base.client.widgets.file.FileReader;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.MessageDialog;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class PersonalInformationView
		extends Composite
		implements View {

	private static final Logger log = Logger.getLogger("PersonalInformationView");
	private final Image avatar;
	private final TextBox birthday;
	private final Button cancel;
	private final TextBox email;
	private final ClientFactory factory;
	private final TextBox fullname;
	private final FlexTable layout;
	private final TextBox nick;
	private final Button ok;

	private VCard vcard = null;

	public PersonalInformationView(ClientFactory factory) {
		this.factory = factory;

		layout = new FlexTable();
		layout.addStyleName("settingsView");

		Label label = new Label(factory.i18n().avatar());
		layout.setWidget(0, 0, label);

		avatar = new Image();
		avatar.setHeight("160px");
		avatar.setUrl(factory.theme().socialPerson().getSafeUri());
		layout.setWidget(0, 1, avatar);

		final FileInput avatarFile = new FileInput();
		final Element avatarEl = avatarFile.getElement();
		avatarEl.getStyle().setWidth(100, Style.Unit.PCT);
		layout.getCellFormatter().getElement(0, 1).appendChild(avatarEl);
		//layout.setWidget(0, 0, avatarFile);
		avatarFile.setChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				File file = avatarFile.getFiles().getItem(0);
				FileReader reader = FileReader.newInstance();
				reader.addLoadEndHandler(new com.google.gwt.user.client.rpc.AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						throw new UnsupportedOperationException("Not supported yet.");
					}

					public void onSuccess(String result) {
						avatar.setUrl(result);
					}

				});
				reader.readAsDataURL(file);
			}

		});

		label = new Label(factory.i18n().fullName());
		layout.setWidget(1, 0, label);
		fullname = new TextBox();
		layout.setWidget(1, 1, fullname);

		label = new Label(factory.i18n().nick());
		layout.setWidget(2, 0, label);
		nick = new TextBox();
		layout.setWidget(2, 1, nick);

		label = new Label(factory.i18n().email());
		layout.setWidget(3, 0, label);
		email = new TextBox();
		layout.setWidget(3, 1, email);

		label = new Label(factory.i18n().birthday());
		layout.setWidget(4, 0, label);
		birthday = new TextBox();
		layout.setWidget(4, 1, birthday);

		cancel = new Button(factory.baseI18n().cancel());
		cancel.setStyleName(factory.theme().style().button());
		cancel.addStyleName(factory.theme().style().left());
		layout.setWidget(5, 0, cancel);
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				update();
			}
		});

		ok = new Button(factory.baseI18n().confirm());
		ok.setStyleName(factory.theme().style().button());
		ok.addStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().right());
		layout.setWidget(5, 1, ok);
		ok.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				try {
					publish();
				} catch (Exception ex) {
				}
			}
		});

		initWidget(layout);
	}

	public void update() {
		try {
			if (!factory.jaxmpp().isConnected()) {
				return;
			}
			disableButtons();

			BareJID userJid = factory.jaxmpp().getSessionObject().getUserBareJid();
			VCardModule vcardModule = factory.jaxmpp().getModulesManager().getModule(VCardModule.class);
			vcardModule.retrieveVCard(JID.jidInstance(userJid), new VCardAsyncCallback() {

				public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
					enableButtons();
				}

				public void onTimeout() throws JaxmppException {
					enableButtons();
				}

				@Override
				protected void onVCardReceived(VCard vcard_) throws XMLException {
					vcard = vcard_;
					if (vcard.getPhotoType() != null && vcard.getPhotoVal() != null) {
						avatar.setUrl("data:" + vcard.getPhotoType() + ";base64," + vcard.getPhotoVal());
					} else {
						avatar.setUrl(factory.theme().socialPerson().getSafeUri());
					}

					fullname.setText(vcard.getFullName());
					nick.setText(vcard.getNickName());
					email.setText(vcard.getHomeEmail());
					birthday.setText(vcard.getBday());
					enableButtons();
				}

			});
		} catch (JaxmppException ex) {
			log.log(Level.SEVERE, null, ex);
			enableButtons();
		}
	}

	public void publish() throws XMLException, JaxmppException {
		disableButtons();

		if (vcard == null) {
			vcard = new VCard();
		}

		vcard.setFullName(fullname.getText());
		vcard.setNickName(nick.getText());
		vcard.setHomeEmail(email.getText());
		vcard.setBday(birthday.getText());

		if (!factory.theme().socialPerson().getSafeUri().asString().equals(avatar.getUrl())) {
			String url = avatar.getUrl();
			int idx = url.indexOf(":") + 1;
			if (idx < 1) {
				return;
			}
			int idx2 = url.indexOf(",");
			if (idx2 < 1) {
				return;
			}
			String[] params = url.substring(idx, idx2).split(";");
			if (log.isLoggable(Level.FINEST)) {
				log.finest("type = " + params[0] + ", encoding = " + params[1]);
				log.finest("data = " + url.substring(idx2 + 1));
			}
			vcard.setPhotoType(params[0]);
			vcard.setPhotoVal(url.substring(idx2 + 1));
		}

		if (!factory.jaxmpp().isConnected()) {
			enableButtons();
			return;
		}

		IQ iq = IQ.create();
		iq.setType(StanzaType.set);
		iq.addChild(vcard.makeElement());
		factory.jaxmpp().getWriter().write(iq, new AsyncCallback() {

			public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
				MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), error.getElementName());
				dlg.show();
				dlg.center();
				enableButtons();
			}

			public void onSuccess(Stanza responseStanza) throws JaxmppException {
				update();
			}

			public void onTimeout() throws JaxmppException {
				MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(),
													  factory.i18n().requestTimedOut());
				dlg.show();
				dlg.center();
				enableButtons();
			}

		});
	}

	private void disableButtons() {
		ok.removeStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().buttonDisabled());
		ok.setEnabled(false);
		cancel.addStyleName(factory.theme().style().buttonDisabled());
		cancel.setEnabled(false);
	}

	private void enableButtons() {
		ok.removeStyleName(factory.theme().style().buttonDisabled());
		ok.addStyleName(factory.theme().style().buttonDefault());
		ok.setEnabled(true);
		cancel.removeStyleName(factory.theme().style().buttonDisabled());
		cancel.setEnabled(true);
	}

}
