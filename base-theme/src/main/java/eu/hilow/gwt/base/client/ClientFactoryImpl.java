/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import tigase.jaxmpp.core.client.XmppSessionLogic.SessionListener;
import tigase.jaxmpp.core.client.connector.ConnectorWrapper;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.gwt.client.Jaxmpp;

/**
 *
 * @author andrzej
 */
public abstract class ClientFactoryImpl implements ClientFactory {

        private final Theme theme = GWT.create(Theme.class);
        private final Jaxmpp jaxmpp = new Jaxmpp() {

                private void intLogin() throws JaxmppException {
                        if (this.sessionLogic != null) {
                                this.sessionLogic.unbind();
                                this.sessionLogic = null;
                        }

                        try {
                                ((ConnectorWrapper) this.connector).setConnector(new BoshConnector(observable, this.sessionObject));
                        } catch (Exception ex) {
                        }
                        this.sessionLogic = connector.createSessionLogic(modulesManager, this.writer);
                        this.sessionLogic.bind(new SessionListener() {

                                @Override
                                public void onException(JaxmppException e) throws JaxmppException {
                                        //Jaxmpp.this.onException(e);
                                }
                        });

                        try {
                                this.connector.start();
                        } catch (XMLException e1) {
                                throw new JaxmppException(e1);
                        }
                }

                @Override
                public void login() throws JaxmppException {
                        super.login();
                        sessionObject.clear();
                        intLogin();
                }
        };
        
        private final EventBus eventBus = GWT.create(SimpleEventBus.class);
        
        private BaseI18n baseI18n = GWT.create(BaseI18n.class);

        public EventBus eventBus() {
                return eventBus;
        }

        public Theme theme() {
                return theme;
        }

        public Jaxmpp jaxmpp() {
                return jaxmpp;
        }
        
        public BaseI18n baseI18n() {
                return baseI18n;
        }
}
