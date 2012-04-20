/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

import com.google.web.bindery.event.shared.EventBus;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 *
 * @author andrzej
 */
public interface ClientFactory {

        EventBus eventBus();
        
        Theme theme();
        
        Jaxmpp jaxmpp();
        
        AbstractAvatarFactory avatarFactory();

        BaseI18n baseI18n();
}
