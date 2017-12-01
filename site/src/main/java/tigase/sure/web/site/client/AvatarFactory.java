/*
 * AvatarFactory.java
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
package tigase.sure.web.site.client;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.Image;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCard;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule.VCardAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AbstractAvatarFactory;
import tigase.sure.web.base.client.AvatarChangedEvent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class AvatarFactory
		extends AbstractAvatarFactory {

	private static final Logger log = Logger.getLogger("AvatarFactory");
	private final ClientFactory factory;
	//        private final MainCss css;
	private final Storage storage;

	public AvatarFactory(ClientFactory factory) {
		super(factory);
		this.factory = factory;
//                this.css = factory.getMainCss();
		this.storage = Storage.getLocalStorageIfSupported();
	}

	@Override
	public Image getAvatarForJid(final BareJID jid) {
		Image img = null;
		try {
			Presence p = PresenceModule.getPresenceStore(factory.sessionObject()).getBestPresence(jid);
			if (p != null) {
				Element x = p.getChildrenNS("x", "vcard-temp:x:update");
				if (x != null) {
					List<Element> photos = x.getChildren("photo");
					if (photos != null) {
						final String hash = photos.get(0).getValue();
						img = getAvatarForHash(hash);
						if (img == null) {
							factory.jaxmpp()
									.getModulesManager()
									.getModule(VCardModule.class)
									.retrieveVCard(JID.jidInstance(jid), new VCardAsyncCallback() {

										@Override
										public void onError(Stanza responseStanza, ErrorCondition error)
												throws JaxmppException {
										}

										@Override
										public void onTimeout() throws JaxmppException {
										}

										@Override
										protected void onVCardReceived(VCard vcard) throws XMLException {
											if (vcard.getPhotoVal() != null) {
												String avatar = "data:" + vcard.getPhotoType() + ";base64," +
														vcard.getPhotoVal();
												setAvatarForHash(hash, avatar);
											}
											factory.eventBus().fireEvent(new AvatarChangedEvent(JID.jidInstance(jid)));
										}
									});
						}
					}
				}
			}

			if (img == null) {
				img = getImageFromStore(jid);
			}
		} catch (Exception ex) {
			log.log(Level.WARNING, "exception processing presence for avatar hash", ex);
		}

		if (img == null) {
			img = new Image(factory.theme().socialPerson());
		}

		//img.setStyleName(css.avatarImageClass());

		return img;
	}

	@Override
	public Image getAvatarForHash(String hash) {
		String imgData = storage.getItem("avatar:hash:" + hash);
		if (imgData == null) {
			return null;
		}

		return new Image(imgData);
	}

	@Override
	public void setAvatarForHash(String hash, String data) {
		storage.setItem("avatar:hash:" + hash, data);
	}

	@Override
	public void setAvatarForJid(BareJID jid, String data) {
		storage.setItem("avatar:jid:" + jid.toString(), data);
	}

	public void requestAvatar(final JID jid) {
		try {
			factory.jaxmpp()
					.getModulesManager()
					.getModule(VCardModule.class)
					.retrieveVCard(jid, new VCardAsyncCallback() {

						@Override
						public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
							throw new UnsupportedOperationException("Not supported yet.");
						}

						@Override
						public void onTimeout() throws JaxmppException {
							throw new UnsupportedOperationException("Not supported yet.");
						}

						@Override
						protected void onVCardReceived(VCard vcard) throws XMLException {
							if (vcard.getPhotoVal() != null) {
								String avatar = "data:" + vcard.getPhotoType() + ";base64," + vcard.getPhotoVal();
								setAvatarForJid(jid.getBareJid(), avatar);
							}
							factory.eventBus().fireEvent(new AvatarChangedEvent(jid));
						}

					});
		} catch (Exception ex) {
			Logger.getLogger("AvatarFactory").log(Level.WARNING, "exception requesting vCard avatar", ex);
		}
	}

	private Image getImageFromStore(BareJID jid) {
		String imgData = storage.getItem("avatar:jid:" + jid.toString());
		if (imgData == null) {
			return null;
		}

		return new Image(imgData);
	}
}
