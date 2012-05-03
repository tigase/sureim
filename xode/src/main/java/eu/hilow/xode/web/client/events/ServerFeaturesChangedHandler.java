/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.events;

import java.util.Collection;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.Identity;

/**
 *
 * @author andrzej
 */
public interface ServerFeaturesChangedHandler {
        
        void serverFeaturesChanged(Collection<Identity> identities, Collection<String> features);
        
}
