/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.management;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import static tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.ITEMS_XMLNS;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.site.client.ClientFactory;

/**
 *
 * @author andrzej
 */
public class ManagementViewImpl extends ResizeComposite implements ManagementView, ProvidesResize {

	private static final Logger log = Logger.getLogger(ManagementViewImpl.class.getName());

	public static final String COMMANDS_FEATURE = "http://jabber.org/protocol/commands";

	private final ClientFactory factory;
	private final DiscoComponentsCallback discoComponentsCallback;
	private final CommandGroupsList commandGroupsList;

	private final FlowPanel layout;
	
	public ManagementViewImpl(ClientFactory factory_) {
		this.factory = factory_;

		AppView appView = new AppView(factory);
		appView.setActionBar(factory.actionBarFactory().createActionBar(this));

		factory.actionBarFactory().addLink("management", factory.i18n().management(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				factory.placeController().goTo(new ManagementPlace());
			}
		});

		discoComponentsCallback = new DiscoComponentsCallback();

		commandGroupsList = new CommandGroupsList(Style.Unit.EM);
		appView.setLeftSidebar(commandGroupsList.asWidget(), 25);
		
		layout = new tigase.sure.web.base.client.widgets.FlowPanel();
        layout.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		appView.setCenter(layout);
		initWidget(appView);
	}
	
	@Override
	public void refresh() {
		commandGroupsList.reset();
		JID jid = JID.jidInstance(factory.jaxmpp().getSessionObject().getUserBareJid().getDomain());
		try {
			factory.jaxmpp().getModule(DiscoveryModule.class).getItems(jid, discoComponentsCallback);
		} catch (JaxmppException ex) {
			log.log(Level.SEVERE, null, ex);
		}
	}

	private class DiscoComponentsCallback extends DiscoveryModule.DiscoItemsAsyncCallback {

		@Override
		public void onInfoReceived(String attribute, ArrayList<DiscoveryModule.Item> items) throws XMLException {
			for (DiscoveryModule.Item item : items) {

				DiscoveryModule module = factory.jaxmpp().getModulesManager().getModule(DiscoveryModule.class);
				try {
					module.getInfo(item.getJid(), item.getNode(), new DiscoInfoCallback(item));
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, null, ex);
				}
			}
		}

		public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onTimeout() throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class DiscoInfoCallback extends DiscoveryModule.DiscoInfoAsyncCallback {

		private final DiscoveryModule.Item item;

		public DiscoInfoCallback(DiscoveryModule.Item item) {
			super(item.getNode());
			this.item = item;
		}

		@Override
		protected void onInfoReceived(String node, Collection<DiscoveryModule.Identity> identities, Collection<String> features) throws XMLException {
			if (features != null && features.contains(COMMANDS_FEATURE)) {
				try {
					factory.jaxmpp().getModule(DiscoveryModule.class).getItems(item.getJid(), COMMANDS_FEATURE, new DiscoCommandCallback());
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, null, ex);
				}
			}
		}

		public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onTimeout() throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class DiscoCommandCallback implements AsyncCallback {

		@Override
		public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void onSuccess(Stanza responseStanza) throws XMLException {
			final Element query = responseStanza.getChildrenNS("query", ITEMS_XMLNS);
			List<Element> ritems = query.getChildren("item");
			ArrayList<CommandItem> items = new ArrayList<CommandItem>();
			for (Element i : ritems) {
				CommandItem to = new CommandItem();
				if (i.getAttribute("jid") != null) {
					to.setJid(JID.jidInstance(i.getAttribute("jid")));
				} else {
					to.setJid(responseStanza.getFrom());
				}
				to.setName(i.getAttribute("name"));
				to.setNode(i.getAttribute("node"));
				String group = i.getAttribute("group");
				if (group == null || group.isEmpty())
					group = factory.i18n().commandUndefinedGroup();
				to.setGroup(group);
				items.add(to);
			}
			onCommandItemsReceived(query.getAttribute("node"), items);
		}

		@Override
		public void onTimeout() throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		public void onCommandItemsReceived(String node, List<CommandItem> commands) {
			for (CommandItem item : commands) {
				commandGroupsList.addCommand(item);
			}
			commandGroupsList.refresh();
		}
	}


}
