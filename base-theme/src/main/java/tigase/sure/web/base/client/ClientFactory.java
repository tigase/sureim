/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.web.bindery.event.shared.EventBus;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 *
 * @author andrzej
 */
public interface ClientFactory {

        EventBus eventBus();
        
        Theme theme();
        
        Jaxmpp jaxmpp();
		
		SessionObject sessionObject();
        
        AbstractAvatarFactory avatarFactory();

        BaseI18n baseI18n();
}
