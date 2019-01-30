/**
 * Sure.IM site - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.sure.web.site.client.chat;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import tigase.jaxmpp.core.client.*;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.ElementFactory;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;
import tigase.jaxmpp.core.client.xmpp.forms.XDataType;
import tigase.jaxmpp.core.client.xmpp.modules.chat.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule;
import tigase.jaxmpp.core.client.xmpp.modules.httpfileupload.HttpFileUploadModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Occupant;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.modules.muc.XMucUserElement;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.roster.FlatRoster;
import tigase.sure.web.base.client.roster.FlatRoster.RosterItemClickHandler;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.roster.ContactDialog;
import tigase.sure.web.site.client.roster.ContactSubscribeRequestDialog;
import tigase.sure.web.site.client.settings.SettingsPlace;
import tigase.sure.web.site.client.vcard.VCardDialog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class ChatViewImpl
		extends ResizeComposite
		implements ChatView {

	private final Widget addContactAction;
	private final AppView appView;
	private final Widget bookmarksAction;
	private final HashMap<Chat, ChatWidget> chats;
	private final Widget closeChatAction;
	private final ClientFactory factory;
	private final Widget joinRoomAction;
	private final MessageHandler messageListener;
	private final MucHandler mucListener;
	private final HashMap<BareJID, MucRoomWidget> rooms;
	private final Widget sendFileAction;
	private final Widget settingsAction;
	private final TabLayoutPanel tabLayout;

	public static boolean isVisible(Widget w) {
		if (w.isAttached() && w.isVisible()) {
			if (w.getParent() != null) {
				return isVisible(w.getParent());
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public ChatViewImpl(ClientFactory factory_) {
		this.factory = factory_;
		chats = new HashMap<Chat, ChatWidget>();
		rooms = new HashMap<BareJID, MucRoomWidget>();

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

		sendFileAction = appView.getActionBar().addAction(factory.theme().socialShare(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = tabLayout.getSelectedIndex();
				if (idx < 0) {
					return;
				}
				final Widget widget = (Widget) tabLayout.getWidget(idx);
				if (widget != null) {
					final DialogBox dlg = new SendFileDialog(factory, new SendFileDialog.Handler() {
						@Override
						public void fileUploaded(HttpFileUploadModule.Slot slot, SendFileDialog.JsFile file) {
							if (widget instanceof ChatWidget) {
								MessageModule messageModule = factory.jaxmpp()
										.getModulesManager()
										.getModule(MessageModule.class);
								try {
									Chat chat = ((ChatWidget) widget).getChat();
									Message msg = chat.createMessage(slot.getGetUri());
									HttpFileUploadModule.addOobLink(msg, slot.getGetUri());
									HttpFileUploadModule.addFileInfoFormToStanza(msg, file.getName(), file.getSize(),
																				 file.getType());
									messageModule.sendMessage(msg);
									((ChatWidget) widget).handleMessage(msg);
								} catch (JaxmppException ex) {
									Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
								}
							} else if (widget instanceof MucRoomWidget) {
								try {
									Room room = ((MucRoomWidget) widget).getRoom();
									Message msg = room.createMessage(slot.getGetUri());
									HttpFileUploadModule.addOobLink(msg, slot.getGetUri());
									HttpFileUploadModule.addFileInfoFormToStanza(msg, file.getName(), file.getSize(),
																				 file.getType());
									room.sendMessage(msg);
								} catch (XMLException ex) {
									Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
								} catch (JaxmppException ex) {
									Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						}
					});
					dlg.show();
					dlg.center();
				}
			}
		});

		closeChatAction = appView.getActionBar().addAction(factory.theme().navigationCancel(), new ClickHandler() {

			public void onClick(ClickEvent event) {
				int idx = tabLayout.getSelectedIndex();
				if (idx < 0) {
					return;
				}
				Widget widget = (Widget) tabLayout.getWidget(idx);
				if (widget != null) {
					if (widget instanceof ChatWidget) {
						MessageModule messageModule = factory.jaxmpp()
								.getModulesManager()
								.getModule(MessageModule.class);
						try {
							messageModule.getChatManager().close(((ChatWidget) widget).getChat());
						} catch (JaxmppException ex) {
							Logger.getLogger("ChatViewImpl").warning("exception closing chat");
						}
					} else if (widget instanceof MucRoomWidget) {
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

//               appView.getActionBar().addAction(factory.theme().collectionsCloud(), new ClickHandler() {
//
//                        public void onClick(ClickEvent event) {
////                                throw new UnsupportedOperationException("Not supported yet.");
//                                ((GwtSessionObject) factory.sessionObject()).test();
//                        }
//
//                });
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

		factory.jaxmpp()
				.getModulesManager()
				.getModule(PresenceModule.class)
				.addSubscribeRequestHandler(new PresenceModule.SubscribeRequestHandler() {

					@Override
					public void onSubscribeRequest(SessionObject sessionObject, Presence stanza, BareJID jid) {
						DialogBox dlg = new ContactSubscribeRequestDialog(factory, jid);
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
				if (idx < 0) {
					return;
				}
				//ChatWidget chat = (ChatWidget) tabLayout.getWidget(idx);
				Widget w = tabLayout.getTabWidget(idx);
				w.getElement().getStyle().clearColor();
			}

		});

		appView.setCenter(tabLayout);

		messageListener = new MessageHandler();

		factory.jaxmpp()
				.getEventBus()
				.addHandler(MessageModule.MessageReceivedHandler.MessageReceivedEvent.class, messageListener);
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MessageModule.ChatCreatedHandler.ChatCreatedEvent.class, messageListener);
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MessageModule.ChatClosedHandler.ChatClosedEvent.class, messageListener);

		mucListener = new MucHandler();
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MucModule.MucMessageReceivedHandler.MucMessageReceivedEvent.class, mucListener);
		factory.jaxmpp().getEventBus().addHandler(MucModule.YouJoinedHandler.YouJoinedEvent.class, mucListener);
		factory.jaxmpp().getEventBus().addHandler(MucModule.RoomClosedHandler.RoomClosedEvent.class, mucListener);
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MucModule.OccupantChangedNickHandler.OccupantChangedNickEvent.class, mucListener);
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MucModule.OccupantChangedPresenceHandler.OccupantChangedPresenceEvent.class, mucListener);
		factory.jaxmpp().getEventBus().addHandler(MucModule.OccupantComesHandler.OccupantComesEvent.class, mucListener);
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MucModule.OccupantLeavedHandler.OccupantLeavedEvent.class, mucListener);
		factory.jaxmpp()
				.getEventBus()
				.addHandler(MucModule.NewRoomCreatedHandler.NewRoomCreatedEvent.class, mucListener);

		initWidget(appView);

		updateActionBar();
	}

	public void openChat(BareJID bareJid) {
		JID jid = JID.jidInstance(bareJid);
		MessageModule messageModule = factory.jaxmpp().getModulesManager().getModule(MessageModule.class);
		Chat chat = messageModule.getChatManager().getChat(jid, null);
		if (chat == null) {
			try {
				chat = messageModule.createChat(jid);
			} catch (JaxmppException ex) {
				Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
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
						final Boolean autojoin = bookmark.getAttribute("autojoin") != null ? Boolean.parseBoolean(
								bookmark.getAttribute("autojoin")) : null;
						List<Element> nicksEl = bookmark.getChildren("nick");
						final String nick = nicksEl.isEmpty() ? null : nicksEl.get(0).getValue();
						List<Element> passwordEl = bookmark.getChildren("password");
						final String password = passwordEl.isEmpty() ? null : passwordEl.get(0).getValue();
						menu.addItem(name, new Command() {
							public void execute() {
								if (autojoin == null) {
									JoinRoomDialog joinRoomDlg = new JoinRoomDialog(factory, jid.getDomain(),
																					jid.getLocalpart());
									joinRoomDlg.show();
									joinRoomDlg.center();
								} else {
									MucModule mucModule = factory.jaxmpp()
											.getModulesManager()
											.getModule(MucModule.class);
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
					RosterModule.getRosterStore(factory.sessionObject()).remove(ri.getJid());
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
		sendFileAction.setVisible(tabLayout.getWidgetCount() > 0);
	}

	private class MessageHandler
			implements MessageModule.ChatClosedHandler,
					   MessageModule.ChatCreatedHandler,
					   MessageModule.MessageReceivedHandler {

		@Override
		public void onChatClosed(SessionObject sessionObject, Chat chat) {
			ChatWidget chatWidget = chats.get(chat);
			if (chatWidget != null) {
				chats.remove(chat);
				tabLayout.remove(chatWidget);
				updateActionBar();
			}
		}

		@Override
		public void onChatCreated(SessionObject sessionObject, Chat chat, Message message) {
			ChatWidget chatWidget = chats.get(chat);
			if (chatWidget == null) {
				chatWidget = new ChatWidget(factory, chat);
				chats.put(chat, chatWidget);
				tabLayout.add(chatWidget, chatWidget.getTitle());
				tabLayout.selectTab(chatWidget);
				updateActionBar();
			}
		}

		@Override
		public void onMessageReceived(SessionObject sessionObject, Chat chat, Message stanza) {
			ChatWidget chatWidget = chats.get(chat);
			if (chatWidget != null) {
				boolean visible = chatWidget.handleMessage(stanza);
				if (!visible) {
					if (tabLayout.getSelectedIndex() != tabLayout.getWidgetIndex(chatWidget)) {
						tabLayout.getTabWidget(chatWidget).getElement().getStyle().setColor("#DD4B39");
					}
					if (!isVisible(ChatViewImpl.this)) {
						factory.actionBarFactory().setWaitingEvents("chat", 1);
					}
				}
			}
		}

	}

	private class MucHandler
			implements MucModule.RoomClosedHandler,
					   MucModule.YouJoinedHandler,
					   MucModule.MucMessageReceivedHandler,
					   MucModule.OccupantChangedNickHandler,
					   MucModule.OccupantChangedPresenceHandler,
					   MucModule.OccupantComesHandler,
					   MucModule.OccupantLeavedHandler,
					   MucModule.NewRoomCreatedHandler {

		@Override
		public void onNewRoomCreated(SessionObject sessionObject, Room room) {

			Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, "New room created: " + room);

			configureRoom(room);
		}

		@Override
		public void onRoomClosed(SessionObject sessionObject, Presence presence, Room room) {
			MucRoomWidget roomWidget = rooms.get(room.getRoomJid());
			if (roomWidget != null) {
				rooms.remove(room.getRoomJid());
				tabLayout.remove(roomWidget);
				updateActionBar();
			}
		}

		@Override
		public void onYouJoined(SessionObject sessionObject, Room room, String asNickname) {
			refreshOccupantsList(room);
		}

		@Override
		public void onMucMessageReceived(SessionObject sessionObject, Message message, Room room, String nickname,
										 Date timestamp) {
			BareJID roomJid = room.getRoomJid();
			MucRoomWidget roomWidget = rooms.get(roomJid);
			Logger.getLogger(ChatViewImpl.class.getName())
					.log(Level.FINE,
						 "Received MUC message: " + message + ", room: " + room + ", nickname: " + nickname +
								 ", timestamp: " + timestamp + ", roomJid: " + roomJid + ", roomWidget: " + roomWidget);
			if (roomWidget != null) {
				boolean visible = roomWidget.handleMessage(message);
				if (!visible) {
					if (tabLayout.getSelectedIndex() != tabLayout.getWidgetIndex(roomWidget)) {
						tabLayout.getTabWidget(roomWidget).getElement().getStyle().setColor("#DD4B39");
					}
					if (!isVisible(ChatViewImpl.this)) {
						factory.actionBarFactory().setWaitingEvents("chat", 1);
					}
				}
			}
		}

		@Override
		public void onOccupantChangedNick(SessionObject sessionObject, Room room, Occupant occupant, String oldNickname,
										  String newNickname) {
			refreshOccupantsList(room);
		}

		@Override
		public void onOccupantChangedPresence(SessionObject sessionObject, Room room, Occupant occupant,
											  Presence newPresence) {
			refreshOccupantsList(room);
		}

		@Override
		public void onOccupantComes(SessionObject sessionObject, Room room, Occupant occupant, String nickname) {
			refreshOccupantsList(room);
		}

		@Override
		public void onOccupantLeaved(SessionObject sessionObject, Room room, Occupant occupant, Presence stanza,
									 XMucUserElement xUserElement) {
			refreshOccupantsList(room);
		}

		protected void configureRoom(Room room) {
			try {
				factory.jaxmpp().getModule(MucModule.class).getRoomConfiguration(room, new AsyncCallback() {

					@Override
					public void onError(Stanza responseStanza, XMPPException.ErrorCondition error)
							throws JaxmppException {
						Logger.getLogger(ChatViewImpl.class.getName())
								.log(Level.SEVERE, "Error while ocnfiguring room: " + error);
					}

					@Override
					public void onSuccess(Stanza responseStanza) throws JaxmppException {
						Element query = responseStanza.getChildrenNS("query", "http://jabber.org/protocol/muc#owner");
						query = ElementFactory.create(query);
						Element x = query.getChildrenNS("x", "jabber:x:data");
						JabberDataElement data = new JabberDataElement(x);
						data.setAttribute("type", XDataType.submit.name());

						IQ iq = IQ.createIQ();
						iq.setTo(responseStanza.getFrom());
						iq.setAttribute("type", "set");
						iq.addChild(query);

						factory.jaxmpp().send(iq, new AsyncCallback() {

							@Override
							public void onError(Stanza responseStanza, XMPPException.ErrorCondition error)
									throws JaxmppException {
								Logger.getLogger(ChatViewImpl.class.getName())
										.log(Level.SEVERE, "Error while ocnfiguring room: " + error);
							}

							@Override
							public void onSuccess(Stanza responseStanza) throws JaxmppException {
								Logger.getLogger(ChatViewImpl.class.getName())
										.log(Level.FINE, "Room configured correctly: " + responseStanza);
							}

							@Override
							public void onTimeout() throws JaxmppException {
								Logger.getLogger(ChatViewImpl.class.getName())
										.log(Level.SEVERE, "Timeout while ocnfiguring room: ");
							}
						});

						Logger.getLogger(ChatViewImpl.class.getName())
								.log(Level.FINE, "Room configured correctly: " + responseStanza);
					}

					@Override
					public void onTimeout() throws JaxmppException {
						Logger.getLogger(ChatViewImpl.class.getName())
								.log(Level.SEVERE, "Timeout while ocnfiguring room: ");
					}

				});

			} catch (JaxmppException ex) {
				Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private void refreshOccupantsList(Room room) {
			MucRoomWidget roomWidget = openRoom(room);
			if (roomWidget != null) {
				roomWidget.refreshOccupantsList();
			}
		}

	}
}
