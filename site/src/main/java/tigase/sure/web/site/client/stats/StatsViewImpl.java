/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;
import tigase.jaxmpp.core.client.xmpp.forms.XDataType;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.Action;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.AdHocCommansModule;
import tigase.jaxmpp.core.client.xmpp.modules.adhoc.State;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.disco.CommandsWidget;
import tigase.sure.web.site.client.disco.DiscoItem;
import tigase.sure.web.site.client.disco.DiscoItemCell;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.events.ServerFeaturesChangedHandler;

/**
 *
 * @author andrzej
 */
public class StatsViewImpl extends ResizeComposite implements StatsView {

	private final ClientFactory factory;
	
	private final DiscoItemsCallback discoItemsCallback = new DiscoItemsCallback();
	private final DiscoItemsNamesComparator itemsNamesComparator = new DiscoItemsNamesComparator();
	
	private final ListDataProvider<DiscoItem> provider;
	private final CellList<DiscoItem> list;
	private final tigase.sure.web.base.client.widgets.FlowPanel layout;
	private final StatsStore store = new StatsStore();
	private ChartJS packetsPerSecondGraph;
	private ChartJS totalQueuesWaitGraph;
	private Timer timer;
	private ChartJS userConnectionsGraph;
	private ChartJS userSessionsGraph;
	
	private final ServerFeaturesChangedHandler serverFeaturesChangedHandler = new ServerFeaturesChangedHandler() {

		@Override
		public void serverFeaturesChanged(Collection<DiscoveryModule.Identity> identities, Collection<String> features) {
			BareJID jid = factory.sessionObject().getUserBareJid();
			boolean hidden = true;
			
			if (jid != null) {
				DiscoveryModule module = factory.jaxmpp().getModulesManager().getModule(DiscoveryModule.class);
				try {
					module.getItems(JID.jidInstance(jid.getDomain()), null, new DiscoveryModule.DiscoItemsAsyncCallback() {

						@Override
						public void onInfoReceived(String attribute, ArrayList<DiscoveryModule.Item> items) throws XMLException {
							boolean hidden = true;
							if (items != null) {
								for (DiscoveryModule.Item item : items) {
									if (item.getJid() != null && "stats".equals(item.getJid().getLocalpart())) {
										hidden = false;
									}
								}
							}
							factory.actionBarFactory().setVisible("stats", !hidden);
						}

						@Override
						public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
						}

						@Override
						public void onTimeout() throws JaxmppException {
						}
					});
				} catch (XMLException ex) {
					Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				} catch (JaxmppException ex) {
					Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
				}			
			}
			factory.actionBarFactory().setVisible("stats", !hidden);
		}

	};
	
    public StatsViewImpl(ClientFactory factory_) {
		factory = factory_;	
		
		AppView appView = new AppView(factory);
		appView.setActionBar(factory.actionBarFactory().createActionBar(this));
		
		factory.actionBarFactory().addLink("stats", factory.i18n().statistics(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				factory.placeController().goTo(new StatsPlace());
			}

		});	
		
		factory.eventBus().addHandler(ServerFeaturesChangedEvent.TYPE, serverFeaturesChangedHandler);
		
		provider = new ListDataProvider<DiscoItem>();
		list = new CellList<DiscoItem>(new DiscoItemCell(factory));
		provider.addDataDisplay(list);	
		
		final SingleSelectionModel<DiscoItem> selectionModel = new SingleSelectionModel<DiscoItem>();
		list.setSelectionModel(selectionModel);

		appView.setLeftSidebar(new ScrollPanel(list), 25);
		
		layout = new tigase.sure.web.base.client.widgets.FlowPanel();
		layout.getElement().getStyle().setWidth(100, Style.Unit.PCT);	
		layout.getElement().getStyle().setOverflowY(Style.Overflow.AUTO);
		appView.setCenter(layout);
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {
				DiscoItem item = selectionModel.getSelectedObject();
				retrieveStats(item.getJid(), item.getNode());
			}

		});
		
		initWidget(appView);
	}
	
	@Override
	public void refresh() {
		if (packetsPerSecondGraph == null) {
			//com.google.gwt.dom.client.Element c1 = DOM.createElement("canvas");
			//c1.setAttribute("id", "packetsPerSecondGraph");
			//c1.setAttribute("width", "90%");
			//c1.setAttribute("height", "300px");
			//layout.getElement().appendChild(c1);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			Map<String, Object> dataset = new HashMap<String, Object>();
			dataset.put("label", "Packets last minute");
			dataset.put("fillColor", "rgba(220,220,220,0.2)");
			dataset.put("strokeColor", "rgba(220,220,220,1)");
			dataset.put("pointColor", "rgba(220,220,220,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(220,220,220,1)");
			dataset.put("data", new ArrayList());
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
				((List) dataset.get("data")).add(0);
			}
			((List) data.get("datasets")).add(dataset);
			packetsPerSecondGraph = new ChartJS(data);
			packetsPerSecondGraph.setPixelWidth(600);
			packetsPerSecondGraph.setPixelHeight(300);
			layout.add(packetsPerSecondGraph);
			//packetsPerSecondGraph = ChartJS.createLineChart("packetsPerSecondGraph", data);	
			//packetsPerSecondGraph.addToElement(layout.getElement());
		}
		if (totalQueuesWaitGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			Map<String, Object> dataset = new HashMap<String, Object>();
			dataset.put("label", "In queue wait");
			dataset.put("fillColor", "rgba(220,220,220,0.2)");
			dataset.put("strokeColor", "rgba(220,220,220,1)");
			dataset.put("pointColor", "rgba(220,220,220,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(220,220,220,1)");
			dataset.put("data", new ArrayList());		
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
				((List) dataset.get("data")).add(0);
			}
			((List) data.get("datasets")).add(dataset);
			dataset = new HashMap<String, Object>();
			dataset.put("label", "Out queue wait");
			dataset.put("fillColor", "rgba(151,187,205,0.2)");
			dataset.put("strokeColor", "rgba(151,187,205,1)");
			dataset.put("pointColor", "rgba(151,187,205,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(151,187,205,1)");
			dataset.put("data", new ArrayList());			
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) dataset.get("data")).add(0);
			}
			((List) data.get("datasets")).add(dataset);
			totalQueuesWaitGraph = new ChartJS(data);
			totalQueuesWaitGraph.setPixelWidth(600);
			totalQueuesWaitGraph.setPixelHeight(300);
			layout.add(totalQueuesWaitGraph);			
		}
		if (userConnectionsGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			Map<String, Object> dataset = new HashMap<String, Object>();
			dataset.put("label", "Maximum user connections");
			dataset.put("fillColor", "rgba(220,220,220,0.2)");
			dataset.put("strokeColor", "rgba(220,220,220,1)");
			dataset.put("pointColor", "rgba(220,220,220,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(220,220,220,1)");
			dataset.put("data", new ArrayList());		
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
				((List) dataset.get("data")).add(0);
			}
			((List) data.get("datasets")).add(dataset);
			dataset = new HashMap<String, Object>();
			dataset.put("label", "Open user connections");
			dataset.put("fillColor", "rgba(151,187,205,0.2)");
			dataset.put("strokeColor", "rgba(151,187,205,1)");
			dataset.put("pointColor", "rgba(151,187,205,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(151,187,205,1)");
			dataset.put("data", new ArrayList());			
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) dataset.get("data")).add(0);
			}		
			((List) data.get("datasets")).add(dataset);
			userConnectionsGraph = new ChartJS(data);
			userConnectionsGraph.setPixelWidth(600);
			userConnectionsGraph.setPixelHeight(300);
			layout.add(userConnectionsGraph);			
		}
		if (userSessionsGraph == null) {
				Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			Map<String, Object> dataset = new HashMap<String, Object>();
			dataset.put("label", "Maximum user sessions");
			dataset.put("fillColor", "rgba(220,220,220,0.2)");
			dataset.put("strokeColor", "rgba(220,220,220,1)");
			dataset.put("pointColor", "rgba(220,220,220,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(220,220,220,1)");
			dataset.put("data", new ArrayList());		
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
				((List) dataset.get("data")).add(0);
			}
			((List) data.get("datasets")).add(dataset);
			dataset = new HashMap<String, Object>();
			dataset.put("label", "Open user sessions");
			dataset.put("fillColor", "rgba(151,187,205,0.2)");
			dataset.put("strokeColor", "rgba(151,187,205,1)");
			dataset.put("pointColor", "rgba(151,187,205,1)");
			dataset.put("pointStrokeColor", "#fff");
			dataset.put("pointHighlightFill", "#fff");
			dataset.put("pointHighlightStroke", "rgba(151,187,205,1)");
			dataset.put("data", new ArrayList());			
			for (int i=0; i<store.getMaxRecords(); i++) {
				((List) dataset.get("data")).add(0);
			}		
			((List) data.get("datasets")).add(dataset);
			userSessionsGraph = new ChartJS(data);
			userSessionsGraph.setPixelWidth(600);
			userSessionsGraph.setPixelHeight(300);
			layout.add(userSessionsGraph);			
		}		
		JID jid = JID.jidInstance("stats", factory.jaxmpp().getSessionObject().getUserBareJid().getDomain());

		DiscoveryModule module = factory.jaxmpp().getModulesManager().getModule(DiscoveryModule.class);
		try {
			module.getItems(jid, "stats", discoItemsCallback);
		} catch (XMLException ex) {
			Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JaxmppException ex) {
			Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void retrieveStats(final JID jid, final String node) {
		store.clear();
		boolean sessMan = (node.endsWith("/sess-man"));
		resetGraphs(sessMan);
		userConnectionsGraph.setVisible(sessMan);
		userSessionsGraph.setVisible(sessMan);
		if (timer != null)
			timer.cancel();
		
		final Runnable run = new Runnable() {

			@Override
			public void run() {
				if (!StatsViewImpl.this.isAttached()) {
					timer.cancel();
					return;
				}
				AdHocCommansModule adHocCommands = factory.jaxmpp().getModulesManager().getModule(AdHocCommansModule.class);
				try {
					JabberDataElement data = new JabberDataElement(XDataType.submit);
					data.addListSingleField("Stats level", "FINEST");
					// data should contain info about level of stats - FINEST ??
					adHocCommands.execute(jid, node, Action.execute, data, new AdHocCommansModule.AdHocCommansAsyncCallback() {

						@Override
						protected void onResponseReceived(String sessionid, String node, State status, JabberDataElement data) throws JaxmppException {
							StatsItem item = new StatsItem(data, node.replace("stats/", ""));
							boolean overflow = store.add(item);
							refreshGraphs(item, true);
						}

						@Override
						public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
							throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
						}

						@Override
						public void onTimeout() throws JaxmppException {
							throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
						}

					});
//						if (progressHandler != null) progressHandler.started();
				} catch (JaxmppException ex) {
					Logger.getLogger(CommandsWidget.class.getName()).log(Level.SEVERE, null, ex);
				}
			}			
			
		};
		
		timer = new Timer() {

			@Override
			public void run() {
				run.run();
			}
			
		};
		timer.scheduleRepeating(60 * 1000);
		run.run();
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
	
	private void refreshGraphs(StatsItem item, boolean overflow) {
		if (overflow) {
			packetsPerSecondGraph.removeData();
			totalQueuesWaitGraph.removeData();
			if (item.openUserConnections != null) {
				userConnectionsGraph.removeData();
				userSessionsGraph.removeData();
			}
		}
		
		DateTimeFormat format = DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.HOUR24_MINUTE);
		String ts = format.format(new Date());
		JsArrayInteger data = (JsArrayInteger) JsArrayInteger.createArray();
		data.push(item.lastMinutePackets);
		packetsPerSecondGraph.addData(data, ts);
		data = (JsArrayInteger) JsArrayInteger.createArray();
		data.push(item.totalInQueuesWait);
		data.push(item.totalOutQueuesWait);
		totalQueuesWaitGraph.addData(data, ts);
		if (item.openUserConnections != null) {
			data = (JsArrayInteger) JsArrayInteger.createArray();
			data.push(item.maxUserConnections);
			data.push(item.openUserConnections);
			userConnectionsGraph.addData(data, ts);
			data = (JsArrayInteger) JsArrayInteger.createArray();
			data.push(item.maxUserSessions);
			data.push(item.openUserSessions);
			userSessionsGraph.addData(data, ts);			
		}
	}
	
	private void resetGraphs(boolean sessMan) {
		for (int i=0; i<store.getMaxRecords(); i++) {
			JsArrayInteger data = (JsArrayInteger) JsArrayInteger.createArray();
			data.push(0);
			packetsPerSecondGraph.addData(data, " ");
			packetsPerSecondGraph.removeData();
			data = (JsArrayInteger) JsArrayInteger.createArray();
			data.push(0);
			data.push(0);
			totalQueuesWaitGraph.addData(data, " ");
			totalQueuesWaitGraph.removeData();
			if (sessMan) {
				userConnectionsGraph.addData(data, " ");
				userConnectionsGraph.removeData();
				userSessionsGraph.addData(data, " ");
				userSessionsGraph.removeData();
			}
		}
	}
	
	private class DiscoItemsCallback extends DiscoveryModule.DiscoItemsAsyncCallback {

		@Override
		public void onInfoReceived(String attribute, ArrayList<DiscoveryModule.Item> items) throws XMLException {
			provider.getList().clear();
			for (DiscoveryModule.Item item : items) {
				DiscoItem discoItem = new DiscoItem(item.getJid(), item.getNode(), item.getName());
				addItem(discoItem);
			}
			provider.flush();
		}

		public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void onTimeout() throws JaxmppException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}
	
	private class DiscoItemsNamesComparator implements Comparator<DiscoItem> {

		private String getName(DiscoItem t1) {
			DiscoveryModule.Identity identity1 = t1.getIdentities() != null ? t1.getIdentities().iterator().next() : null;

			String name1 = (identity1 != null && identity1.getName() != null) ? identity1.getName() : t1.getName();
			if (name1 == null) {
				name1 = t1.getJid().toString();
			}

			return name1;
		}

		@Override
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
