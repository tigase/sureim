/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.register;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.MessageDialog;
import eu.hilow.xode.web.client.Xode;
import eu.hilow.xode.web.client.chat.ChatViewImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.connector.AbstractBoshConnector;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.DefaultElement;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule;
import tigase.jaxmpp.core.client.xmpp.modules.ResourceBinderModule.ResourceBindEvent;
import tigase.jaxmpp.core.client.xmpp.modules.StreamFeaturesModule;
import tigase.jaxmpp.core.client.xmpp.modules.StreamFeaturesModule.StreamFeaturesReceivedEvent;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule.RegistrationEvent;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;

/**
 *
 * @author andrzej
 */
public class RegisterDialog extends DialogBox {
        
        private static final Logger log = Logger.getLogger("RegisterDialog");
        private final ClientFactory factory;
        
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
                
                label = new Label(factory.baseI18n().jid());
                table.setWidget(1, 0, label);
                final TextBox jidTextBox = new TextBox();
                table.setWidget(1, 1, jidTextBox);
                
                label = new Label(factory.baseI18n().password());
                table.setWidget(2, 0, label);
                final TextBox passwordTextBox = new PasswordTextBox();
                table.setWidget(2, 1, passwordTextBox);
                
                label = new Label(factory.i18n().email());
                table.setWidget(3, 0, label);
                final TextBox emailTextBox = new TextBox();
                table.setWidget(3, 1, emailTextBox);

                Button cancel = new Button(factory.baseI18n().cancel());
                cancel.setStyleName(factory.theme().style().button());
                cancel.addStyleName(factory.theme().style().left());
                table.setWidget(4, 0, cancel);
                cancel.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                hide();
                        }
                });                
                
                Button ok = new Button(factory.baseI18n().confirm());
                ok.setStyleName(factory.theme().style().button());
                ok.addStyleName(factory.theme().style().buttonDefault());
                ok.addStyleName(factory.theme().style().right());
                table.setWidget(4, 1, ok);
                ok.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                try {
                                        final Jaxmpp jaxmpp = new Jaxmpp();
                                        
                                        final BareJID jid = BareJID.bareJIDInstance(jidTextBox.getText());
                                        final String password = passwordTextBox.getText();
                                        final String email = emailTextBox.getText();
                
                                        final InBandRegistrationModule regModule = jaxmpp.getModulesManager().getModule(InBandRegistrationModule.class);
                                        //jaxmpp.getProperties().setUserProperty(InBandRegistrationModule.IN_BAND_REGISTRATION_MODE_KEY, Boolean.TRUE);                                        
                                        jaxmpp.getProperties().setUserProperty(SessionObject.SERVER_NAME, jid.getDomain());
                                        jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, Xode.getBoshUrl(jid.getDomain()));
                                                                                
                                        regModule.addListener(new Listener<RegistrationEvent>() {
                                                public void handleEvent(RegistrationEvent be) throws JaxmppException {
                                                        String message = null;
                                                        if (be.getType() == InBandRegistrationModule.NotSupportedError) {
                                                                message = "Registration not supported";
                                                        }
                                                        else if (be.getType() == InBandRegistrationModule.ReceivedError) {
                                                                ErrorCondition error = be.getStanza().getErrorCondition();
                                                                if (error == null) {
                                                                        message = "Registration error";
                                                                }
                                                                else {
                                                                        switch (error) {
                                                                                case conflict:
                                                                                        message = "Username not available. Choose another one.";
                                                                                        break;
                                                                                default:
                                                                                        message = error.name();
                                                                                        break;
                                                                        }                                                                        
                                                                }
                                                        }
                                                        else if (be.getType() == InBandRegistrationModule.ReceivedTimeout) {
                                                                message = "Server doesn't responses";
                                                        }
                                                        else if (be.getType() == InBandRegistrationModule.ReceivedRequestedFields) {
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
                                                                                
                                                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), message);
                                                                                dlg.show();
                                                                                dlg.center();
                                                                                jaxmpp.disconnect();
                                                                        }

                                                                        public void onSuccess(Stanza responseStanza) throws JaxmppException {
                                                                                String message = "Registration successful";                                                                        
                                                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().success(), message);
                                                                                dlg.show();
                                                                                dlg.center();
                                                                                hide();
                                                                                jaxmpp.disconnect();
                                                                        }

                                                                        public void onTimeout() throws JaxmppException {
                                                                                String message = "Server doesn't responses";
                                                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), message);
                                                                                dlg.show();
                                                                                dlg.center();
                                                                                jaxmpp.disconnect();
                                                                        }
                                                                        
                                                                });
                                                        }
                                                        if (message != null) {
                                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), message);
                                                                dlg.show();
                                                                dlg.center();
                                                                jaxmpp.disconnect();
                                                        }
                                                }                                                
                                        });

                                        jaxmpp.getModulesManager().getModule(StreamFeaturesModule.class).addListener(StreamFeaturesModule.StreamFeaturesReceived, new Listener<StreamFeaturesReceivedEvent>() {

                                                public void handleEvent(StreamFeaturesReceivedEvent be) throws JaxmppException {
                                                        // fixes unability to change connector logic
                                                        Element features = DefaultElement.create(be.getFeatures());
                                                        Element e = features.getChildrenNS("mechanisms", "urn:ietf:params:xml:ns:xmpp-sasl");
                                                        if (e != null) {
                                                                features.removeChild(e);
                                                        }
                                                        e = features.getChildrenNS("bind", "urn:ietf:params:xml:ns:xmpp-bind");
                                                        if (e != null) {
                                                                features.removeChild(e);
                                                        }
                                     
                                                        be.getSessionObject().setStreamFeatures(features);
                                                        
                                                        regModule.start();
                                                }
                                                
                                        });
                                        
                                        jaxmpp.login();
                                } catch (XMLException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                        
                });
                                
                setWidget(table);                
        }
        
}
