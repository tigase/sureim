/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.roster;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.chat.ChatViewImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class ContactDialog extends DialogBox {
        
        private final ClientFactory factory;
        
        public ContactDialog(ClientFactory factory_, BareJID jid) {
                super(true);
                factory = factory_;
   
                boolean add = jid == null;
                
                setStyleName("dialogBox");
                setTitle(jid == null ? factory.i18n().addContact() : factory.i18n().modifyContact());

                FlexTable table = new FlexTable();
                Label label = new Label(jid == null ? factory.i18n().addContact() : factory.i18n().modifyContact());
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                table.setWidget(0, 0, label);
                
                label = new Label(factory.baseI18n().jid());
                table.setWidget(1, 0, label);
                final TextBox jidTextBox = new TextBox();
                table.setWidget(1, 1, jidTextBox);
                
                label = new Label(factory.baseI18n().name());
                table.setWidget(2, 0, label);
                final TextBox nameTextBox = new TextBox();
                table.setWidget(2, 1, nameTextBox);
                
                label = new Label(factory.baseI18n().group());
                table.setWidget(3, 0, label);
                final TextBox groupTextBox = new TextBox();
                table.setWidget(3, 1, groupTextBox);

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
                                        final BareJID jid = BareJID.bareJIDInstance(jidTextBox.getText());
                                        String name = nameTextBox.getText();
                                        String group = groupTextBox.getText();
                                                              
                                        String[] groups = null;
                                        if (group != null && !group.isEmpty()) {
                                                groups = new String[] { group };
                                        }
 
                                        factory.jaxmpp().getRoster().add(jid, name, groups, new AsyncCallback() {

                                                public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
                                                }

                                                public void onSuccess(Stanza responseStanza) throws JaxmppException {
                                                        JID jidFull = JID.jidInstance(jid);
                                                        factory.jaxmpp().getModulesManager().getModule(PresenceModule.class).subscribe(jidFull);
                                                        hide();
                                                }

                                                public void onTimeout() throws JaxmppException {
                                                }
                                                
                                        });
                                } catch (XMLException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                        
                });
                
                if (jid != null) {
                        RosterItem ri = factory.jaxmpp().getRoster().get(jid);
                        jidTextBox.setText(ri.getJid().toString());
                        nameTextBox.setText(ri.getName());
                        String group = "";
                        if (ri.getGroups() != null && !ri.getGroups().isEmpty()) {
                                groupTextBox.setText(ri.getGroups().get(0));
                        }
                }
                
                setWidget(table);                
        }
        
}
