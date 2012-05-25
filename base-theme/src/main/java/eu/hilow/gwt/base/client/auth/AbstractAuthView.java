/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.auth;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import eu.hilow.gwt.base.client.ClientFactory;
import eu.hilow.gwt.base.client.ResizablePanel;
import eu.hilow.gwt.base.client.Showdown;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public class AbstractAuthView extends ResizeComposite {
        
        protected final ClientFactory factory;
        
        private TextBox username;
        private TextBox password;
        private Button authButton;
        
        public AbstractAuthView(ClientFactory factory_) {
                this.factory = factory_;
                
                this.factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {
                                authFinished();
                        }

                        public void deauthenticated() {
                                authFinished();
                        }
                        
                });
        }

        private void authFinished() {
                authButton.setEnabled(true);
                authButton.addStyleName(factory.theme().style().buttonDefault());
                authButton.removeStyleName(factory.theme().style().buttonDisabled());                
        }
        
        private void handle() {
                authButton.setEnabled(false);
                authButton.removeStyleName(factory.theme().style().buttonDefault());
                authButton.addStyleName(factory.theme().style().buttonDisabled());
                factory.eventBus().fireEvent(new AuthRequestEvent(JID.jidInstance(username.getText()), password.getText()));
        }
        
        protected AbsolutePanel createAuthBox() {
                AbsolutePanel panel = new AbsolutePanel();

                Label header = new Label(factory.baseI18n().authenticate());
                header.setStyleName(factory.theme().style().authHeader());
                panel.add(header);
                
                Label jidLabel = new Label(factory.baseI18n().jid());
                jidLabel.setStyleName(factory.theme().style().authLabel());
                panel.add(jidLabel);
                
                username = new TextBox();
                username.setStyleName(factory.theme().style().authTextBox());
                panel.add(username);
                
                Label passwordLabel = new Label(factory.baseI18n().password());
                passwordLabel.setStyleName(factory.theme().style().authLabel());
                panel.add(passwordLabel);
                
                password = new PasswordTextBox();
                password.setStyleName(factory.theme().style().authTextBox());
                panel.add(password);
                
                authButton = new Button(factory.baseI18n().authenticate());
                authButton.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                                handle();
                        }                        
                });
                authButton.setStyleName(factory.theme().style().authButton());
                panel.add(authButton);
                
                panel.setStyleName(factory.theme().style().authPanel());
               
                KeyUpHandler handler = new KeyUpHandler() {

                        public void onKeyUp(KeyUpEvent event) {
                                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                                        authButton.click();
                                }
                        }
                        
                };
                
                username.addKeyUpHandler(handler);
                password.addKeyUpHandler(handler);
                
                return panel;
        }
        
}
