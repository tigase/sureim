/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.auth;

import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.Widget;
import eu.hilow.gwt.base.client.ResizablePanel;
import eu.hilow.gwt.base.client.auth.AbstractAuthView;
import eu.hilow.gwt.base.client.auth.AuthEvent;
import eu.hilow.gwt.base.client.auth.AuthHandler;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.pubsub.PubSubPanel;
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

                final PubSubPanel panel = new PubSubPanel(factory);
                layout.add(panel);
                panel.getElement().getStyle().setFloat(Style.Float.LEFT);
                panel.getElement().getStyle().setMargin(5, Style.Unit.PCT);
                panel.getElement().getStyle().setWidth(50, Style.Unit.PCT);
                factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {
                                Dictionary root = Dictionary.getDictionary("root");
                                String domain = root.get("anon-domain");                                
                                panel.requestEntries(BareJID.bareJIDInstance("pubsub",domain), "news");
                        }

                        public void deauthenticated() {
                                throw new UnsupportedOperationException("Not supported yet.");
                        }
                
                });
                
                Widget w = createAuthBox();
                layout.add(w);
                w.getElement().getStyle().setFloat(Style.Float.RIGHT);
                w.getElement().getStyle().setMargin(5, Style.Unit.PCT);
                
                initWidget(layout);
        }
        
}
