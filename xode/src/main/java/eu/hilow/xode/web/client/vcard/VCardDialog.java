/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.vcard;

import eu.hilow.xode.web.client.roster.*;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.MessageDialog;
import eu.hilow.xode.web.client.chat.ChatViewImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCard;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule.VCardAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class VCardDialog extends DialogBox {
        
        private final ClientFactory factory;
        private final Image avatar;
        
        public VCardDialog(ClientFactory factory_, BareJID jid) {
                super(true);
                factory = factory_;
   
                setStyleName("dialogBox");
                setTitle(factory.i18n().personalInformation());

                FlexTable table = new FlexTable();
                Label label = new Label(factory.i18n().personalInformation());
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                table.setWidget(0, 0, label);

                label = new Label(factory.i18n().avatar());
                table.setWidget(1, 0, label);
                
                avatar = new Image();
                avatar.setHeight("120px");                
                avatar.setUrl(factory.theme().socialPerson().getSafeUri());
                table.setWidget(1, 1, avatar);
                
                label = new Label(factory.baseI18n().name());
                table.setWidget(2, 0, label);
                final Label nameLabel = new Label();
                table.setWidget(2, 1, nameLabel);
                
                label = new Label(factory.baseI18n().jid());
                table.setWidget(3, 0, label);
                final Label jidLabel = new Label(jid.toString());
                table.setWidget(3, 1, jidLabel);
                
                label = new Label(factory.i18n().birthday());
                table.setWidget(4, 0, label);
                final Label birthdayLabel = new Label();
                table.setWidget(4, 1, birthdayLabel);

                label = new Label(factory.i18n().email());
                table.setWidget(5, 0, label);
                final Label emailLabel = new Label();
                table.setWidget(5, 1, emailLabel);

                Button close = new Button(factory.baseI18n().close());
                close.setStyleName(factory.theme().style().button());
                close.addStyleName(factory.theme().style().buttonDefault());
                close.addStyleName(factory.theme().style().left());
                table.setWidget(6, 0, close);
                close.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                hide();
                        }
                });                
                                
                if (jid != null) {
                        try {
                                factory.jaxmpp().getModulesManager().getModule(VCardModule.class).retrieveVCard(JID.jidInstance(jid), new VCardAsyncCallback() {

                                        @Override
                                        protected void onVCardReceived(VCard vcard) throws XMLException {
                                                nameLabel.setText(vcard.getFullName());                                                
                                                birthdayLabel.setText(vcard.getBday());
                                                emailLabel.setText(vcard.getHomeEmail());
                                                if (vcard.getPhotoType() != null && vcard.getPhotoVal() != null) {
                                                        avatar.setUrl("data:" + vcard.getPhotoType() + ";base64," + vcard.getPhotoVal());
                                                } else {
                                                        avatar.setUrl(factory.theme().socialPerson().getSafeUri());
                                                }
                                        }

                                        public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                                hide();
                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), error.getElementName());
                                                dlg.show();
                                                dlg.center();
                                        }

                                        public void onTimeout() throws JaxmppException {
                                                hide();
                                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), factory.i18n().requestTimedOut());
                                                dlg.show();
                                                dlg.center();
                                        }
                                        
                                });
                        } catch (JaxmppException ex) {
                                Logger.getLogger(VCardDialog.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
                
                setWidget(table);                
        }
        
}
