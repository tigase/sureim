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
package tigase.sure.web.site.client.auth;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.base.client.auth.AbstractAuthView;
import tigase.sure.web.base.client.auth.AuthEvent;
import tigase.sure.web.base.client.auth.AuthHandler;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.other.TigaseMessengerPromoPanel;
import tigase.sure.web.site.client.pubsub.PubSubPanel;
import tigase.sure.web.site.client.register.RegisterDialog;

/**
 * @author andrzej
 */
public class AuthView
		extends AbstractAuthView {

	private final ClientFactory factory;

	public AuthView(ClientFactory clientFactory) {
		super(clientFactory);

		this.factory = clientFactory;

		ResizablePanel layout = new ResizablePanel();

		HTML titleHeader = new HTML();
		titleHeader.setHTML("<h1>" + factory.i18n().authViewHeaderTitle() + "</h1>");
		titleHeader.setStyleName("authViewHeaderTitle");
		layout.add(titleHeader);

		layout.addStyleName("authViewStyle");

		AbsolutePanel w = createAuthBox(true);
		Image logo = new Image();
		logo.setUrl("logo.png");
		logo.getElement().getStyle().setMarginTop(-20, Style.Unit.PX);
		logo.getElement().getStyle().setFloat(Style.Float.RIGHT);
		logo.setWidth("64px");
		w.insert(logo, 0);

		//Anchor register = new Anchor("Register");
		Button register = new Button(factory.i18n().register());
		register.setStyleName(factory.theme().style().button());
		register.getElement().getStyle().setFloat(Style.Float.RIGHT);
		register.getElement().getStyle().setBackgroundColor("white");
		register.getElement().getStyle().setMarginTop(1.5, Style.Unit.EM);
		register.getElement().getStyle().setMarginRight(-0.5, Style.Unit.EM);
		register.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				RegisterDialog regDlg = new RegisterDialog(factory);
				regDlg.show();
				regDlg.center();
			}
		});
		w.add(register);

		layout.add(w);
		w.getElement().getStyle().setFloat(Style.Float.RIGHT);
		w.getElement().getStyle().setMargin(2, Style.Unit.PCT);
		w.getElement().getStyle().setMarginBottom(1, Style.Unit.PCT);

		final PubSubPanel panel = new PubSubPanel(factory);
		layout.add(panel);
		panel.getElement().getStyle().setFloat(Style.Float.LEFT);
		panel.getElement().getStyle().setProperty("margin", "0% 2%");
		panel.getElement().getStyle().setWidth(33, Style.Unit.PCT);
		panel.getElement().getStyle().setProperty("clear", "left");
		factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {

			public void authenticated(JID jid) {
				Dictionary root = Dictionary.getDictionary("root");
				String domain = root.get("anon-domain");
				panel.requestEntries(BareJID.bareJIDInstance("pubsub", domain), "news");
			}

			public void deauthenticated(String msg, SaslModule.SaslError saslError) {
			}

		});

		layout.add(new TigaseMessengerPromoPanel());

		initWidget(layout);
	}

}
