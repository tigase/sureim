/*
 * DiscoItem.java
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
package tigase.sure.web.site.client.disco;

import java.util.Collection;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;

/**
 *
 * @author andrzej
 */
public class DiscoItem {
        
        private final JID jid;
        private final String node;
        private final String name;

        private Collection<String> features;
        private Collection<Identity> identities;

        public DiscoItem(JID jid, String node, String name) {
                this.jid = jid;
                this.node = node;
                this.name = name;
                
        }

        public JID getJid() {
                return jid;
        }

        public String getName() {
                return name;
        }

        public String getNode() {
                return node;
        }

        public Collection<String> getFeatures() {
                return features;
        }
        
        public boolean hasFeature(String feature) {
                if (features == null) {
                        return false;
                }
                return features.contains(feature);
        }
        
        void setFeatures(Collection<String> features) {
                this.features = features;
        }

        public Collection<Identity> getIdentities() {
                return identities;
        }
        
        void setIdentities(Collection<Identity> identities) {
                this.identities = identities;
        }
        
        @Override
        public boolean equals(Object o) {
                if (o != null && o instanceof DiscoItem) {
                        return hashCode() == o.hashCode();
                }
                return false;
        }
        
        @Override
        public int hashCode() {
                if (getNode() == null) 
                        return getJid().hashCode();
                return getJid().hashCode() * getNode().hashCode();
        }
}
