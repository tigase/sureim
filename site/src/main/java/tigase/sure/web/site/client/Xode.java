package tigase.sure.web.site.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.ConnectionConfiguration;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;
import tigase.jaxmpp.core.client.xmpp.modules.streammng.StreamManagementModule;
import tigase.jaxmpp.gwt.client.GwtSessionObject;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;
import tigase.jaxmpp.gwt.client.connectors.WebSocket;
import tigase.jaxmpp.gwt.client.connectors.WebSocketConnector;
import tigase.jaxmpp.gwt.client.dns.WebDnsResolver;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.base.client.RootView;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AuthHandler;
import tigase.sure.web.base.client.auth.AuthRequestEvent;
import tigase.sure.web.base.client.auth.AuthRequestHandler;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatPlace;

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
        
		private DnsResult dnsResult = null;
		
		/**
         * This is the entry point method.
         */
        public void onModuleLoad() {
                factory = GWT.create(ClientFactory.class);
                factory.theme().style().ensureInjected();

                RootView view = new RootView(factory);

				
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
				final Anchor logout = new Anchor(factory.i18n().logout());
				logout.setStyleName(factory.theme().style().navigationBarItem());
				logout.addStyleName(factory.theme().style().right());
				logout.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						try {
							factory.jaxmpp().disconnect();
						} catch (JaxmppException ex) {
							Logger.getLogger(Xode.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				});	
				logout.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
				view.getNav().add(logout);

                RootLayoutPanel.get().add(view);

                //historyHandler.handleCurrentHistory();

                eventBus.addHandler(AuthEvent.TYPE, new AuthHandler() {
						@Override
                        public void authenticated(JID jid) {                                
                                if (factory.jaxmpp().getSessionObject().getProperty(SessionObject.USER_BARE_JID) != null) {
										logout.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
                                        placeController.goTo(new ChatPlace());
                                }
                                else {
										logout.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
                                        placeController.goTo(new AuthPlace());
                                }
                        }
						@Override
                        public void deauthenticated(String msg, SaslModule.SaslError saslError) {
								logout.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
                                placeController.goTo(new AuthPlace());
                        }
                });
                eventBus.addHandler(AuthRequestEvent.TYPE, new AuthRequestHandler() {
						@Override
                        public void authenticate(JID jid, String password, String boshUrl) {
                                authenticateInt(jid, password, boshUrl);
                        }
                });

                placeController.goTo(new AuthPlace());
                
			Window.addWindowClosingHandler(new Window.ClosingHandler() {
				@Override
				public void onWindowClosing(Window.ClosingEvent event) {
					if (factory.jaxmpp().isConnected() && factory.sessionObject().getUserBareJid() != null 
							&& factory.jaxmpp().getSessionObject().getProperty(BoshConnector.BOSH_SERVICE_URL_KEY) != null
							&& ((String) factory.jaxmpp().getSessionObject().getProperty(BoshConnector.BOSH_SERVICE_URL_KEY)).startsWith("ws")) {
						String session = ((GwtSessionObject) factory.sessionObject()).serialize();
						if (session != null) {
							storeSerializedSession(session);
						}
					}
				}
			});				
				
				String session = restoreSerializedSession();
				if (session != null && !session.isEmpty()) {
					GwtSessionObject sessionObject = (GwtSessionObject) factory.sessionObject();
					try {
						sessionObject.restore(session);
						final Jaxmpp jaxmpp = factory.jaxmpp();
						Timer t = new Timer() {
							@Override
							public void run() {
								try {
									new StreamManagementResumptionHandler(jaxmpp).resume();
								} catch (JaxmppException ex) {
									Logger.getLogger(Xode.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						};
						t.schedule(100);
					} catch (GwtSessionObject.RestoringSessionException ex) {
						Logger.getLogger(Xode.class.getName()).log(Level.SEVERE, null, ex);
//					} catch (JaxmppException ex) {
//						Logger.getLogger(Xode.class.getName()).log(Level.SEVERE, null, ex);
					}
				} else {
					if (Cookies.getCookie("username") != null && Cookies.getCookie("password") != null) {
							authenticateInt(JID.jidInstance(Cookies.getCookie("username")), Cookies.getCookie("password"), null);
					}
					//authenticateTest(factory);

					authenticateInt(null, null, null);
				}
        }

        public void authenticateInt(JID jid, String password, String boshUrl) {   

                // storing jid and password to use it during reconnection for see-other-host
                this.jid = jid;
                this.password = password;
                
				final Dictionary root = Dictionary.getDictionary("root");
				
				ConnectionConfiguration connCfg = factory.jaxmpp().getConnectionConfiguration();
				connCfg.setDomain(null);
				connCfg.setUserJID(jid == null ? null : jid.getBareJid());
				if (jid == null) {
					String domain = root.get("anon-domain");
					connCfg.setDomain(domain);
					if (boshUrl == null) {
						boshUrl = getBoshUrl(domain);
					}
				}
				if (boshUrl != null) {
					factory.jaxmpp().getSessionObject().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, boshUrl);
				} else {
					factory.jaxmpp().getSessionObject().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, null);
					String webDnsResolver = root.get("dns-resolver");
					factory.jaxmpp().getSessionObject().setUserProperty(WebDnsResolver.WEB_DNS_RESOLVER_URL_KEY, webDnsResolver);
				}
				connCfg.setUserPassword(password);
				try {
                        factory.jaxmpp().login();
                } catch (JaxmppException ex) {
                        log.log(Level.WARNING, "login exception", ex);
                        //log.log(Level.WARNING, "login exception", ex);
                }
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
				if (WebSocket.isSupported()) {
					if (url.startsWith("http://")) {
						url = url.replace("http://", "ws://").replace(":5280", ":5290");
					}
				}
                return url;
        }
		
		private static native void storeSerializedSession(String data) /*-{
			window.sessionStorage.setItem('jaxmppSession', data);
		}-*/;
		
		private static native String restoreSerializedSession() /*-{
			return window.sessionStorage.getItem('jaxmppSession');
		}-*/;
        
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
		
	private class StreamManagementResumptionHandler implements StreamManagementModule.StreamResumedHandler, StreamManagementModule.StreamManagementFailedHandler {

		private final Jaxmpp jaxmpp;
		
		public StreamManagementResumptionHandler(Jaxmpp jaxmpp) {
			this.jaxmpp = jaxmpp;
		}
		
		public void resume() throws JaxmppException {
			try {
				jaxmpp.getEventBus().addHandler(
						StreamManagementModule.StreamManagementFailedHandler.StreamManagementFailedEvent.class, this);
				jaxmpp.getEventBus().addHandler(
						StreamManagementModule.StreamResumedHandler.StreamResumedEvent.class, this);
				jaxmpp.login();
					
			} catch (JaxmppException ex) {
				cleanUp();
				throw ex;
			}			
		}
		
		@Override
		public void onStreamResumed(SessionObject sessionObject, Long h, String previd) throws JaxmppException {
			cleanUp();
			factory.eventBus().fireEvent(new AuthEvent(ResourceBinderModule.getBindedJID(sessionObject)));
		}

		@Override
		public void onStreamManagementFailed(SessionObject sessionObject, XMPPException.ErrorCondition condition) {
			cleanUp();
			//StreamManagementModule.reset(sessionObject);
			try {
//				jaxmpp.login();
				sessionObject.clear();
			} catch (JaxmppException ex) {
				Logger.getLogger(Xode.class.getName()).log(Level.SEVERE, null, ex);
//				factory.eventBus().fireEvent(new AuthEvent(null));
			}
			factory.eventBus().fireEvent(new AuthEvent(null));
		}

		private void cleanUp() {
			jaxmpp.getEventBus().remove(this);
		}
	}
}
