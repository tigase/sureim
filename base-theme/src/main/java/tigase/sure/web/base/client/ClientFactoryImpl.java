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

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 * @author andrzej
 */
public abstract class ClientFactoryImpl
		implements ClientFactory {

	private final BaseI18n baseI18n = GWT.create(BaseI18n.class);
	private final EventBus eventBus = GWT.create(SimpleEventBus.class);
	private final Jaxmpp jaxmpp = new Jaxmpp();
	private final Theme theme = GWT.create(Theme.class);

	public ClientFactoryImpl() {
		theme().verticalTabPanelStyles().ensureInjected();
	}

	@Override
	public EventBus eventBus() {
		return eventBus;
	}

	@Override
	public Theme theme() {
		return theme;
	}

	@Override
	public Jaxmpp jaxmpp() {
		return jaxmpp;
	}

	@Override
	public SessionObject sessionObject() {
		return jaxmpp.getSessionObject();
	}

	@Override
	public BaseI18n baseI18n() {
		return baseI18n;
	}
}
