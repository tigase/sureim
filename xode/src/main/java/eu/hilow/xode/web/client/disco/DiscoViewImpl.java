/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.disco;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import eu.hilow.gwt.base.client.AppView;
import eu.hilow.gwt.base.client.ResizablePanel;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.MessageDialog;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoInfoModule.Identity;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoItemsModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoItemsModule.Item;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class DiscoViewImpl extends ResizeComposite implements DiscoView, ProvidesResize {
        
        public static final String COMMANDS_FEATURE = "http://jabber.org/protocol/commands";
        private static final String DISCO_ITEMS_FEATURE = "http://jabber.org/protocol/disco#items";
        
        private final ClientFactory factory;
        private final FlowPanel layout;
        private final ListDataProvider<DiscoItem> provider;
        private final CellList<DiscoItem> list;
        
        private final DiscoItemsCallback discoItemsCallback;        
        private final DiscoItemsNamesComparator itemsNamesComparator = new DiscoItemsNamesComparator();

        private final SuggestBox jidBox;
        
        private final ResizablePanel buttonsPanel;
        private final HorizontalPanel buttons;
        private final Button executeCommand;
        private final Button browse;
        
        private final CommandsWidget.FinishHandler commandFinishHandler;
        private final CommandsWidget commandsWidget;
        
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
                                        discover(JID.jidInstance(jidBox.getText()), null);
                                }
                        }
                        
                };                
                jidBox.addKeyUpHandler(handler);
                
                this.discoItemsCallback = new DiscoItemsCallback();
                
                layout = new eu.hilow.gwt.base.client.widgets.FlowPanel();
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
                                }
                                else {
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
                
                commandsWidget = new CommandsWidget(factory, commandFinishHandler);
                layout.add(commandsWidget);
                commandsWidget.reset();
                
                appView.setLeftSidebar(new ScrollPanel(list), 25);
                appView.setCenter(layout);
                
                selectionModel.addSelectionChangeHandler(new Handler() {

                        public void onSelectionChange(SelectionChangeEvent event) {
                                DiscoItem item = selectionModel.getSelectedObject();
                                setButtonEnabled(executeCommand, item.hasFeature(COMMANDS_FEATURE));
                                setButtonEnabled(browse, item.hasFeature(DISCO_ITEMS_FEATURE));
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
                
                DiscoItemsModule module = factory.jaxmpp().getModulesManager().getModule(DiscoItemsModule.class);
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
        
        private class DiscoItemsCallback extends DiscoItemsModule.DiscoItemsAsyncCallback {

                @Override
                public void onInfoReceived(String attribute, ArrayList<Item> items) throws XMLException {
                        provider.getList().clear();
                        for (Item item : items) {
                                DiscoItem discoItem = new DiscoItem(item.getJid(), item.getNode(), item.getName());
                                addItem(discoItem);
                                
                                DiscoInfoModule module = factory.jaxmpp().getModulesManager().getModule(DiscoInfoModule.class);
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
        
        private class DiscoInfoCallback extends DiscoInfoModule.DiscoInfoAsyncCallback {

                private final DiscoItem item;
                
                public DiscoInfoCallback(DiscoItem item) {
                        super(item.getNode());
                        this.item = item;
                }
                
                @Override
                protected void onInfoReceived(String node, Collection<Identity> identities, Collection<String> features) throws XMLException {
                        item.setIdentities(identities);
                        item.setFeatures(features);
                        
                        updateItem(item);
                }

                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onTimeout() throws JaxmppException {
                        throw new UnsupportedOperationException("Not supported yet.");
                }
                
        }
        
        private class DiscoItemsNamesComparator implements Comparator<DiscoItem> {

                private String getName(DiscoItem t1) {
                        Identity identity1 = t1.getIdentities() != null ? t1.getIdentities().iterator().next() : null;
                        
                        String name1 = (identity1 != null && identity1.getName() != null) ? identity1.getName() : t1.getName();
                        if (name1 == null) {
                                name1 = t1.getJid().toString();
                        }                        
                        
                        return name1;
                }
                
                public int compare(DiscoItem t1, DiscoItem t2) {
                        if (t1 == null || t2 == null) {
                                return -1;
                        }
                        String n1 = getName(t1);
                        String n2 = getName(t2);
                        return n1.compareTo(n2);
                }
                        
        }
}
