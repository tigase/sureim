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
package tigase.sure.web.site.client.events;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;

import java.util.Collection;

/**
 * @author andrzej
 */
public class ServerFeaturesChangedEvent
		extends Event<ServerFeaturesChangedHandler> {

	public static final Event.Type<ServerFeaturesChangedHandler> TYPE = new Event.Type<ServerFeaturesChangedHandler>();
	private final Collection<String> features;
	private final Collection<Identity> identities;

	public ServerFeaturesChangedEvent(Collection<Identity> identities, Collection<String> features) {
		this.identities = identities;
		this.features = features;
	}

	@Override
	public Event.Type<ServerFeaturesChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ServerFeaturesChangedHandler handler) {
		handler.serverFeaturesChanged(identities, features);
	}
}
