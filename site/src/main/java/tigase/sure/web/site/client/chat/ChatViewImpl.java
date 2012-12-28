/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.chat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import tigase.sure.web.base.client.ActionBar;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.roster.FlatRoster;
import tigase.sure.web.base.client.roster.FlatRoster.RosterItemClickHandler;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.roster.ContactDialog;
import tigase.sure.web.site.client.roster.ContactSubscribeRequestDialog;
import tigase.sure.web.site.client.settings.SettingsPlace;
import tigase.sure.web.site.client.vcard.VCardDialog;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.chat.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.chat.ChatSelector;
import tigase.jaxmpp.core.client.xmpp.modules.chat.DefaultChatSelector;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule.MessageEvent;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule.MucEvent;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.PresenceEvent;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class ChatViewImpl extends ResizeComposite implements ChatView {
        
        private final ClientFactory factory;
        private final AppView appView;
        
        private final TabLayoutPanel tabLayout;
        private final HashMap<Chat,ChatWidget> chats;
        private final HashMap<BareJID, MucRoomWidget> rooms;
        private final ChatSelector chatSelector = new DefaultChatSelector();
        private final Listener<MessageEvent> messageListener;
        private final Listener<MucEvent> mucListener;
        
        private final Widget addContactAction;
        private final Widget joinRoomAction;
        private final Widget closeChatAction;
        private final Widget settingsAction;
        private final Widget bookmarksAction;
        
        public ChatViewImpl(ClientFactory factory_) {                
                this.factory = factory_;
                chats = new HashMap<Chat,ChatWidget>();
                rooms = new HashMap<BareJID,MucRoomWidget>();
                
                appView = new AppView(factory);                
                appView.setActionBar(factory.actionBarFactory().createActionBar(this));

                addContactAction = appView.getActionBar().addAction(factory.theme().addPerson(), new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                final DialogBox dlg = new ContactDialog(factory, null);                
                
                                dlg.show();
                                dlg.center();
                        }
                        
                });

                joinRoomAction = appView.getActionBar().addAction(factory.theme().muc(), new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                JoinRoomDialog joinRoomDlg = new JoinRoomDialog(factory, null, null);
                                joinRoomDlg.show();
                                joinRoomDlg.center();
                        }
                        
                });
                
                bookmarksAction = appView.getActionBar().addAction(factory.theme().bookmarks(), new ClickHandler() {

                        public void onClick(ClickEvent event) {
//                                throw new UnsupportedOperationException("Not supported yet.");
                                showBookmarksMenu();
                        }
                        
                });
                
                closeChatAction = appView.getActionBar().addAction(factory.theme().navigationCancel(), new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                int idx = tabLayout.getSelectedIndex();
                                if (idx < 0) return;
                                Widget widget = (Widget) tabLayout.getWidget(idx);
                                if (widget != null) {
                                        if (widget instanceof ChatWidget) {
                                                MessageModule messageModule = factory.jaxmpp().getModulesManager().getModule(MessageModule.class);
                                                try {
                                                        messageModule.getChatManager().close(((ChatWidget) widget).getChat());
                                                } catch (JaxmppException ex) {
                                                        Logger.getLogger("ChatViewImpl").warning("exception closing chat");
                                                }
                                        }
                                        else if (widget instanceof MucRoomWidget) {
                                                MucModule mucModule = factory.jaxmpp().getModulesManager().getModule(MucModule.class);
                                                try {
                                                        Room room = ((MucRoomWidget) widget).getRoom();
                                                        mucModule.leave(room);
                                                        rooms.remove(room.getRoomJid());
                                                        tabLayout.remove(widget);
                                                        updateActionBar();
                                                } catch (XMLException ex) {
                                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (JaxmppException ex) {
                                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                        }
                                }
                        }
                        
                });
                
                factory.actionBarFactory().addLink("chat", "Chat", new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                factory.placeController().goTo(new ChatPlace());
                                factory.actionBarFactory().setWaitingEvents("chat", 0);
                        }
                        
                });
                
                
                settingsAction = appView.getActionBar().addAction(factory.theme().settings(), new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                factory.placeController().goTo(new SettingsPlace());
                        }
                        
                });
//                appView.getActionBar().addLink("Advanced", new ClickHandler() {
//
//                        public void onClick(ClickEvent event) {
//                                throw new UnsupportedOperationException("Not supported yet.");
//                        }
//                        
//                });

                FlatRoster roster = new FlatRoster(factory);
//                roster.sinkEvents(Event.ONCONTEXTMENU);
//                roster.sinkEvents(Event.ONMOUSEUP);
                roster.addClickHandler("click", new RosterItemClickHandler() {
                        public void itemClicked(RosterItem ri, int left, int top) {
                                openChat(ri.getJid());
                        }                        
                });
                
                roster.addClickHandler("contextmenu", new RosterItemClickHandler() {
                        public void itemClicked(RosterItem ri, int left, int top) {
                                showContactContextMenu(ri, left, top);
                        }                        
                });
                
                factory.jaxmpp().getModulesManager().getModule(PresenceModule.class).addListener(PresenceModule.SubscribeRequest, new Listener<PresenceEvent>() {

                        public void handleEvent(PresenceEvent be) throws JaxmppException {
                                DialogBox dlg = new ContactSubscribeRequestDialog(factory, be.getJid().getBareJid());
                                dlg.show();
                                dlg.center();
                        }
                        
                });
                
                appView.setLeftSidebar(roster);
//                appView.sinkEvents(Event.ONCONTEXTMENU);
//                appView.sinkEvents(Event.ONMOUSEUP);
                
//                appView.setCenter(new Label("Center panel"));
                
                tabLayout = new TabLayoutPanel(2.8, Style.Unit.EM);
                
                tabLayout.addStyleName("tabLayoutPanel");
                
                tabLayout.addSelectionHandler(new SelectionHandler() {

                        public void onSelection(SelectionEvent event) {
                                int idx = tabLayout.getSelectedIndex();
                                if (idx < 0) 
                                        return;
                                //ChatWidget chat = (ChatWidget) tabLayout.getWidget(idx);
                                Widget w = tabLayout.getTabWidget(idx);
                                w.getElement().getStyle().clearColor();
                        }
                        
                });
                
                appView.setCenter(tabLayout);
                
                messageListener = new Listener<MessageEvent>() {

                        public void handleEvent(MessageEvent be) throws JaxmppException {
                                if (be.getChat() != null) {
                                        handleMessageEvent(be);                                
                                }
                                else {
                                        handleMucMessageEvent(be);
                                }
                        }
                        
                };
                
                MessageModule messageModule = factory.jaxmpp().getModulesManager().getModule(MessageModule.class);
                messageModule.addListener(messageListener);

                mucListener = new Listener<MucModule.MucEvent>() {

                        public void handleEvent(MucEvent be) throws JaxmppException {
                                handleMucEvent(be);
                        }
                        
                };
                MucModule mucModule = factory.jaxmpp().getModulesManager().getModule(MucModule.class);
                mucModule.addListener(mucListener);
                
                initWidget(appView);
                
                updateActionBar();
        }
        
        public void openChat(BareJID bareJid) {
                JID jid = JID.jidInstance(bareJid);
                MessageModule messageModule = factory.jaxmpp().getModulesManager().getModule(MessageModule.class);
                Chat chat = chatSelector.getChat(messageModule.getChats(), jid, null);
                if (chat == null) {
                        try {
                                chat = messageModule.createChat(jid);
                        } catch (JaxmppException ex) {
                                Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
                else {
                        ChatWidget chatWidget = chats.get(chat);
                        tabLayout.selectTab(chatWidget);
                }
        }

        protected MucRoomWidget openRoom(Room room) {
                BareJID roomJID = room.getRoomJid();
                MucRoomWidget roomWidget = rooms.get(roomJID);
                if (roomWidget == null) {
                        roomWidget = new MucRoomWidget(factory, room);
                        rooms.put(roomJID, roomWidget);
                        tabLayout.add(roomWidget, roomWidget.getTitle());
                        tabLayout.selectTab(roomWidget);
                        updateActionBar();
                }
                
                return roomWidget;
        }
        
        protected void handleMessageEvent(MessageEvent be) {
                if (be.getType() == MessageModule.ChatCreated) {
                        ChatWidget chatWidget = chats.get(be.getChat());
                        if (chatWidget == null) {
                                chatWidget = new ChatWidget(factory, be.getChat());
                                chats.put(be.getChat(), chatWidget);
                                tabLayout.add(chatWidget, chatWidget.getTitle());
                                tabLayout.selectTab(chatWidget);
                                updateActionBar();                                
                        }
                }
                else if (be.getType() == MessageModule.ChatClosed) {
                        ChatWidget chatWidget = chats.get(be.getChat());
                        if (chatWidget != null) {
                                chats.remove(be.getChat());
                                tabLayout.remove(chatWidget);
                                updateActionBar();
                        }                        
                }
                else if (be.getType() == MessageModule.MessageReceived) {
                        ChatWidget chatWidget = chats.get(be.getChat());
                        if (chatWidget != null) {
                                boolean visible = chatWidget.handleMessage(be.getMessage());
                                if (!visible) {
                                        if (tabLayout.getSelectedIndex() != tabLayout.getWidgetIndex(chatWidget)) {
                                                tabLayout.getTabWidget(chatWidget).getElement().getStyle().setColor("#DD4B39");
                                        }
                                        if (!isVisible(this)) {
                                                factory.actionBarFactory().setWaitingEvents("chat", 1);
                                        }
                                }
                        }
                }
        }
        
        protected void handleMucEvent(MucEvent be) {
                if (be.getType() == MucModule.MucMessageReceived) {
                        try {              
                                openRoom(be.getRoom());
                                handleMucMessage(be.getMessage());
                        }
                        catch (JaxmppException ex) {
                                Logger.getLogger("ChatViewImpl").log(Level.SEVERE, ex.getMessage(), ex);
                        }
                }
                else if (be.getType() == MucModule.YouJoined) {
                        Room room = be.getRoom();
                        MucRoomWidget roomWidget = openRoom(room);
                        if (roomWidget != null) {
                                roomWidget.handleMucEvent(be);
                        }
                }       
                else if (be.getType() == MucModule.RoomClosed) {
                        Room room = be.getRoom();
                        MucRoomWidget roomWidget = rooms.get(room.getRoomJid());
                        if (roomWidget != null) {
                                rooms.remove(room.getRoomJid());
                                tabLayout.remove(roomWidget);
                                updateActionBar();
                        }
                }
                else {
                        Room room = be.getRoom();
                        MucRoomWidget roomWidget = rooms.get(room.getRoomJid());
                        if (roomWidget != null) {
                                roomWidget.handleMucEvent(be);
                        }
                }
        }
        
        protected void handleMucMessageEvent(MessageEvent be) throws XMLException {
                if (be.getType() == MessageModule.MessageReceived) {
                        Message m = (Message) be.getMessage();
                        handleMucMessage(m);
                }
        }
        
        protected void handleMucMessage(Message m) throws XMLException {
                BareJID roomJid = m.getFrom().getBareJid();
                MucRoomWidget roomWidget = rooms.get(roomJid);
                if (roomWidget != null) {
                        boolean visible = roomWidget.handleMessage(m);
                        if (!visible) {
                                if (tabLayout.getSelectedIndex() != tabLayout.getWidgetIndex(roomWidget)) {
                                        tabLayout.getTabWidget(roomWidget).getElement().getStyle().setColor("#DD4B39");
                                }
                                if (!isVisible(this)) {
                                        factory.actionBarFactory().setWaitingEvents("chat", 1);
                                }
                        }
                }
        }
        
        protected void showBookmarksMenu() {
                final PopupPanel popup = new PopupPanel(true);
                popup.setStyleName("actionBarActionMenu");
                MenuBar menu = new MenuBar(true);

                List<Element> bookmarks = factory.bookmarksManager().getBookmarks();
                if (bookmarks != null) {
                        for (Element bookmark : bookmarks) {
                                try {
                                        if ("conference".equals(bookmark.getName())) {
                                                final String name = bookmark.getAttribute("name");
                                                final JID jid = JID.jidInstance(bookmark.getAttribute("jid"));
                                                final Boolean autojoin = bookmark.getAttribute("autojoin") != null ? Boolean.parseBoolean(bookmark.getAttribute("autojoin")) : null;
                                                List<Element> nicksEl = bookmark.getChildren("nick");
                                                final String nick = nicksEl.isEmpty() ? null : nicksEl.get(0).getValue();
                                                List<Element> passwordEl = bookmark.getChildren("password");
                                                final String password = passwordEl.isEmpty() ? null : passwordEl.get(0).getValue();                                                
                                                menu.addItem(name, new Command() {
                                                        public void execute() {
                                                                if (autojoin == null) {
                                                                        JoinRoomDialog joinRoomDlg = new JoinRoomDialog(factory, jid.getDomain(), jid.getLocalpart());
                                                                        joinRoomDlg.show();
                                                                        joinRoomDlg.center();                                                                        
                                                                }
                                                                else {
                                                                        MucModule mucModule = factory.jaxmpp().getModulesManager().getModule(MucModule.class);
                                                                        try {
                                                                                mucModule.join(jid.getLocalpart(), jid.getDomain(), nick, password);
                                                                        } catch (XMLException ex) {
                                                                                Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                                                        } catch (JaxmppException ex) {
                                                                                Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                                                        }
                                                                }
                                                                popup.hide();
                                                        }                                                        
                                                });
                                        }
                                } catch (XMLException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                }
                
                menu.setStyleName("");
                popup.add(menu);
                int left = bookmarksAction.getAbsoluteLeft();
                popup.setPopupPosition(left, bookmarksAction.getAbsoluteTop() + bookmarksAction.getOffsetHeight());
                popup.show();
                if (popup.getOffsetWidth() + left > Window.getClientWidth()) {
                        left = (bookmarksAction.getAbsoluteLeft() - popup.getOffsetWidth()) + bookmarksAction.getOffsetWidth();
                        popup.setPopupPosition(left, bookmarksAction.getAbsoluteTop() + bookmarksAction.getOffsetHeight());
                }
        }
        
        protected void showContactContextMenu(final RosterItem ri, int left, int top) {
                final PopupPanel popup = new PopupPanel(true);
                popup.setStyleName(factory.theme().style().popupPanel());
                MenuBar menu = new MenuBar(true);
                
                menu.addItem(factory.i18n().chat(), new Command() {
                        public void execute() {
                                popup.hide();
                                openChat(ri.getJid());
                        }                        
                });
                
                menu.addItem(factory.baseI18n().modify(), new Command() {
                        public void execute() {
                                popup.hide();
                                ContactDialog dlg = new ContactDialog(factory, ri.getJid());
                                dlg.show();
                                dlg.center();
                        }                        
                });
                
                menu.addItem(factory.baseI18n().delete(), new Command() {
                        public void execute() {
                                try {
                                        popup.hide();
                                        factory.jaxmpp().getRoster().remove(ri.getJid());
                                } catch (XMLException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }                        
                });
                
                menu.addItem(factory.baseI18n().info(), new Command() {

                        public void execute() {
                                popup.hide();
                                VCardDialog dlg = new VCardDialog(factory, ri.getJid());
                                dlg.show();
                                dlg.center();
                        }
                        
                });

                menu.setStyleName("");
                popup.add(menu);
                popup.setPopupPosition(left, top);
                popup.show();
//                final DialogBox dlg = new ContactDialog(factory, ri.getJid());                
//                
//                dlg.show();
//                dlg.center();                
        }
        
        protected void updateActionBar() {
                closeChatAction.setVisible(tabLayout.getWidgetCount() > 0);
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
