package eu.hilow.xode.web.client;

import eu.hilow.xode.web.client.auth.AuthPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import eu.hilow.gwt.base.client.ActionBar;
import eu.hilow.gwt.base.client.AppView;
import eu.hilow.gwt.base.client.RootView;
import eu.hilow.gwt.base.client.auth.AuthEvent;
import eu.hilow.gwt.base.client.auth.AuthHandler;
import eu.hilow.gwt.base.client.auth.AuthRequestEvent;
import eu.hilow.gwt.base.client.auth.AuthRequestHandler;
import eu.hilow.gwt.base.client.roster.FlatRoster;
import eu.hilow.xode.web.client.chat.ChatPlace;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Xode implements EntryPoint {


        /**
         * This is the entry point method.
         */
        public void onModuleLoad() {
                
                final ClientFactory factory = GWT.create(ClientFactory.class);
                factory.theme().style().ensureInjected();
                
                RootView view = new RootView(factory);
                
//                AbsolutePanel center = new AbsolutePanel();
//                center.add(new Label("Center panel"));
                
//                AppView appView = new AppView(center, factory);
                ResizeLayoutPanel appView = new ResizeLayoutPanel();                
                view.setCenter(appView);
                                
                EventBus eventBus = factory.eventBus();
                final PlaceController placeController = factory.placeController();
                
                // Start ActivityManager for the main widget with our ActivityMapper
                ActivityMapper activityMapper = new AppActivityMapper(factory);
                ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
                activityManager.setDisplay(appView);

                Place defaultPlace = new AuthPlace();//new ChatPlace();
                
                // Start PlaceHistoryHandler with our PlaceHistoryMapper
                AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
                PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
                historyHandler.register(placeController, eventBus, defaultPlace);                
                
                //appView.getActionBar().setSearchBox(new TextBox());
                
                RootLayoutPanel.get().add(view);
                
                historyHandler.handleCurrentHistory();
                
                
                eventBus.addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {
                                placeController.goTo(new ChatPlace());
                        }

                        public void deauthenticated() {
                                placeController.goTo(new AuthPlace());
                        }
                        
                });
                
                eventBus.addHandler(AuthRequestEvent.TYPE, new AuthRequestHandler() {

                        public void authenticate(JID jid, String password) {
                                Jaxmpp jaxmpp = factory.jaxmpp();
                                jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, "http://" + jid.getDomain() + ":5280/bosh");
                        
                                jaxmpp.getProperties().setUserProperty(SessionObject.RESOURCE, "jaxmpp");
                                jaxmpp.getProperties().setUserProperty(SessionObject.USER_BARE_JID, jid.getBareJid());
                                jaxmpp.getProperties().setUserProperty(SessionObject.PASSWORD, password);
                                try {
                                        jaxmpp.login();
                                }
                                catch (JaxmppException ex) {
                                        //log.log(Level.WARNING, "login exception", ex);
                                }
                        }
                        
                });
                
                //authenticateTest(factory);
        }
        
        private void authenticateTest(ClientFactory factory) {
                        Jaxmpp jaxmpp = factory.jaxmpp();
                        jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, "http://hi-low.eu:5280/bosh");
                        
                        jaxmpp.getProperties().setUserProperty(SessionObject.RESOURCE, "jaxmpp");
                        jaxmpp.getProperties().setUserProperty(SessionObject.USER_BARE_JID, BareJID.bareJIDInstance("andrzej@hi-low.eu"));
                        jaxmpp.getProperties().setUserProperty(SessionObject.PASSWORD, "W$vve!@2gw");
                        try {
                                jaxmpp.login();
                        }
                        catch (JaxmppException ex) {
                                //log.log(Level.WARNING, "login exception", ex);
                        }
       }
        
        
}
