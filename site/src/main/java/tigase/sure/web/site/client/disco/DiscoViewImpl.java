/*
 * DiscoViewImpl.java
 *
 * Tigase XMPP Web Client
 * Copyright (C) 2012-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
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
package tigase.sure.web.site.client.disco;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Item;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.MessageDialog;
import tigase.sure.web.site.client.chat.JoinRoomDialog;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class DiscoViewImpl
		extends ResizeComposite
		implements DiscoView, ProvidesResize {

	public static final String COMMANDS_FEATURE = "http://jabber.org/protocol/commands";
	private static final String DISCO_ITEMS_FEATURE = "http://jabber.org/protocol/disco#items";
	private static final String MUC_FEATURE = "http://jabber.org/protocol/muc";
	private final Button browse;
	private final HorizontalPanel buttons;
	private final ResizablePanel buttonsPanel;
	private final CommandsWidget.FinishHandler commandFinishHandler;
	private final CommandsWidget commandsWidget;
	private final DiscoItemsCallback discoItemsCallback;
	private final Button executeCommand;
	private final ClientFactory factory;
	private final DiscoItemsNamesComparator itemsNamesComparator = new DiscoItemsNamesComparator();
	private final SuggestBox jidBox;
	private final Button joinRoom;
	private final FlowPanel layout;
	private final CellList<DiscoItem> list;
	private final ListDataProvider<DiscoItem> provider;
	private JID jid;
	private String node = null;

	public DiscoViewImpl(ClientFactory factory_) {
		this.factory = factory_;

		AppView appView = new AppView(factory);
		appView.setActionBar(factory.actionBarFactory().createActionBar(this));

		factory.actionBarFactory().addLink("serviceDiscovery", factory.i18n().discovery(), new ClickHandler() {

			public void onClick(ClickEvent event) {
				factory.placeController().goTo(new DiscoPlace());
			}

		});

		jidBox = new SuggestBox();
		jidBox.getElement().getStyle().setFontWeight(Style.FontWeight.BOLDER);
		appView.getActionBar().setSearchBox(jidBox);
		KeyUpHandler handler = new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					String jidStr = jidBox.getText();
					JID jid = null;
					if (jidStr != null) {
						jidStr = jidStr.trim();
						if (!jidStr.isEmpty()) {
							jid = JID.jidInstance(jidStr);
						}
					}
					discover(jid, null);
				}
			}

		};
		jidBox.addKeyUpHandler(handler);

		this.discoItemsCallback = new DiscoItemsCallback();

		layout = new tigase.sure.web.base.client.widgets.FlowPanel();
		layout.getElement().getStyle().setWidth(100, Style.Unit.PCT);

		provider = new ListDataProvider<DiscoItem>();
		list = new CellList<DiscoItem>(new DiscoItemCell(factory));
		provider.addDataDisplay(list);

		buttonsPanel = new ResizablePanel();
		buttons = new HorizontalPanel();
		buttonsPanel.add(buttons);

		final SingleSelectionModel<DiscoItem> selectionModel = new SingleSelectionModel<DiscoItem>();
		list.setSelectionModel(selectionModel);

		browse = new Button(factory.i18n().browse());
		browse.setStyleName(factory.theme().style().button());
		browse.getElement().getStyle().setMargin(5, Style.Unit.PX);
		browse.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DiscoItem item = selectionModel.getSelectedObject();
				setButtonsEnabled(false);
				discover(item.getJid(), item.getNode());
			}
		});
		setButtonEnabled(browse, false);
		buttons.add(browse);

		joinRoom = new Button(factory.i18n().joinRoom());
		joinRoom.setStyleName(factory.theme().style().button());
		joinRoom.getElement().getStyle().setMargin(5, Style.Unit.PX);
		joinRoom.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DiscoItem item = selectionModel.getSelectedObject();
				JID jid = item.getJid();
				JoinRoomDialog dlg = new JoinRoomDialog(factory, jid.getDomain(), jid.getLocalpart());
				dlg.show();
				dlg.center();
			}
		});
		setButtonEnabled(joinRoom, false);
		buttons.add(joinRoom);

		executeCommand = new Button(factory.i18n().executeCommand());
		executeCommand.setStyleName(factory.theme().style().button());
		executeCommand.getElement().getStyle().setMargin(5, Style.Unit.PX);
		executeCommand.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DiscoItem item = selectionModel.getSelectedObject();
				boolean discoverCommands = true;
				Collection<Identity> identities = item.getIdentities();
				if (identities != null) {
					for (Identity id : identities) {
						if ("automation".equals(id.getCategory()) && "command-node".equals(id.getType())) {
							discoverCommands = false;
							break;
						}
					}
				}
				if (discoverCommands) {
					commandsWidget.updateCommandsList(item.getJid());
				} else {
					commandsWidget.executeCommand(item.getJid(), item.getNode());
				}
				commandsWidget.setVisible(true);
				buttonsPanel.setVisible(false);
				commandsWidget.onResize();
			}
		});
		setButtonEnabled(executeCommand, false);
		buttons.add(executeCommand);

		layout.add(buttonsPanel);

		commandFinishHandler = new CommandsWidget.FinishHandler() {

			public void finished() {
				DiscoItem item = selectionModel.getSelectedObject();
				setButtonEnabled(executeCommand, item != null && item.hasFeature(COMMANDS_FEATURE));
				setButtonEnabled(browse, item != null && item.hasFeature(DISCO_ITEMS_FEATURE));
				buttonsPanel.setVisible(true);
				commandsWidget.reset();
			}

			public void error(String msg) {
				MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), msg);
				dlg.show();
				dlg.center();

				finished();
			}

		};

		commandsWidget = new CommandsWidget(factory, false, commandFinishHandler);
		layout.add(commandsWidget);
		commandsWidget.reset();

		appView.setLeftSidebar(new ScrollPanel(list), 25);
		appView.setCenter(layout);

		selectionModel.addSelectionChangeHandler(new Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {
				DiscoItem item = selectionModel.getSelectedObject();
				setButtonEnabled(executeCommand, item.hasFeature(COMMANDS_FEATURE));
				setButtonEnabled(browse, item.hasFeature(DISCO_ITEMS_FEATURE) ||
						(item.hasFeature(MUC_FEATURE) && item.getJid() != null &&
								item.getJid().getLocalpart() == null));
				setButtonEnabled(joinRoom, item.hasFeature(MUC_FEATURE));
				buttonsPanel.setVisible(true);
				commandsWidget.reset();
			}

		});

		initWidget(appView);
	}

	public void refresh() {
		commandsWidget.reset();
		buttonsPanel.setVisible(true);

		if (jid == null) {
			jid = JID.jidInstance(factory.jaxmpp().getSessionObject().getUserBareJid().getDomain());
		}

		if (!jid.toString().equals(jidBox.getText())) {
			jidBox.setText(jid.toString());
		}

		DiscoveryModule module = factory.jaxmpp().getModulesManager().getModule(DiscoveryModule.class);
		try {
			module.getItems(jid, node, discoItemsCallback);
		} catch (XMLException ex) {
			Logger.getLogger(DiscoViewImpl.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JaxmppException ex) {
			Logger.getLogger(DiscoViewImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void discover(JID jid, String node) {
		this.jid = jid;
		this.node = node;
		refresh();
	}

	public void setButtonEnabled(Button button, boolean enable) {
		if (enable) {
			button.removeStyleName(factory.theme().style().buttonDisabled());
		} else {
			button.addStyleName(factory.theme().style().buttonDisabled());
		}
		button.setEnabled(enable);
	}

	public void setButtonsEnabled(boolean enable) {
		setButtonEnabled(browse, enable);
		setButtonEnabled(executeCommand, enable);
	}

	private void addItem(DiscoItem item) {
		provider.getList().remove(item);

		List<DiscoItem> items = new ArrayList<DiscoItem>();
		items.addAll(provider.getList());
		items.add(item);
		Collections.sort(items, itemsNamesComparator);

		int idx = items.indexOf(item);
		provider.getList().add(idx, item);
	}

	private void updateItem(DiscoItem item) {
		provider.getList().remove(item);
		addItem(item);
	}

	private class DiscoInfoCallback
			extends DiscoveryModule.DiscoInfoAsyncCallback {

		private final DiscoItem item;

		public DiscoInfoCallback(DiscoItem item) {
			super(item.getNode());
			this.item = item;
		}

		public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onTimeout() throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected void onInfoReceived(String node, Collection<Identity> identities, Collection<String> features)
				throws XMLException {
			item.setIdentities(identities);
			item.setFeatures(features);

			updateItem(item);
		}

	}

	private class DiscoItemsCallback
			extends DiscoveryModule.DiscoItemsAsyncCallback {

		@Override
		public void onInfoReceived(String attribute, ArrayList<Item> items) throws XMLException {
			provider.getList().clear();
			for (Item item : items) {
				DiscoItem discoItem = new DiscoItem(item.getJid(), item.getNode(), item.getName());
				addItem(discoItem);

				DiscoveryModule module = factory.jaxmpp().getModulesManager().getModule(DiscoveryModule.class);
				try {
					module.getInfo(item.getJid(), item.getNode(), new DiscoInfoCallback(discoItem));
				} catch (JaxmppException ex) {
					Logger.getLogger(DiscoViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			provider.flush();
		}

		public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onTimeout() throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class DiscoItemsNamesComparator
			implements Comparator<DiscoItem> {

		public int compare(DiscoItem t1, DiscoItem t2) {
			if (t1 == null || t2 == null) {
				return -1;
			}
			String n1 = getName(t1);
			String n2 = getName(t2);
			return n1.compareTo(n2);
		}

		private String getName(DiscoItem t1) {
			Identity identity1 = t1.getIdentities() != null ? t1.getIdentities().iterator().next() : null;

			String name1 = (identity1 != null && identity1.getName() != null) ? identity1.getName() : t1.getName();
			if (name1 == null) {
				name1 = t1.getJid().toString();
			}

			return name1;
		}

	}
}
