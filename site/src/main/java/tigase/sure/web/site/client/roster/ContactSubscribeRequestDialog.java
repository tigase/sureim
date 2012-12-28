/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.roster;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.chat.ChatViewImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;

/**
 *
 * @author andrzej
 */
public class ContactSubscribeRequestDialog extends DialogBox {
        
        private final ClientFactory factory;
        
        public ContactSubscribeRequestDialog(ClientFactory factory_, final BareJID jid) {
                super(true);
                factory = factory_;
   
                setStyleName("dialogBox");
                setTitle(factory.i18n().subscriptionRequestContact());
                
                FlexTable table = new FlexTable();
                Label label = new Label(factory.i18n().subscriptionRequestContact());
                label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                table.setWidget(0, 0, label);
                
                label = new Label(factory.i18n().subscriptionRequestMessageContact() + " " + jid.toString());
                table.setWidget(1, 0, label);
                
                Button cancel = new Button(factory.baseI18n().cancel());
                cancel.setStyleName(factory.theme().style().button());
                cancel.addStyleName(factory.theme().style().left());
                table.setWidget(2, 0, cancel);
                cancel.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                try {
                                        factory.jaxmpp().getModulesManager().getModule(PresenceModule.class).unsubscribed(JID.jidInstance(jid));
                                        hide();
                                } catch (XMLException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                });                
                
                Button ok = new Button(factory.baseI18n().accept());
                ok.setStyleName(factory.theme().style().button());
                ok.addStyleName(factory.theme().style().buttonDefault());
                ok.addStyleName(factory.theme().style().right());
                table.setWidget(2, 1, ok);
                ok.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                try {
                                        factory.jaxmpp().getModulesManager().getModule(PresenceModule.class).subscribed(JID.jidInstance(jid));
                                        hide();
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
