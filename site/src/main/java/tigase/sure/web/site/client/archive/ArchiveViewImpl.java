/*
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
package tigase.sure.web.site.client.archive;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.MessageArchivingModule;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.MessageArchivingModule.SettingsAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.events.ServerFeaturesChangedHandler;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class ArchiveViewImpl
		extends ResizeComposite
		implements ArchiveView {

	private static final Logger log = Logger.getLogger("ArchiveViewImpl");
	private static final int MILIS_PER_DAY = 1000 * 60 * 60 * 24;
	private final AppView appView;
	private final Widget archiveDisabled;
	private final Widget archiveEnabled;
	private final Controller controller;
	private final ClientFactory factory;
	private final SettingsAsyncCallback settingsAsyncCallback = new SettingsAsyncCallback() {

		@Override
		public void onSuccess(boolean autoArchive) {
			archiveEnabled.setVisible(autoArchive);
			archiveDisabled.setVisible(!autoArchive);
		}

		public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
			archiveEnabled.setVisible(false);
			archiveDisabled.setVisible(false);
		}

		public void onTimeout() throws JaxmppException {
			archiveEnabled.setVisible(false);
			archiveDisabled.setVisible(false);
		}

	};

	private final ServerFeaturesChangedHandler serverFeaturesChangedHandler = new ServerFeaturesChangedHandler() {

		@Override
		public void serverFeaturesChanged(Collection<DiscoveryModule.Identity> identities,
										  Collection<String> features) {
			boolean ok = (features != null && features.contains("urn:xmpp:archive:auto"));
			factory.actionBarFactory().setVisible("archive", ok);
			MessageArchivingModule module = factory.jaxmpp()
					.getModulesManager()
					.getModule(MessageArchivingModule.class);
			if (ok) {
				try {
					module.getSettings(settingsAsyncCallback);
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, null, ex);
				}
			} else {
				archiveEnabled.setVisible(false);
				archiveDisabled.setVisible(false);
			}
		}

	};

	public ArchiveViewImpl(ClientFactory factory_) {
		this.factory = factory_;

		appView = new AppView(factory);
		appView.setActionBar(factory.actionBarFactory().createActionBar(this));

		factory.actionBarFactory().addLink("archive", "History", new ClickHandler() {

			public void onClick(ClickEvent event) {
				factory.placeController().goTo(new ArchivePlace());
			}

		});

		factory.eventBus().addHandler(ServerFeaturesChangedEvent.TYPE, serverFeaturesChangedHandler);

		controller = new Controller(factory);

		ContactList contactList = new ContactList(factory);
		contactList.addSelectionChangeHandler(new ContactList.SelectionHandler() {
			public void itemSelected(BareJID jid) {
				contactSelected(jid);
			}
		});

		appView.setLeftSidebar(contactList);

		CalendarWidget calendar = new CalendarWidget(factory, controller);
		appView.setRightSidebar(calendar, 17);
		Messages messages = new Messages(factory, controller);
		appView.setCenter(messages);

		archiveEnabled = appView.getActionBar().addAction(factory.theme().microfoneOn(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MessageArchivingModule module = factory.jaxmpp()
						.getModulesManager()
						.getModule(MessageArchivingModule.class);
				try {
					module.setAutoArchive(false, null);
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, null, ex);
				}
				archiveEnabled.setVisible(false);
				archiveDisabled.setVisible(true);
			}

		});
		((Image) archiveEnabled).setTitle("Disable auto archiving");

		archiveDisabled = appView.getActionBar().addAction(factory.theme().microfoneOff(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MessageArchivingModule module = factory.jaxmpp()
						.getModulesManager()
						.getModule(MessageArchivingModule.class);
				try {
					module.setAutoArchive(true, null);
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, null, ex);
				}
				archiveEnabled.setVisible(true);
				archiveDisabled.setVisible(false);
			}

		});
		((Image) archiveDisabled).setTitle("Enable auto archiving");

		archiveEnabled.setVisible(false);
		archiveDisabled.setVisible(false);

		initWidget(appView);
	}

	private void contactSelected(BareJID jid) {
		Date from = new Date();
		from.setDate(1);
		from.setTime(((long) (from.getTime() / MILIS_PER_DAY)) * MILIS_PER_DAY);
		controller.setJid(JID.jidInstance(jid));
		controller.listCollections(from, null);
/*                MessageArchivingModule archiveModule = factory.jaxmpp().getModulesManager().getModule(MessageArchivingModule.class);
                try {
                        Date from = new Date();
                        from.setDate(1);
                        from.setTime(((long) (from.getTime() / MILIS_PER_DAY)) * MILIS_PER_DAY);
                        Date to = new Date(from.getTime());
                        to.setMonth(to.getMonth() + 1);
                        
                        archiveModule.listCollections(JID.jidInstance(jid), from, to, null, new CollectionAsyncCallback() {
                                @Override
                                protected void onCollectionReceived(ResultSet<Chat> vcard) throws XMLException {
                                        log.warning("received response with results");
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }

                                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                        log.warning("received error response");
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }

                                public void onTimeout() throws JaxmppException {
                                        log.warning("timeout");                                        
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }
                                
                        });
                } catch (XMLException ex) {
                        Logger.getLogger(ArchiveViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JaxmppException ex) {
                        Logger.getLogger(ArchiveViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                }*/
	}

}
