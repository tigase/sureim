/*
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
package tigase.sure.web.base.client;

import com.google.gwt.user.client.ui.Image;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;

/**
 * @author andrzej
 */
public abstract class AbstractAvatarFactory {

	private final ClientFactory factory;

	public AbstractAvatarFactory(ClientFactory factory) {
		this.factory = factory;
	}

	public abstract Image getAvatarForJid(BareJID jid);

	public abstract Image getAvatarForHash(String hash);

	public abstract void setAvatarForJid(BareJID jid, String data);

	public abstract void setAvatarForHash(String hash, String data);

	protected void notifyAvatarChange(BareJID jid) {
		factory.eventBus().fireEvent(new AvatarChangedEvent(JID.jidInstance(jid)));
	}
}
