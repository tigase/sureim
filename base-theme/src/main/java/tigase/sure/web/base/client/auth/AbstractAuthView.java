/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.auth;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import java.util.ArrayList;
import java.util.List;
import tigase.sure.web.base.client.ClientFactory;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule;

/**
 *
 * @author andrzej
 */
public class AbstractAuthView extends ResizeComposite {
        
        protected final ClientFactory factory;
        
        private TextBox username;
		private String selectedDomain;
        private TextBox password;
        private Button authButton;
        private DisclosurePanel disclosure;
        private TextBox boshUrl;
		private AbsolutePanel errorPanel;
        
        public AbstractAuthView(ClientFactory factory_) {
                this.factory = factory_;
                
                this.factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {

                        public void authenticated(JID jid) {
                                authFinished();
                        }

                        public void deauthenticated(String msg, SaslModule.SaslError saslError) {
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
				errorPanel.setVisible(false);
                authButton.setEnabled(false);
                authButton.removeStyleName(factory.theme().style().buttonDefault());
                authButton.addStyleName(factory.theme().style().buttonDisabled());                
                String url = (boshUrl != null) ? boshUrl.getText() : null;
                if (url != null) {
                        url = url.trim();
                        if (url.isEmpty()) {
                                url = null;
                        }
                }
                factory.eventBus().fireEvent(new AuthRequestEvent(JID.jidInstance(username.getText(), selectedDomain), password.getText(), url));
        }
        
        protected AbsolutePanel createAuthBox(boolean advanced) {
                AbsolutePanel panel = new AbsolutePanel();

                Label header = new Label(factory.baseI18n().authenticate());
                header.setStyleName(factory.theme().style().authHeader());
                panel.add(header);
                
                Label usernameLabel = new Label(factory.baseI18n().username());
                usernameLabel.setStyleName(factory.theme().style().authLabel());
                panel.add(usernameLabel);
                
                username = new TextBox();
                username.setStyleName(factory.theme().style().authTextBox());
                panel.add(username);
				
			Label domainLabel = new Label(factory.baseI18n().domain());
			domainLabel.setStyleName(factory.theme().style().authLabel());
			panel.add(domainLabel);

			Dictionary root = Dictionary.getDictionary("root");
			String domainsStr = root.get("hosted-domains");
			JSONArray domainsArr = (JSONArray) JSONParser.parseLenient(domainsStr);

			final ListBox domainSelector = new ListBox();
			List<String> knownDomains = new ArrayList<>();
			knownDomains.add(Window.Location.getHostName());
			for (int i = 0; i < domainsArr.size(); i++) {
				String serverName = ((JSONString) domainsArr.get(i)).stringValue();
				if (!knownDomains.contains(serverName)) {
					knownDomains.add(serverName);
				}
			}
			for (String serverName : knownDomains) {
				domainSelector.addItem(serverName);
			}
			domainSelector.addItem(factory.baseI18n().other()+"...");
			domainSelector.setSelectedIndex(0);
			domainSelector.getElement().getStyle().setWidth(100, Style.Unit.PCT);
			panel.add(domainSelector);

			final TextBox customDomain = new TextBox();
			customDomain.getElement().getStyle().setWidth(100, Style.Unit.PCT);
			customDomain.setStyleName(factory.theme().style().authTextBox());
			customDomain.setVisible(false);
			customDomain.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					selectedDomain = customDomain.getValue();
				}
			});
			panel.add(customDomain);

			selectedDomain = domainSelector.getSelectedValue();
			domainSelector.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					selectedDomain = domainSelector.getSelectedValue();
					if ((factory.baseI18n().other() + "...").equals(selectedDomain)) {
						selectedDomain = null;
						customDomain.setVisible(true);
					} else {
						customDomain.setVisible(false);
					}
				}
			});

                
                Label passwordLabel = new Label(factory.baseI18n().password());
                passwordLabel.setStyleName(factory.theme().style().authLabel());
                panel.add(passwordLabel);
                
                password = new PasswordTextBox();
                password.setStyleName(factory.theme().style().authTextBox());
                panel.add(password);
                
                if (advanced) {
                        disclosure = new DisclosurePanel(factory.baseI18n().advanced());

                        Label boshUrlLabel = new Label(factory.baseI18n().connectionUrlBoshWs() + ":");
                        FlowPanel disclosurePanel = new FlowPanel();
                        disclosurePanel.add(boshUrlLabel);
                        boshUrl = new TextBox();
                        boshUrl.setStyleName(factory.theme().style().authTextBox());
                        boshUrl.getElement().getStyle().setWidth(97, Style.Unit.PCT);
                        disclosurePanel.add(boshUrl);

                        disclosure.add(disclosurePanel);
                        disclosure.getElement().getStyle().setWidth(100, Style.Unit.PCT);

                        panel.add(disclosure);
                }
                
				errorPanel = new AbsolutePanel();
				errorPanel.setStyleName(factory.theme().style().errorPanelStyle());
				errorPanel.setVisible(false);
				panel.add(errorPanel);
				
				factory.eventBus().addHandler(AuthEvent.TYPE, new AuthHandler() {
					public void authenticated(JID jid) {
						errorPanel.setVisible(false);
					}

					public void deauthenticated(String msg, SaslModule.SaslError saslError) {
						if (msg != null) {
							errorPanel.getElement().setInnerText(msg);
						}
						if (saslError != null) {
							errorPanel.getElement().setInnerText("Authentication error: " + saslError.name());
						}
						if (msg != null || saslError != null) {
							errorPanel.setVisible(true);
						}
					}
				});
				
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
