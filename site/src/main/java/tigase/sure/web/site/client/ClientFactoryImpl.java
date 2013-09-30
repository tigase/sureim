/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Cookies;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import tigase.sure.web.base.client.AbstractAvatarFactory;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.Theme;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AbstractAuthView;
import tigase.jaxmpp.ext.client.xmpp.modules.archive.MessageArchivingModule;
import tigase.sure.web.site.client.archive.ArchiveView;
import tigase.sure.web.site.client.archive.ArchiveViewImpl;
import tigase.sure.web.site.client.auth.AuthView;
import tigase.sure.web.site.client.bookmarks.BookmarksManager;
import tigase.sure.web.site.client.chat.ChatView;
import tigase.sure.web.site.client.chat.ChatViewImpl;
import tigase.sure.web.site.client.disco.DiscoView;
import tigase.sure.web.site.client.disco.DiscoViewImpl;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.pubsub.PubSubPublishView;
import tigase.sure.web.site.client.pubsub.PubSubPublishViewImpl;
import tigase.sure.web.site.client.settings.SettingsView;
import tigase.sure.web.site.client.settings.SettingsViewImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.JaxmppCore;
import tigase.jaxmpp.core.client.JaxmppCore.JaxmppEvent;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.BookmarksModule;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule.ResourceBindEvent;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.DiscoInfoAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.Identity;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 *
 * @author andrzej
 */
public class ClientFactoryImpl extends tigase.sure.web.base.client.ClientFactoryImpl implements ClientFactory {

        private static final Logger log = Logger.getLogger("ClientFactoryImpl");
        private final ActionBarFactory actionBarFactory;
        private final ArchiveView archiveView;
        private final AbstractAvatarFactory avatarFactory;
        private final AuthView authView;
        private final BookmarksManager bookmarksManager;
        private final ChatView chatView;
        private final DiscoView discoView;
        private final PubSubPublishView pubSubPublishView;
        private final SettingsView settingsView;
        private final I18n i18n = GWT.create(I18n.class);
        private final PlaceController placeController;
        private final Listener<ResourceBindEvent> jaxmppBindListener;
        
        public ClientFactoryImpl() {
                super();
                placeController = new PlaceController(eventBus());
                avatarFactory = new AvatarFactory(this);
                actionBarFactory = new ActionBarFactory(this);
                
                bookmarksManager = new BookmarksManager(this);
                
                authView = new AuthView(this);
                chatView = new ChatViewImpl(this);
                archiveView = new ArchiveViewImpl(this);                
                discoView = new DiscoViewImpl(this);
                pubSubPublishView = new PubSubPublishViewImpl(this);
                settingsView = new SettingsViewImpl(this);
                
                jaxmpp().getModulesManager().register(new MessageArchivingModule());
                jaxmpp().getModulesManager().register(new BookmarksModule(jaxmpp().getSessionObject(), jaxmpp().getWriter()));
                
                jaxmppBindListener = new Listener<ResourceBinderModule.ResourceBindEvent>() {

                        public void handleEvent(ResourceBindEvent be) throws JaxmppException {
                                try {  
                                        eventBus().fireEvent(new ServerFeaturesChangedEvent(new ArrayList<Identity>(), new ArrayList<String>()));
                                        bookmarksManager().retrieve();
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
                
                jaxmpp().addListener(Jaxmpp.Disconnected, new Listener<Jaxmpp.JaxmppEvent>() {

                        public void handleEvent(JaxmppEvent be) throws JaxmppException {
                                if (be.getCaught() != null) {
                                        log.log(Level.WARNING, "Disconnected = " + be.getCaught().getMessage(), be.getCaught());
                                        MessageDialog dlg = new MessageDialog(ClientFactoryImpl.this, baseI18n().error(), be.getCaught().getMessage());
                                        dlg.show();
                                        dlg.center();
                                }
                                eventBus().fireEvent(new AuthEvent(null));
                        }
                
                });
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
        public BookmarksManager bookmarksManager() {
                return bookmarksManager;
        }
        
        @Override
        public ChatView chatView() {
                return chatView;
        }

        @Override
        public DiscoView discoView() {
                return discoView;
        }
        
        public I18n i18n() {
                return i18n;
        }
        
        @Override
        public PlaceController placeController() {
                return placeController;
        }
        
        @Override
        public PubSubPublishView pubSubPublishView() {
                return pubSubPublishView;
        }
        
        @Override
        public SettingsView settingsView() {
                return settingsView;
        }
        
}
