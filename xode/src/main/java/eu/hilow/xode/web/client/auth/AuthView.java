/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.auth;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.*;
import eu.hilow.gwt.base.client.ResizablePanel;
import eu.hilow.gwt.base.client.auth.AbstractAuthView;
import eu.hilow.gwt.base.client.auth.AuthEvent;
import eu.hilow.gwt.base.client.auth.AuthHandler;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.other.TigaseMessengerPromoPanel;
import eu.hilow.xode.web.client.pubsub.PubSubPanel;
import eu.hilow.xode.web.client.register.RegisterDialog;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public class AuthView extends AbstractAuthView {
        
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
                                panel.requestEntries(BareJID.bareJIDInstance("pubsub",domain), "news");
                        }

                        public void deauthenticated() {
                        }
                
                });
                
                layout.add(new TigaseMessengerPromoPanel());
                
                initWidget(layout);
        }
        
}
