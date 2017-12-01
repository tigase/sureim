/*
 * PubSubPublishViewImpl.java
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
package tigase.sure.web.site.client.pubsub;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubErrorCondition;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.widgets.Markdown;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.MessageDialog;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.events.ServerFeaturesChangedHandler;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 *
 * @author andrzej
 */
public class PubSubPublishViewImpl extends ResizeComposite implements PubSubPublishView {
        
        private final ClientFactory factory;
        
        private final FlexTable layout;
        
        private final TextArea content;
        private final AbsolutePanel preview;
        private final TextBox pubSubJidBox;
        private final TextBox nodeBox;
        private final TextBox idBox;
        private final TextBox titleBox;
        private final TextBox authorBox;
        
        private final ServerFeaturesChangedHandler serverFeaturesChangedHandler = new ServerFeaturesChangedHandler() {

                public void serverFeaturesChanged(Collection<DiscoveryModule.Identity> identities, Collection<String> features) {
                        BareJID jid = factory.jaxmpp().getSessionObject().getUserBareJid();
                        boolean hide = true;
                        if (jid != null) {
                                hide = !Dictionary.getDictionary("admins").keySet().contains(jid.toString());
                        }
                        factory.actionBarFactory().setVisible("publish", !hide);
                }
                
        };        
        
        public PubSubPublishViewImpl(ClientFactory factory_) {
                this.factory = factory_;
                
                AppView appView = new AppView(factory);
                appView.setActionBar(factory.actionBarFactory().createActionBar(this));
                
                factory.actionBarFactory().addLink("publish", "Publish", new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                factory.placeController().goTo(new PubSubPublishPlace());
                        }
                        
                });

                factory.eventBus().addHandler(ServerFeaturesChangedEvent.TYPE, serverFeaturesChangedHandler);
                
                layout = new FlexTable();
                layout.setStyleName("pubSubPublishForm");
                
                layout.setWidth("80%");
                layout.getElement().getStyle().setProperty("margin", "0px 10%");

                layout.setWidget(0, 0, new Label("PubSub JID"));
                pubSubJidBox = new TextBox();
                layout.setWidget(0, 1, pubSubJidBox);
                layout.setWidget(0, 2, new Label("e.g. pubsub@sure.im"));
                layout.setWidget(1, 0, new Label("Node"));
                nodeBox = new TextBox();
                layout.setWidget(1, 1, nodeBox);
                layout.setWidget(1, 2, new Label("e.g. news"));                
                layout.setWidget(2, 0, new Label("Item ID"));
                idBox = new TextBox();
                layout.setWidget(2, 1, idBox);
                layout.setWidget(2, 2, new Label("ID of pubsub entry (can be anything)"));                
                layout.setWidget(3, 0, new Label("Author"));
                authorBox = new TextBox();
                layout.setWidget(3, 1, authorBox);
                layout.setWidget(3, 2, new Label("Name of author"));
                layout.setWidget(4, 0, new Label("Title"));
                titleBox = new TextBox();
                layout.setWidget(4, 1, titleBox);
                layout.setWidget(4, 2, new Label("Title of article"));
                layout.setWidget(5, 0, new Label("Content"));
                
                content = new TextArea();
                content.setWidth("100%");
                content.setHeight("150px");
                
                layout.setWidget(5, 1, content);
                layout.setWidget(5, 2, new Label());
                
                preview = new AbsolutePanel();
                
                Style previewStyle = preview.getElement().getStyle();
//                previewStyle.setFontSize(0.7, Style.Unit.EM);
                previewStyle.setColor("#555");
                previewStyle.setWidth(100, Style.Unit.PCT);
                previewStyle.setProperty("minHeight", "250px");
                previewStyle.setDisplay(Style.Display.BLOCK);
                //previewStyle.setOverflow(Style.Overflow.AUTO);
                
                layout.setWidget(6, 0, new Label("Preview"));
                layout.setWidget(6, 1, preview);
                layout.setWidget(6, 2, new Label());
                
                appView.setCenter(new ScrollPanel(layout));
                
                content.addKeyUpHandler(new KeyUpHandler() {

                        public void onKeyUp(KeyUpEvent event) {
                                updatePreview();
                        }
                        
                });
                
                appView.getActionBar().addAction(factory.theme().navigationAccept(), new ClickHandler() {

                        public void onClick(ClickEvent event) {                
                                publish();
                        }
                        
                });
                appView.getActionBar().addAction(factory.theme().navigationCancel(), new ClickHandler() {
                   
                        public void onClick(ClickEvent event) {
                                reset();
                        }
                        
                });

                reset();
                
                initWidget(appView);
        }
        
        public void updatePreview() {
                String text = content.getText();
                text = Markdown.parse(text);
                preview.getElement().setInnerHTML(text);
        }
        
        public void publish() {
                try {
                        JID jid = JID.jidInstance(pubSubJidBox.getText());
                        
                        String node = nodeBox.getText();
                        
                        String id = idBox.getText();
                        if (id == null || id.isEmpty()) {
                                id = null;
                        }
                        
                        AtomEntry atomEntry = new AtomEntry();
                        
                        atomEntry.setId(id);
                        atomEntry.setTitle(this.titleBox.getText());
                        atomEntry.setAuthorName(this.authorBox.getText());
                        atomEntry.setContent(this.content.getText());
                        
                        Element payload = atomEntry.toElement();
                        
                        PubSubModule pubSubModule  = factory.jaxmpp().getModulesManager().getModule(PubSubModule.class);
                        pubSubModule.publishItem(jid.getBareJid(), node, id, payload, new PubSubAsyncCallback() {

                                @Override
                                protected void onEror(IQ response, ErrorCondition errorCondition, PubSubErrorCondition pubSubErrorCondition) {
                                        MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), 
                                                (pubSubErrorCondition == null) ? errorCondition.getElementName() : pubSubErrorCondition.name());
                                        dlg.show();
                                        dlg.center();                                        
                                }

                                public void onSuccess(Stanza responseStanza) throws JaxmppException {
                                        reset();
                                }

                                public void onTimeout() throws JaxmppException {
                                        MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), factory.i18n().requestTimedOut());
                                        dlg.show();
                                        dlg.center();
                                }
                                
                        });
                        
                        reset();
                } 
                catch (JaxmppException ex) {
                        Logger.getLogger("PubSubPublishViewImpl").log(Level.SEVERE, ex.getMessage(), ex);
                }
        }
        
        public void reset() {
                pubSubJidBox.setText(null);
                nodeBox.setText(null);
                idBox.setText(null);
                titleBox.setText(null);
                authorBox.setText(null);
                content.setText(Markdown.MARKDOWN_EXAMPLE);
                updatePreview();
        }
}