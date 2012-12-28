/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.chat;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.*;
import tigase.sure.web.site.client.ClientFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class JoinRoomDialog extends DialogBox {
        
        private final ClientFactory factory;
        
        public JoinRoomDialog(ClientFactory factory_, String server, String room) {
                super(true);
                factory = factory_;
   
                setStyleName("dialogBox");
                setTitle(factory.i18n().joinRoom());
                
                FlexTable table = new FlexTable();
                
                Label label = new Label(factory.i18n().server());
                table.setWidget(0, 0, label);
                final TextBox serverBox = new TextBox();
                serverBox.setText(server);
                table.setWidget(0, 1, serverBox);
                
                label = new Label(factory.i18n().room());
                table.setWidget(1, 0, label);
                final TextBox roomBox = new TextBox();
                roomBox.setText(room);
                table.setWidget(1, 1, roomBox);

                label = new Label(factory.i18n().nick());
                table.setWidget(2, 0, label);
                final TextBox nickBox = new TextBox();
//                nickBox.setText(room);
                table.setWidget(2, 1, nickBox);

                label = new Label(factory.i18n().password());
                table.setWidget(3, 0, label);
                final PasswordTextBox passwordBox = new PasswordTextBox();
                table.setWidget(3, 1, passwordBox);
                
                Button cancel = new Button(factory.baseI18n().cancel());
                cancel.setStyleName(factory.theme().style().button());
                cancel.addStyleName(factory.theme().style().left());
                table.setWidget(4, 0, cancel);
                cancel.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                hide();
                        }
                });                
                
                Button join = new Button(factory.i18n().join());
                join.setStyleName(factory.theme().style().button());
                join.addStyleName(factory.theme().style().buttonDefault());
                join.addStyleName(factory.theme().style().right());
                table.setWidget(4, 1, join);
                join.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                try {
                                        String server = serverBox.getText();
                                        String room = roomBox.getText();
                                        String nick = nickBox.getText();
                                        String password = passwordBox.getText();

                                        factory.placeController().goTo(new ChatPlace());
                                        MucModule mucModule = factory.jaxmpp().getModulesManager().getModule(MucModule.class);
                                        if (password != null && !password.isEmpty()) {
                                                mucModule.join(room, server, nick, password);
                                        }
                                        else {
                                                mucModule.join(room, server, nick);                                                
                                        }
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
