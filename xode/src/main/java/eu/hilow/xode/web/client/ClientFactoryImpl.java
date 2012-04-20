/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import eu.hilow.gwt.base.client.AbstractAvatarFactory;
import eu.hilow.gwt.base.client.AppView;
import eu.hilow.gwt.base.client.Theme;
import eu.hilow.gwt.base.client.auth.AuthEvent;
import eu.hilow.gwt.base.client.auth.AuthView;
import eu.hilow.xode.web.client.chat.ChatView;
import eu.hilow.xode.web.client.chat.ChatViewImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule.ResourceBindEvent;

/**
 *
 * @author andrzej
 */
public class ClientFactoryImpl extends eu.hilow.gwt.base.client.ClientFactoryImpl implements ClientFactory {

        private static final Logger log = Logger.getLogger("ClientFactoryImpl");
        
        private final AbstractAvatarFactory avatarFactory;
        private final AuthView authView;
        private final ChatView chatView;
        private final PlaceController placeController;
        
        private final Listener<ResourceBindEvent> jaxmppBindListener;
        
        public ClientFactoryImpl() {
                super();
                placeController = new PlaceController(eventBus());
                avatarFactory = new AvatarFactory(this);
                authView = new AuthView(this);
                chatView = new ChatViewImpl(this);

                jaxmppBindListener = new Listener<ResourceBinderModule.ResourceBindEvent>() {

                        public void handleEvent(ResourceBindEvent be) throws JaxmppException {
                                try {
                                        eventBus().fireEvent(new AuthEvent(be.getJid()));
                                }
                                catch (Exception ex) {
                                        log.log(Level.WARNING, "exception firing auth event", ex);
                                }
                        }
                        
                };
                
                jaxmpp().addListener(ResourceBinderModule.ResourceBindError, jaxmppBindListener);
                jaxmpp().addListener(ResourceBinderModule.ResourceBindSuccess, jaxmppBindListener);
        }        
        
        @Override
        public AbstractAvatarFactory avatarFactory() {
                return avatarFactory;
        }
        
        @Override
        public AuthView authView() {
                return authView;
        }
        
        @Override
        public ChatView chatView() {
                return chatView;
        }
        
        @Override
        public PlaceController placeController() {
                return placeController;
        }
        
}
