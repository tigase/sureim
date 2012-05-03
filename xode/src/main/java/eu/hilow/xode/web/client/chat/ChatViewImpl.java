/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.chat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import eu.hilow.gwt.base.client.ActionBar;
import eu.hilow.gwt.base.client.AppView;
import eu.hilow.gwt.base.client.roster.FlatRoster;
import eu.hilow.gwt.base.client.roster.FlatRoster.RosterItemClickHandler;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.settings.SettingsPlace;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.chat.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.chat.ChatSelector;
import tigase.jaxmpp.core.client.xmpp.modules.chat.DefaultChatSelector;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule.MessageEvent;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;

/**
 *
 * @author andrzej
 */
public class ChatViewImpl extends ResizeComposite implements ChatView {
        
        private final ClientFactory factory;
        private final AppView appView;
        
        private final TabLayoutPanel tabLayout;
        private final HashMap<Chat,ChatWidget> chats;
        private final ChatSelector chatSelector = new DefaultChatSelector();
        private final Listener<MessageEvent> listener;
        
        private final Widget closeChatAction;
        private final Widget settingsAction;
        
        public ChatViewImpl(ClientFactory factory_) {                
                this.factory = factory_;
                chats = new HashMap<Chat,ChatWidget>();
                
                appView = new AppView(factory);                
                appView.setActionBar(factory.actionBarFactory().createActionBar(this));
                
                closeChatAction = appView.getActionBar().addAction(factory.theme().navigationCancel(), new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                int idx = tabLayout.getSelectedIndex();
                                if (idx < 0) return;
                                ChatWidget chatWidget = (ChatWidget) tabLayout.getWidget(idx);
                                if (chatWidget != null) {
                                        MessageModule messageModule = factory.jaxmpp().getModulesManager().getModule(MessageModule.class);
                                        try {
                                                messageModule.getChatManager().close(chatWidget.getChat());
                                        } catch (JaxmppException ex) {
                                                Logger.getLogger("ChatViewImpl").warning("exception closing chat");
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

                        public void itemClicked(RosterItem ri) {
                                openChat(ri.getJid());
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
                
                listener = new Listener<MessageEvent>() {

                        public void handleEvent(MessageEvent be) throws JaxmppException {
                                handleMessageEvent(be);
                        }
                        
                };
                MessageModule messageModule = factory.jaxmpp().getModulesManager().getModule(MessageModule.class);
                messageModule.addListener(listener);
                
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
