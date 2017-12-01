/*
 * ServerFeaturesChangedEvent.java
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
package tigase.sure.web.site.client.events;

import com.google.web.bindery.event.shared.Event;
import tigase.sure.web.base.client.auth.AuthHandler;
import java.util.Collection;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;

/**
 *
 * @author andrzej
 */
public class ServerFeaturesChangedEvent extends Event<ServerFeaturesChangedHandler> {

        public static final Event.Type<ServerFeaturesChangedHandler> TYPE = new Event.Type<ServerFeaturesChangedHandler>();
        
        private final Collection<Identity> identities;
        private final Collection<String> features;
        
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
