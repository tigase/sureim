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
package tigase.sure.web.base.client;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.JID;

/**
 * @author andrzej
 */
public class AvatarChangedEvent
		extends Event<AvatarChangedHandler> {

	public static final Type<AvatarChangedHandler> TYPE = new Type<AvatarChangedHandler>();

	private final JID jid;

	public AvatarChangedEvent(JID jid) {
		this.jid = jid;
	}

	@Override
	public Type<AvatarChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AvatarChangedHandler handler) {
		handler.avatarChanged(jid);
	}

}
