package tigase.sure.web.site.client;

import tigase.sure.web.site.client.auth.AuthPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import tigase.sure.web.base.client.ActionBar;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.base.client.RootView;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AuthHandler;
import tigase.sure.web.base.client.auth.AuthRequestEvent;
import tigase.sure.web.base.client.auth.AuthRequestHandler;
import tigase.sure.web.base.client.roster.FlatRoster;
import tigase.sure.web.site.client.chat.ChatPlace;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.Connector;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.connector.AbstractBoshConnector;
import tigase.jaxmpp.core.client.connector.AbstractBoshConnector.BoshConnectorEvent;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.ErrorElement;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Xode implements EntryPoint {

        private static final Logger log = Logger.getLogger("Xode");

        // need this for reconnect during reaction on see-other-host
        private JID jid = null;
        private String password = null;
        
        private ClientFactory factory;
        /**
         * This is the entry point method.
         */
        public void onModuleLoad() {

                factory = GWT.create(ClientFactory.class);
                factory.theme().style().ensureInjected();

                RootView view = new RootView(factory);

                factory.jaxmpp().addListener(Connector.Error, new Listener<BoshConnectorEvent>() {

                        public void handleEvent(BoshConnectorEvent be) throws JaxmppException {
                                // needed to handle see-other-host
                                Element error = be.getStreamErrorElement();
                                if (error != null) {
                                        Element seeOtherHost = error.getChildrenNS("see-other-host", "urn:ietf:params:xml:ns:xmpp-streams");
                                        if (seeOtherHost != null) {
                                                authenticateInt2(jid, password, seeOtherHost.getValue());
                                        }
                                }
                        }
                        
                });
                
//                AbsolutePanel center = new AbsolutePanel();
//                center.add(new Label("Center panel"));

//                AppView appView = new AppView(center, factory);
//                ResizeLayoutPanel appView = new ResizeLayoutPanel();
//                view.setCenter(appView);
                XTest appView = new XTest();
                view.setCenter(appView);

                EventBus eventBus = factory.eventBus();
                final PlaceController placeController = factory.placeController();

                // Start ActivityManager for the main widget with our ActivityMapper
                ActivityMapper activityMapper = new AppActivityMapper(factory);
                ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
                activityManager.setDisplay(appView);

                Place defaultPlace = new AuthPlace();//new ChatPlace();

                // Start PlaceHistoryHandler with our PlaceHistoryMapper
                AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
                PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
                historyHandler.register(placeController, eventBus, defaultPlace);

                //appView.getActionBar().setSearchBox(new TextBox());

                RootLayoutPanel.get().add(view);

                //historyHandler.handleCurrentHistory();

                eventBus.addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {                                
                                if (factory.jaxmpp().getSessionObject().getProperty(SessionObject.USER_BARE_JID) != null) {
                                        placeController.goTo(new ChatPlace());
                                }
                                else {
                                        placeController.goTo(new AuthPlace());
                                }
                        }

                        public void deauthenticated() {
                                placeController.goTo(new AuthPlace());
                        }
                });
                eventBus.addHandler(AuthRequestEvent.TYPE, new AuthRequestHandler() {

                        public void authenticate(JID jid, String password, String boshUrl) {
                                authenticateInt(jid, password, boshUrl);
                        }
                });

                placeController.goTo(new AuthPlace());
                
                if (Cookies.getCookie("username") != null && Cookies.getCookie("password") != null) {
                        authenticateInt(JID.jidInstance(Cookies.getCookie("username")), Cookies.getCookie("password"), null);
                }
                //authenticateTest(factory);
                
                authenticateInt(null, null, null);
        }

        private void authenticateInt3(JID jid, String password, String boshUrl) {
                Jaxmpp jaxmpp = factory.jaxmpp();
//                if (jaxmpp.isConnected()) {
//                       try {
//                                jaxmpp.disconnect();
//                        } catch (JaxmppException ex) {
//                                Logger.getLogger(Xode.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                }
        
                jaxmpp.getProperties().setUserProperty(AbstractBoshConnector.SEE_OTHER_HOST_KEY, true);
                
                if (jid != null) {
                        String url = boshUrl != null ? boshUrl : getBoshUrl(jid.getDomain());
                        jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, url);

//                        jaxmpp.getProperties().setUserProperty(SessionObject.RESOURCE, "jaxmpp");
                        jaxmpp.getProperties().setUserProperty(SessionObject.USER_BARE_JID, jid.getBareJid());
                        jaxmpp.getProperties().setUserProperty(SessionObject.PASSWORD, password);
                        jaxmpp.getProperties().setUserProperty(SessionObject.SERVER_NAME, jid.getDomain());
                }
                else {
                        Dictionary root = Dictionary.getDictionary("root");
                        String domain = root.get("anon-domain");
                        String url = boshUrl != null ? boshUrl : getBoshUrl(domain);
                        jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, url);
                        jaxmpp.getProperties().setUserProperty(SessionObject.SERVER_NAME, domain);
                }
                
                try {
                        jaxmpp.login();
                } catch (JaxmppException ex) {
                        log.log(Level.WARNING, "login exception", ex);
                        //log.log(Level.WARNING, "login exception", ex);
                }
        }

        public void authenticateInt(final JID jid, final String password, String boshUrl) {                 
                // storing jid and password to use it during reconnection for see-other-host
                this.jid = jid;
                this.password = password;
                
                if (boshUrl != null) {
                        authenticateInt3(jid, password, boshUrl);
                        return;
                }
                
                final Dictionary root = Dictionary.getDictionary("root");
                String domain;
                if (jid == null) {
                        domain = root.get("anon-domain");
                }
                else {
                        domain = jid.getDomain();
                }
                                
                authenticateInt2(jid, password, domain);
        }
        
        public void authenticateInt2(final JID jid, final String password, String domain) {                 
                final Dictionary root = Dictionary.getDictionary("root");
                String url = root.get("dns-resolver");
                url += "?domain=" + URL.encodeQueryString(domain);
                JsonpRequestBuilder builder = new JsonpRequestBuilder();
                builder.requestObject(url, new com.google.gwt.user.client.rpc.AsyncCallback<DnsResult>() {
                        
                        public void onFailure(Throwable caught) {
                                String boshUrl = getBoshUrl((jid != null) ? jid.getDomain() : root.get("anon-domain"));
                                authenticateInt3(jid, password, boshUrl);                                
                        }

                        public void onSuccess(DnsResult result) {
                                JsArray<DnsEntry> entriesJs = result.getEntries();
                                String domain = null;
                                int port = 0;
                                // filter only IPv4
                                List<DnsEntry> entries = new ArrayList<DnsEntry>();
                                for (int i=0; i<entriesJs.length(); i++) {
                                        DnsEntry entry = entriesJs.get(i);
                                        if (entry.getIp().contains(":"))
                                                continue;
                                        
                                        entries.add(entry);
                                }
                                if (entries.size() > 1) {
                                        while (domain == null || domain.contains(":")) {
                                                int rand = Random.nextInt(entries.size());
                                                domain = entries.get(rand).getIp();
                                                port = entries.get(rand).getPort();
                                        }                                        
                                }
                                else if (entries.size() > 0) {
                                        domain = entries.get(0).getResultHost();
                                        port = entries.get(0).getPort();
                                }
                                else {
                                        domain = jid.getDomain();
                                }                                
                                String boshUrl = "http://" + domain + (port != 0 ? (":" + port) : "") + "/bosh";                                
                                authenticateInt3(jid, password, boshUrl);                                
                        }
                        
                });                
        }
        
        public static String getBoshUrl(String domain) {
                Dictionary domains = Dictionary.getDictionary("domains");
                String url = "http://" + domain + ":5280/bosh";
                if (domains != null) {
                        Set<String> keys = domains.keySet();
                        if (keys.contains(domain)) {
                                url = domains.get(domain);
                        } else if (keys.contains("default")) {
                                url = domains.get("default");
                        }
                }
                return url;
        }
        
        private class XTest extends ResizeComposite implements ProvidesResize, AcceptsOneWidget {

                private final ResizablePanel panel;
                private IsWidget widget = null;
                
                public XTest() {
                        panel = new ResizablePanel();
                        
                        initWidget(panel);
                }                
                
                @Override
                public void onResize() {
                        super.onResize();
                        
                        if (widget != null && widget instanceof RequiresResize) {
                                ((RequiresResize) widget).onResize();
                        }
                }

                public void setWidget(IsWidget w) {
                        
                        if (widget != null) {
                                panel.remove(widget);
                        }
                        
                        widget = w;
                        
                        log.info("setting widget = " + w);
                        
                        if (w != null) {
                                panel.add(w);
                                
                                Style style = w.asWidget().getElement().getStyle();
                                style.setPosition(Style.Position.ABSOLUTE);
                                style.setLeft(0, Unit.EM);
                                style.setRight(0, Unit.EM);
                                style.setTop(0, Unit.EM);
                                style.setBottom(0, Unit.EM);
                                         
                                if (widget != null && widget instanceof RequiresResize) {
                                        ((RequiresResize) widget).onResize();
                                }
                        }
                }
                
        }
}
