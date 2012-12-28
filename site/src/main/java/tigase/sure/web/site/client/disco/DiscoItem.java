/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.disco;

import java.util.Collection;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.Identity;

/**
 *
 * @author andrzej
 */
class DiscoItem {
        
        private final JID jid;
        private final String node;
        private final String name;

        private Collection<String> features;
        private Collection<Identity> identities;

        DiscoItem(JID jid, String node, String name) {
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
