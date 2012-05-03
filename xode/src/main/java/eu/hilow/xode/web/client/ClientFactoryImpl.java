/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Cookies;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import eu.hilow.gwt.base.client.AbstractAvatarFactory;
import eu.hilow.gwt.base.client.AppView;
import eu.hilow.gwt.base.client.Theme;
import eu.hilow.gwt.base.client.auth.AuthEvent;
import eu.hilow.gwt.base.client.auth.AbstractAuthView;
import eu.hilow.jaxmpp.ext.client.xmpp.modules.archive.MessageArchivingModule;
import eu.hilow.xode.web.client.archive.ArchiveView;
import eu.hilow.xode.web.client.archive.ArchiveViewImpl;
import eu.hilow.xode.web.client.auth.AuthView;
import eu.hilow.xode.web.client.chat.ChatView;
import eu.hilow.xode.web.client.chat.ChatViewImpl;
import eu.hilow.xode.web.client.events.ServerFeaturesChangedEvent;
import eu.hilow.xode.web.client.settings.SettingsView;
import eu.hilow.xode.web.client.settings.SettingsViewImpl;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule.ResourceBindEvent;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.DiscoInfoAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.Identity;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class ClientFactoryImpl extends eu.hilow.gwt.base.client.ClientFactoryImpl implements ClientFactory {

        private static final Logger log = Logger.getLogger("ClientFactoryImpl");
        private final ActionBarFactory actionBarFactory;
        private final ArchiveView archiveView;
        private final AbstractAvatarFactory avatarFactory;
        private final AuthView authView;
        private final ChatView chatView;
        private final SettingsView settingsView;
        private final I18n i18n = GWT.create(I18n.class);
        private final PlaceController placeController;
        private final Listener<ResourceBindEvent> jaxmppBindListener;

        public ClientFactoryImpl() {
                super();
                placeController = new PlaceController(eventBus());
                avatarFactory = new AvatarFactory(this);
                actionBarFactory = new ActionBarFactory(this);
                
                authView = new AuthView(this);
                chatView = new ChatViewImpl(this);
                archiveView = new ArchiveViewImpl(this);                
                settingsView = new SettingsViewImpl(this);
                
                jaxmpp().getModulesManager().register(new MessageArchivingModule(jaxmpp().getSessionObject(), jaxmpp().getWriter()));
                
                jaxmppBindListener = new Listener<ResourceBinderModule.ResourceBindEvent>() {

                        public void handleEvent(ResourceBindEvent be) throws JaxmppException {
                                try {  
                                        jaxmpp().getModulesManager().getModule(DiscoInfoModule.class).getInfo(
                                                JID.jidInstance(be.getJid().getDomain()), new DiscoInfoAsyncCallback(null) {

                                                @Override
                                                protected void onInfoReceived(String node, Collection<Identity> identities, Collection<String> features) throws XMLException {
                                                        eventBus().fireEvent(new ServerFeaturesChangedEvent(identities, features));
                                                }

                                                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                                        throw new UnsupportedOperationException("Not supported yet.");
                                                }

                                                public void onTimeout() throws JaxmppException {
                                                        throw new UnsupportedOperationException("Not supported yet.");
                                                }
                                                        
                                                });
                                        
//                                        if (be.getError() != null) {
//                                                Cookies.setCookie("username", 
//                                                        jaxmpp().getProperties().getUserProperty(SessionObject.USER_BARE_JID).toString(),
//                                                        new Date(new Date().getTime() + 24*60*60*1000*7));
//                                                Cookies.setCookie("password", 
//                                                        jaxmpp().getProperties().getUserProperty(SessionObject.PASSWORD).toString(),
//                                                        new Date(new Date().getTime() + 24*60*60*1000*7));
//                                        }
                                        eventBus().fireEvent(new AuthEvent(be.getJid()));
                                } catch (Exception ex) {
                                        log.log(Level.WARNING, "exception firing auth event", ex);
                                }
                        }
                };

                jaxmpp().addListener(ResourceBinderModule.ResourceBindError, jaxmppBindListener);
                jaxmpp().addListener(ResourceBinderModule.ResourceBindSuccess, jaxmppBindListener);
        }

        @Override
        public ActionBarFactory actionBarFactory() {
                return actionBarFactory;
        }
        
        @Override
        public ArchiveView archiveView() {
                return archiveView;
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

        public I18n i18n() {
                return i18n;
        }
        
        @Override
        public PlaceController placeController() {
                return placeController;
        }
        
        @Override
        public SettingsView settingsView() {
                return settingsView;
        }
        
}
