/*
 * AuthRequestEvent.java
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
package tigase.sure.web.base.client.auth;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.JID;

/**
 * @author andrzej
 */
public class AuthRequestEvent
		extends Event<AuthRequestHandler> {

	public static final Type<AuthRequestHandler> TYPE = new Type<AuthRequestHandler>();

	private final JID jid;
	private final String password;
	private final String url;

	public AuthRequestEvent(JID j, String p) {
		this.jid = j;
		this.password = p;
		this.url = null;
	}

	public AuthRequestEvent(JID j, String p, String url) {
		this.jid = j;
		this.password = p;
		this.url = url;
	}

	@Override
	public Type<AuthRequestHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AuthRequestHandler handler) {
		handler.authenticate(jid, password, url);
	}

}
