/*
 * AuthFailureEvent.java
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

import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule.SaslError;

/**
 * @author andrzej
 */
public class AuthFailureEvent
		extends AuthEvent {

	private final String msg;
	private final SaslError saslError;

	public AuthFailureEvent(String msg) {
		super(null);
		this.msg = msg;
		this.saslError = null;
	}

	public AuthFailureEvent(SaslError error) {
		super(null);
		this.msg = null;
		this.saslError = error;
	}

	public SaslError getSaslError() {
		return saslError;
	}

	public String getMessage() {
		return msg;
	}
}
