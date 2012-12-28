/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.events;

import com.google.web.bindery.event.shared.Event;
import tigase.sure.web.base.client.auth.AuthHandler;
import java.util.Collection;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.Identity;

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
