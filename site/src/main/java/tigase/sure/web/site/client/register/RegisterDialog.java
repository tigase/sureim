/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.register;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.ElementFactory;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.StreamFeaturesModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.UnifiedRegistrationForm;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.MessageDialog;
import tigase.sure.web.site.client.Xode;
import tigase.sure.web.site.client.chat.ChatViewImpl;

/**
 *
 * @author andrzej
 */
public class RegisterDialog extends DialogBox {
        
        private static final Logger log = Logger.getLogger("RegisterDialog");
		
        private final ClientFactory factory;

		private Jaxmpp jaxmpp;
		private RegisterHandler regHandler;
        private final Button ok;
		
		private BareJID jid;
		private String password;
		private String email;
        
        public RegisterDialog(ClientFactory factory_) {
                super(true);
                factory = factory_;
   
                setStyleName("dialogBox");
                setTitle(factory.i18n().registerAccount());

                FlexTable table = new FlexTable();
                Label label = new Label(factory.i18n().registerAccount());
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                table.setWidget(0, 0, label);
                
                label = new Label(factory.baseI18n().login());
                table.setWidget(1, 0, label);
                final TextBox loginTextBox = new TextBox();
                table.setWidget(1, 1, loginTextBox);
                
                label = new Label(factory.baseI18n().domain());
                table.setWidget(2, 0, label);
                final TextBox domainTextBox = new TextBox();                
                domainTextBox.setText(Window.Location.getHostName());
                table.setWidget(2, 1, domainTextBox);
                
                label = new Label(factory.baseI18n().password());
                table.setWidget(3, 0, label);
                final TextBox passwordTextBox = new PasswordTextBox();
                table.setWidget(3, 1, passwordTextBox);
                
                label = new Label(factory.i18n().email());
                table.setWidget(4, 0, label);
                final TextBox emailTextBox = new TextBox();
                table.setWidget(4, 1, emailTextBox);

                Button cancel = new Button(factory.baseI18n().cancel());
                cancel.setStyleName(factory.theme().style().button());
                cancel.addStyleName(factory.theme().style().left());
                table.setWidget(5, 0, cancel);
                cancel.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                hide();
                        }
                });                
                
                ok = new Button(factory.baseI18n().confirm());
                ok.setStyleName(factory.theme().style().button());
                ok.addStyleName(factory.theme().style().buttonDefault());
                ok.addStyleName(factory.theme().style().right());
                table.setWidget(5, 1, ok);
                ok.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                try {
                                        disableOkButton();
                                        jaxmpp = new Jaxmpp();
                                        
                                        String login = loginTextBox.getText();
                                        String domain = domainTextBox.getText();

                                        String errorMessage = null;
                                        if (login == null || login.isEmpty()) {
                                                errorMessage = "Login is required";
                                        }
                                        if (domain == null || domain.isEmpty()) {
                                                errorMessage  = "Domain is required";
                                        }
                                        
                                        if (errorMessage != null) {
                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), errorMessage);
                                                dlg.show();
                                                dlg.center();
                                                jaxmpp.disconnect();                 
                                                enableOkButton();
                                                return;
                                        }
                                        
                                        jid = BareJID.bareJIDInstance(login, domain);
                                        password = passwordTextBox.getText();
                                        email = emailTextBox.getText();
                
										regHandler = new RegisterHandler();
										jaxmpp.getModulesManager().register(new InBandRegistrationModule());
                                        final InBandRegistrationModule regModule = jaxmpp.getModulesManager().getModule(InBandRegistrationModule.class);
                                        //jaxmpp.getProperties().setUserProperty(InBandRegistrationModule.IN_BAND_REGISTRATION_MODE_KEY, Boolean.TRUE);                                        
                                        jaxmpp.getProperties().setUserProperty(SessionObject.SERVER_NAME, jid.getDomain());
                                        jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, Xode.getBoshUrl(jid.getDomain()));
                                                                                
										regModule.addNotSupportedErrorHandler(regHandler);
										regModule.addReceivedErrorHandler(regHandler);
										regModule.addReceivedRequestedFieldsHandler(regHandler);
										regModule.addReceivedTimeoutHandler(regHandler);

                                        jaxmpp.getModulesManager().getModule(StreamFeaturesModule.class).addStreamFeaturesReceivedHandler(new StreamFeaturesModule.StreamFeaturesReceivedHandler() {

											@Override
											public void onStreamFeaturesReceived(SessionObject sessionObject, Element featuresElement) throws JaxmppException {
												Element features = ElementFactory.create(featuresElement);
												Element e = features.getChildrenNS("mechanisms", "urn:ietf:params:xml:ns:xmpp-sasl");
												if (e != null) {
													features.removeChild(e);
												}
												e = features.getChildrenNS("bind", "urn:ietf:params:xml:ns:xmpp-bind");
												if (e != null) {
													features.removeChild(e);
												}

												sessionObject.setProperty("StreamFeaturesModule#STREAM_FEATURES_ELEMENT", features);

												regModule.start();
											}
										});
                                        
                                        jaxmpp.login();
                                } catch (XMLException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                        enableOkButton();
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                        enableOkButton();                                        
                                }
                        }
                        
                });
                                
                setWidget(table);                
        }
        
        private void disableOkButton() {
                ok.setEnabled(false);
                ok.removeStyleName(factory.theme().style().buttonDefault());
                ok.addStyleName(factory.theme().style().buttonDisabled());
        }

        private void enableOkButton() {
                ok.setEnabled(false);
                ok.removeStyleName(factory.theme().style().buttonDisabled());
                ok.addStyleName(factory.theme().style().buttonDefault());
        }
		
		private void showError(String message) throws JaxmppException {
			MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), message);
			dlg.show();
			dlg.center();
			jaxmpp.disconnect();
			enableOkButton();		
		}

		private class RegisterHandler implements InBandRegistrationModule.NotSupportedErrorHandler, InBandRegistrationModule.ReceivedErrorHandler, 
				InBandRegistrationModule.ReceivedRequestedFieldsHandler, InBandRegistrationModule.ReceivedTimeoutHandler {

		@Override
		public void onNotSupportedError(SessionObject sessionObject) throws JaxmppException {
			showError("Registration not supported");
		}

		@Override
		public void onReceivedError(SessionObject sessionObject, IQ responseStanza, ErrorCondition error) throws JaxmppException {
			String message = null;
			if (error == null) {
				message = "Registration error";
			} else {
				switch (error) {
					case conflict:
						message = "Username not available. Choose another one.";
						break;
					default:
						message = error.name();
						break;
				}
			}
			if (message != null) {
				showError(message);
			}
		}

		@Override
		public void onReceivedRequestedFields(SessionObject sessionObject, IQ responseStanza, UnifiedRegistrationForm unifiedRegistrationForm) {
			try {
				final InBandRegistrationModule regModule = jaxmpp.getModulesManager().getModule(InBandRegistrationModule.class);
				regModule.register(jid.toString(), password, email, new AsyncCallback() {

					public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
						String message = null;

						if (error == null) {
							message = "Registration error";
						} else {
							switch (error) {
								case conflict:
									message = "Username not available. Choose another one.";
									break;
								default:
									message = error.name();
									break;
							}
						}

						showError(message);
					}

					public void onSuccess(Stanza responseStanza) throws JaxmppException {
						String message = "Registration successful";
						MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().success(), message);
						dlg.show();
						dlg.center();
						hide();
						jaxmpp.disconnect();
						enableOkButton();
					}

					public void onTimeout() throws JaxmppException {
						String message = "Server doesn't responses";
						showError(message);
					}

				});
			} catch (JaxmppException ex) {
				log.log(Level.SEVERE, "Exception while requesting registration", ex);
			}
		}

		@Override
		public void onReceivedTimeout(SessionObject sessionObject) throws JaxmppException {
			showError("Server doesn't responses");
		}
	}
}
