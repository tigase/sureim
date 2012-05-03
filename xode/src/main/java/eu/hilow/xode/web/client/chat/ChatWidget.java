/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.chat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.*;
import eu.hilow.xode.web.client.ClientFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.xmpp.modules.chat.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

/**
 *
 * @author andrzej
 */
public class ChatWidget extends ResizeComposite {

        private final ClientFactory factory;
        private final Chat chat;
        private final LogPanel log;
        private final TextArea input;

        public ChatWidget(ClientFactory factory_, Chat chat_) {
                super();

                this.factory = factory_;
                this.chat = chat_;

                DockLayoutPanel layout = new DockLayoutPanel(Unit.EM);

                input = new TextArea();
                input.setWidth("99%");
                input.addKeyDownHandler(new KeyDownHandler() {

                        public void onKeyDown(KeyDownEvent event) {
                                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                                        input.setFocus(false);
                                        try {
                                                String text = input.getText();
                                                Message msg = Message.create();
                                                msg.setType(StanzaType.chat);
                                                msg.setTo(chat.getJid());
                                                msg.setThread(chat.getThreadId());
                                                msg.setBody(text);

                                                input.setText(null);
                                                handleMessage(msg);
                                                factory.jaxmpp().send(msg);
                                        } catch (Exception ex) {
                                                Logger.getLogger("Chat").log(Level.WARNING, "sending message exception", ex);
                                        }
                                        input.setFocus(true);
                                }
                        }
                });
                layout.addSouth(input, 2.0);

                log = new LogPanel(factory, chat.getJid().toString(), getTitle());
                layout.add(log);

                initWidget(layout);
        }

        public Chat getChat() {
                return chat;
        }
        
        @Override
        public String getTitle() {
                RosterItem ri = chat.getSessionObject().getRoster().get(chat.getJid().getBareJid());
                return ri != null ? ri.getName() : chat.getJid().toString();
        }

        public boolean handleMessage(Message message) {
                log.appendMessage(message);
                return isVisible(this);
        }
        
        public static boolean isVisible(Widget w) {
                if (w.isAttached() && w.isVisible()) {
                        if (w.getParent() != null) {
                                return isVisible(w.getParent());
                        }
                        else {
                                return true;
                        }
                }
                else {
                        return false;
                }
        }
}
