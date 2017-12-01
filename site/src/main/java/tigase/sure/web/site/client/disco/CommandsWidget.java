/*
 * CommandsWidget.java
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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.site.client.ClientFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.Action;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.AdHocCommansModule;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.AdHocCommansModule.AdHocCommansAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.State;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Item;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class CommandsWidget extends ResizeComposite  {
        
        public interface FinishHandler {
                void finished();
                void error(String msg);
        };
        
		public interface ProgressHandler {
			void started();
			void finished();
		}
		
        private final ClientFactory factory;
        
        private final DockLayoutPanel layout;//FlowPanel layout;
		protected final HorizontalPanel comboPanel;
        private final ListBox commandsCombo;
        
        protected final Form form;
        HorizontalPanel buttons;
        
        private final FinishHandler finishHandler;
        private final CommandsItemsCallback commandsItemsCallback;
        private final CommandExecCallback commandExecCallback;
        private JID jid;
        private final ScrollPanel scroll;
        private ProgressHandler progressHandler;
		
        public CommandsWidget(ClientFactory factory_, boolean withoutCommandsCombo, FinishHandler finishHandler) {
                this.factory = factory_;
                this.finishHandler = finishHandler;
        
                //layout = new eu.hilow.gwt.base.client.widgets.FlowPanel();                
                layout = new DockLayoutPanel(Unit.EM);
                layout.setWidth("90%");
                layout.setHeight("100%");
                layout.getElement().getStyle().setMargin(0, Unit.PCT);
                layout.getElement().getStyle().setLeft(10, Unit.PCT);
                
                AbsolutePanel panel = new AbsolutePanel();
                comboPanel = new HorizontalPanel();
                Label commandsLabel = new Label(factory.i18n().availableCommands()+":");
                comboPanel.add(commandsLabel);                
                commandsLabel.getElement().getStyle().setFontSize(1.1, Unit.EM);
                commandsLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);                
                commandsLabel.getElement().getStyle().setPaddingTop(8, Unit.PX);
                commandsCombo = new ListBox();
                commandsCombo.getElement().getStyle().setPadding(5, Unit.PX);
                comboPanel.add(commandsCombo);
                commandsCombo.addChangeHandler(new ChangeHandler() {

                        public void onChange(ChangeEvent event) {
                                int idx = commandsCombo.getSelectedIndex();
                                String value = commandsCombo.getValue(idx);
                                if (value != null) {
                                        commandSelected(jid, value);       
                                }
                        }
                        
                });
                
                commandsItemsCallback = new CommandsItemsCallback();
                commandExecCallback = new CommandExecCallback();
                panel.add(comboPanel);
				if (withoutCommandsCombo) {
					layout.addNorth(panel, 0);
					panel.setVisible(false);
				} else
					layout.addNorth(panel, 3);
                
                buttons = new HorizontalPanel();
                
				if (withoutCommandsCombo)
					layout.addSouth(buttons, 4);
				else
					layout.addSouth(buttons, 3);
                
                form = new Form(factory);
                scroll = new ScrollPanel(form);
                layout.add(scroll);

                initWidget(layout);
        }
        
		public void setProgressHandler(ProgressHandler progressHandler) {
			this.progressHandler = progressHandler;
		}
		
        public void updateCommandsList(JID jid) {
                this.jid = jid;                
                commandsCombo.clear();
                commandsCombo.setVisible(true);
                form.reset();
                
                DiscoveryModule module = factory.jaxmpp().getModulesManager().getModule(DiscoveryModule.class);
                try {
                        module.getItems(jid, DiscoViewImpl.COMMANDS_FEATURE, commandsItemsCallback);
                } catch (XMLException ex) {
                        Logger.getLogger(DiscoViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JaxmppException ex) {
                        Logger.getLogger(DiscoViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                }                
        }
        
        public void executeCommand(JID jid, String node) {
                this.jid = jid;                
                commandsCombo.clear();
                form.reset();

                commandsCombo.setVisible(false);
                commandSelected(jid, node);
        }
        
        public void reset() {                
                setVisible(false);
                form.reset();
        }
        
        private void commandSelected(JID jid, String node) {
                AdHocCommansModule adHocCommands = factory.jaxmpp().getModulesManager().getModule(AdHocCommansModule.class);
                try {
                        adHocCommands.execute(jid, node, Action.execute, null, commandExecCallback);
						if (progressHandler != null) progressHandler.started();
                } catch (JaxmppException ex) {
                        Logger.getLogger(CommandsWidget.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        private class CommandsItemsCallback extends DiscoveryModule.DiscoItemsAsyncCallback {

                private final DiscoItemsComparator discoItemsComparator = new DiscoItemsComparator();
                
                @Override
                public void onInfoReceived(String attribute, ArrayList<DiscoveryModule.Item> items) throws XMLException {
                        commandsCombo.addItem("", (String) null);
                        Collections.sort(items, discoItemsComparator);
                        for (DiscoveryModule.Item item : items) {
                                commandsCombo.addItem(item.getName(), item.getNode());
                        }
                }

                public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
                        finishHandler.error(error.getElementName());
                }

                public void onTimeout() throws JaxmppException {
                        finishHandler.error(factory.i18n().requestTimedOut());
                }

                private class DiscoItemsComparator implements Comparator<Item> {

                        public DiscoItemsComparator() {
                        }

                        public int compare(Item t1, Item t2) {
                                if (t1 == null || t2 == null) {
                                        return -1;
                                }
                                
                                String name1 = t1.getName();
                                if (name1 == null) name1 = t1.getNode();
                                String name2 = t2.getName();
                                if (name2 == null) name2 = t2.getNode();
                                
                                return name1.compareTo(name2);
                        }
                }
                
        }
        
        private class CommandExecCallback extends AdHocCommansAsyncCallback {

                @Override
                protected void onResponseReceived(String sessionid, final String node, State status, JabberDataElement data) throws JaxmppException {
                        form.reset();
                        buttons.clear();
                        if (data != null) {
                                form.setData(data);
                                
                                if (status == State.executing) {
                                        Button cancel = new Button(factory.baseI18n().cancel());
                                        cancel.setStyleName(factory.theme().style().button());
                                        buttons.add(cancel);

                                        cancel.addClickHandler(new ClickHandler() {

                                                public void onClick(ClickEvent event) {
                                                        AdHocCommansModule adHocCommands = factory.jaxmpp().getModulesManager().getModule(AdHocCommansModule.class);
                                                        try {
                                                                adHocCommands.execute(jid, node, Action.cancel, null, commandExecCallback);
                                                                finishHandler.finished();
																if (progressHandler != null) progressHandler.started();
                                                        } catch (JaxmppException ex) {
                                                                Logger.getLogger(CommandsWidget.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                }
                                        });

                                        Button submit = new Button(factory.baseI18n().confirm());
                                        submit.setStyleName(factory.theme().style().button());
                                        submit.addStyleName(factory.theme().style().buttonDefault());
                                        buttons.add(submit);

                                        submit.addClickHandler(new ClickHandler() {
                                                public void onClick(ClickEvent event) {
                                                        AdHocCommansModule adHocCommands = factory.jaxmpp().getModulesManager().getModule(AdHocCommansModule.class);
                                                        try {
                                                                JabberDataElement data = form.getData();
                                                                adHocCommands.execute(jid, node, Action.execute, data, commandExecCallback);
																if (progressHandler != null) progressHandler.started();
                                                        } catch (JaxmppException ex) {
                                                                Logger.getLogger(CommandsWidget.class.getName()).log(Level.SEVERE, null, ex);
                                                        }
                                                }                                        
                                        });                        
                                }
                                else if (status == State.completed) {
                                        Button close = new Button(factory.baseI18n().close());
                                        close.setStyleName(factory.theme().style().button());
                                        close.addStyleName(factory.theme().style().buttonDefault());
                                        buttons.add(close);
                                        
                                        close.addClickHandler(new ClickHandler() {
                                                public void onClick(ClickEvent event) {
                                                        finishHandler.finished();
														if (progressHandler != null) progressHandler.finished();
                                                }                                                
                                        });
                                }
                        }
						if (progressHandler != null) progressHandler.finished();
                }

                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                        finishHandler.error(error.getElementName());
						if (progressHandler != null) progressHandler.finished();
                }

                public void onTimeout() throws JaxmppException {
                        finishHandler.error(factory.i18n().requestTimedOut());
						if (progressHandler != null) progressHandler.finished();
                }
                
        }
}
