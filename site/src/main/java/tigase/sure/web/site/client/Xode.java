/*
 * Sure.IM site - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
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
import tigase.jaxmpp.core.client.ConnectionConfiguration;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;
import tigase.jaxmpp.core.client.xmpp.modules.streammng.StreamManagementModule;
import tigase.jaxmpp.gwt.client.ConnectionManager;
import tigase.jaxmpp.gwt.client.GwtSessionObject;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;
import tigase.jaxmpp.gwt.client.connectors.WebSocket;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.base.client.RootView;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AuthHandler;
import tigase.sure.web.base.client.auth.AuthRequestEvent;
import tigase.sure.web.base.client.auth.AuthRequestHandler;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatPlace;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point classes define
 * <code>onModuleLoad()</code>.
 */
public class Xode
		implements EntryPoint {

	private static final Logger log = Logger.getLogger("Xode");
	private DnsResult dnsResult = null;
	private ClientFactory factory;
	// need this for reconnect during reaction on see-other-host
	private JID jid = null;
	private String password = null;

	public static String getBoshUrl(String domain) {
		String url = "http://" + domain + ":5280/bosh";
		if (WebSocket.isSupported()) {
			if (url.startsWith("http://")) {
				url = url.replace("http://", "ws://").replace(":5280", ":5290");
			}
		}
		return url;
	}

	private static native String restoreSerializedSession() /*-{
			return window.sessionStorage.getItem('jaxmppSession');
		}-*/;

	private static native void storeSerializedSession(String data) /*-{
			if (!data) {
				window.sessionStorage.removeItem('jaxmppSession');
			} else {
				window.sessionStorage.setItem('jaxmppSession', data);
			}
		}-*/;

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
				} else {
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
				if (factory.jaxmpp().isConnected() && factory.sessionObject().getUserBareJid() != null &&
						factory.jaxmpp().getSessionObject().getProperty(BoshConnector.BOSH_SERVICE_URL_KEY) != null &&
						((String) factory.jaxmpp()
								.getSessionObject()
								.getProperty(BoshConnector.BOSH_SERVICE_URL_KEY)).startsWith("ws")) {
					String session = ((GwtSessionObject) factory.sessionObject()).serialize();
					if (session != null) {
						storeSerializedSession(session);
						return;
					}
				}
				storeSerializedSession(null);
			}
		});

		String session = restoreSerializedSession();
		if (session != null && !session.isEmpty()) {
			storeSerializedSession(null);
			GwtSessionObject sessionObject = (GwtSessionObject) factory.sessionObject();
			try {
				sessionObject.restore(session);
				factory.authView().setAuthButtonEnabled(false);
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
				factory.authView().setAuthButtonEnabled(false);
				authenticateInt(JID.jidInstance(Cookies.getCookie("username")), Cookies.getCookie("password"), null);
				return;
			}
			//authenticateTest(factory);

			//authenticateInt(null, null, null);
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
		}
		if (boshUrl != null) {
			factory.jaxmpp().getSessionObject().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, boshUrl);
		} else {
			factory.jaxmpp().getSessionObject().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, null);

			String defUrl = WebSocket.isSupported()
							? "ws://" + Window.Location.getHostName() + ":5290/"
							: "http://" + Window.Location.getHostName() + ":5280/";
			if (Window.Location.getProtocol().startsWith("https")) {
				defUrl = WebSocket.isSupported()
						 ? "wss://" + Window.Location.getHostName() + ":5291/"
						 : "https://" + Window.Location.getHostName() + ":5281/";
			}
			factory.sessionObject().setUserProperty(ConnectionManager.URL_ON_FAILURE, defUrl);
		}
		connCfg.setUserPassword(password);
		try {
			factory.jaxmpp().login();
		} catch (JaxmppException ex) {
			log.log(Level.WARNING, "login exception", ex);
			//log.log(Level.WARNING, "login exception", ex);
		}
	}

	private class StreamManagementResumptionHandler
			implements StreamManagementModule.StreamResumedHandler,
					   StreamManagementModule.StreamManagementFailedHandler {

		private final Jaxmpp jaxmpp;

		public StreamManagementResumptionHandler(Jaxmpp jaxmpp) {
			this.jaxmpp = jaxmpp;
		}

		public void resume() throws JaxmppException {
			try {
				jaxmpp.getEventBus()
						.addHandler(
								StreamManagementModule.StreamManagementFailedHandler.StreamManagementFailedEvent.class,
								this);
				jaxmpp.getEventBus()
						.addHandler(StreamManagementModule.StreamResumedHandler.StreamResumedEvent.class, this);
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
				jaxmpp.disconnect();
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

	private class XTest
			extends ResizeComposite
			implements ProvidesResize, AcceptsOneWidget {

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
