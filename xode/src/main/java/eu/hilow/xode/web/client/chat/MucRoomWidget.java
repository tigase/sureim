/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.chat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import eu.hilow.xode.web.client.ClientFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;

/**
 *
 * @author andrzej
 */
public class MucRoomWidget extends ResizeComposite {
        
        private final ClientFactory factory;
        
        private final DockLayoutPanel layout;
        
        private final Room room;
        private final MucLogPanel log;
        private final TextArea input;
        
        public MucRoomWidget(ClientFactory factory_, Room room_) {
                this.factory = factory_;
                
                layout = new DockLayoutPanel(Unit.EM);
                
                room = room_;
                
                input = new TextArea();
                input.setWidth("99%");
                input.addKeyDownHandler(new KeyDownHandler() {

                        public void onKeyDown(KeyDownEvent event) {
                                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                                        input.setFocus(false);
                                        try {
                                                String text = input.getText();
                                                room.sendMessage(text);
                                        } catch (Exception ex) {
                                                Logger.getLogger("MucRoomWidget").log(Level.WARNING, "sending message exception", ex);
                                        }
                                        input.setFocus(true);
                                        event.stopPropagation();
                                }
                        }
                });
                input.addKeyUpHandler(new KeyUpHandler() {

                        public void onKeyUp(KeyUpEvent event) {
                                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                                        input.setFocus(false);
                                        try {
                                                // clearing input field after sending message
                                                input.setText(null);
                                        } catch (Exception ex) {
                                        }
                                        input.setFocus(true);
                                        event.stopPropagation();
                                }
                        }
                });
                layout.addSouth(input, 2.0);
                
                log = new MucLogPanel(factory, room);                
                layout.add(log);
                
                initWidget(layout);
        }
        
        public Room getRoom() {
                return room;
        }
        
        @Override
        public String getTitle() {
                return room.getRoomJid().toString();
        }       
        
        public boolean handleMessage(Message m) {
                log.appendMessage(m);
                
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
