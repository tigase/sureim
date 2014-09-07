/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.events;

import java.util.Collection;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;

/**
 *
 * @author andrzej
 */
public interface ServerFeaturesChangedHandler {
        
        void serverFeaturesChanged(Collection<Identity> identities, Collection<String> features);
        
}
