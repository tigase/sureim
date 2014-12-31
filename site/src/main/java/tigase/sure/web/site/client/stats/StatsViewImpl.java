/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
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
import tigase.jaxmpp.core.client.xmpp.forms.Field;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;
import tigase.jaxmpp.core.client.xmpp.forms.TextMultiField;
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

	private List<String> clusterNodes = new ArrayList<String>();
	private List<String> colors = new ArrayList<String>();

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
	private NodesStatsGrid nodesStatsGrid;
	private ChartJSX nodesConnectionsGraph;
	private ChartJSX nodesPacketPerMinuteGraph;

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

	private void refreshColors() {
		colors.clear();
		int x = (int) Math.ceil(Math.sqrt((double) clusterNodes.size()));
		for (int i = 1; i <= x; i++) {
			for (int j = 1; j <= x; j++) {
				for (int k = 1; k <= x; k++) {
					colors.add("" + ((int) Math.floor(192 / i)) + "," + ((int) Math.floor(192 / j)) + "," + ((int) Math.floor(192 / k)));
				}
			}
		}

	}

	private void initGraphs() {
		if (nodesStatsGrid == null) {
			nodesStatsGrid = new NodesStatsGrid();
			layout.getElement().appendChild(nodesStatsGrid.getElement());
			nodesStatsGrid.refresh();
		}
		if (nodesConnectionsGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode));
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Client connections (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode) + clusterNodes.size());
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Server connections (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (int i = 0; i < store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
			}
			nodesConnectionsGraph = new ChartJSX(data, new String[]{
				"c2s/Open connections",
				"s2s/Open connections"
			});
			nodesConnectionsGraph.setPixelWidth(600);
			nodesConnectionsGraph.setPixelHeight(300);
			layout.add(nodesConnectionsGraph);
		}
		if (nodesPacketPerMinuteGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode));
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Packets per minute (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (int i = 0; i < store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
			}
			nodesPacketPerMinuteGraph = new ChartJSX(data, new String[]{
				"message-router/Last minute packets"
			});
			nodesPacketPerMinuteGraph.setPixelWidth(600);
			nodesPacketPerMinuteGraph.setPixelHeight(300);
			layout.add(nodesPacketPerMinuteGraph);
		}
		if (packetsPerSecondGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode));
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Packets last minute (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (int i = 0; i < store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
			}
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
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode));
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "In queue wait (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode) + clusterNodes.size());
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Out queue wait (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (int i = 0; i < store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
			}
			totalQueuesWaitGraph = new ChartJS(data);
			totalQueuesWaitGraph.setPixelWidth(600);
			totalQueuesWaitGraph.setPixelHeight(300);
			layout.add(totalQueuesWaitGraph);
		}
		if (userConnectionsGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode));
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Maximum user connections (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode) + clusterNodes.size());
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Open user connections (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (int i = 0; i < store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
			}

			userConnectionsGraph = new ChartJS(data);
			userConnectionsGraph.setPixelWidth(600);
			userConnectionsGraph.setPixelHeight(300);
			layout.add(userConnectionsGraph);
		}
		if (userSessionsGraph == null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("labels", new ArrayList<String>());
			data.put("datasets", new ArrayList());
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode));
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Maximum user sessions (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (String clusterNode : clusterNodes) {
				String color = colors.get(clusterNodes.indexOf(clusterNode) + clusterNodes.size());
				Map<String, Object> dataset = new HashMap<String, Object>();
				dataset.put("label", "Open user sessions (" + clusterNode + ")");
				dataset.put("fillColor", "rgba(" + color + ",0.2)");
				dataset.put("strokeColor", "rgba(" + color + ",1)");
				dataset.put("pointColor", "rgba(" + color + ",1)");
				dataset.put("pointStrokeColor", "#fff");
				dataset.put("pointHighlightFill", "#fff");
				dataset.put("pointHighlightStroke", "rgba(" + color + ",1)");
				dataset.put("data", new ArrayList());
				for (int i = 0; i < store.getMaxRecords(); i++) {
					((List) dataset.get("data")).add(0);
				}
				((List) data.get("datasets")).add(dataset);
			}
			for (int i = 0; i < store.getMaxRecords(); i++) {
				((List) data.get("labels")).add(" ");
			}

			userSessionsGraph = new ChartJS(data);
			userSessionsGraph.setPixelWidth(600);
			userSessionsGraph.setPixelHeight(300);
			layout.add(userSessionsGraph);
		}
	}

	@Override
	public void refresh() {
		final JID jid = JID.jidInstance("stats", factory.jaxmpp().getSessionObject().getUserBareJid().getDomain());
		final boolean loadMainStats = provider.getList().isEmpty();
		try {
			factory.jaxmpp().getModule(AdHocCommansModule.class).execute(JID.jidInstance("cl-comp", factory.sessionObject().getUserBareJid().getDomain(), null),
					"cluster-nodes-list", Action.execute, null, new AdHocCommansModule.AdHocCommansAsyncCallback() {

						@Override
						protected void onResponseReceived(String sessionid, String node, State status, JabberDataElement data) throws JaxmppException {
							try {
								clusterNodes.clear();
								String[] nodesStr = ((TextMultiField) data.getField("Cluster nodes:")).getFieldValue();
								for (String nodeStr : nodesStr) {
									clusterNodes.add(nodeStr);
								}
								Collections.sort(clusterNodes);
								finished();
							} catch (Throwable ex) {
								Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
							}
						}

						@Override
						public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
							// retrieval of list of cluster nodes failed, let's assume this is not clustered installation
							clusterNodes.clear();
							clusterNodes.add(factory.sessionObject().getUserBareJid().getDomain());
							finished();
						}

						@Override
						public void onTimeout() throws JaxmppException {
							// retrieval of list of cluster nodes failed, let's assume this is not clustered installation
							clusterNodes.clear();
							clusterNodes.add(factory.sessionObject().getUserBareJid().getDomain());
							finished();
						}

						private void finished() {
							refreshColors();
							initGraphs();
							if (loadMainStats) {
								retrieveStats(jid, "stats");
							}
						}
					});
		} catch (JaxmppException ex) {
			Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
		}

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
		if (!"stats".equals(node)) {
			retrieveStatsForComponent(jid, node);
			return;
		}

		nodesStatsGrid.setVisible(true);
		nodesConnectionsGraph.setVisible(true);
		nodesPacketPerMinuteGraph.setVisible(true);

		packetsPerSecondGraph.setVisible(false);
		totalQueuesWaitGraph.setVisible(false);
		userConnectionsGraph.setVisible(false);
		userSessionsGraph.setVisible(false);

		final Runnable run = new Runnable() {

			@Override
			public void run() {
				if (!StatsViewImpl.this.isAttached()) {
					timer.cancel();
					return;
				}
				final Counter counter = new Counter(clusterNodes.size());
				AdHocCommansModule adHocCommands = factory.jaxmpp().getModulesManager().getModule(AdHocCommansModule.class);
				try {
					JabberDataElement data = new JabberDataElement(XDataType.submit);
					data.addListSingleField("Stats level", "FINEST");
					// data should contain info about level of stats - FINEST ??
					for (final String clusterNode : clusterNodes) {
						adHocCommands.execute(JID.jidInstance(jid.getLocalpart(), clusterNode), node, Action.execute, data, new AdHocCommansModule.AdHocCommansAsyncCallback() {

							@Override
							protected void onResponseReceived(String sessionid, String node, State status, JabberDataElement data) throws JaxmppException {
								nodesStatsGrid.update(clusterNode, data);
								nodesConnectionsGraph.addData(clusterNode, data);
								nodesPacketPerMinuteGraph.addData(clusterNode, data);
								finished();
							}

							@Override
							public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
								finished();
							}

							@Override
							public void onTimeout() throws JaxmppException {
								finished();
							}

							protected void finished() {
								if (counter.dec() > 0) {
									return;
								}
								nodesStatsGrid.refresh();
								try {
									nodesConnectionsGraph.updateChart();
									nodesPacketPerMinuteGraph.updateChart();
								} catch (XMLException ex) {
									Logger.getLogger(StatsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
								}
							}

						});
					}
				} catch (JaxmppException ex) {
					Logger.getLogger(CommandsWidget.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		};

		schedule(run, 60 * 1000);
	}

	private void retrieveStatsForComponent(final JID jid, final String node) {
		boolean sessMan = (node != null && node.endsWith("/sess-man"));
		resetGraphs(sessMan);

		nodesStatsGrid.setVisible(false);
		nodesConnectionsGraph.setVisible(false);
		nodesPacketPerMinuteGraph.setVisible(false);

		packetsPerSecondGraph.setVisible(true);
		totalQueuesWaitGraph.setVisible(true);

		userConnectionsGraph.setVisible(sessMan);
		userSessionsGraph.setVisible(sessMan);

		final Runnable run = new Runnable() {

			@Override
			public void run() {
				if (!StatsViewImpl.this.isAttached()) {
					timer.cancel();
					return;
				}
				final Counter counter = new Counter(clusterNodes.size());
				final StatsItem item = new StatsItem(node.replace("stats/", ""));
				AdHocCommansModule adHocCommands = factory.jaxmpp().getModulesManager().getModule(AdHocCommansModule.class);
				try {
					JabberDataElement data = new JabberDataElement(XDataType.submit);
					data.addListSingleField("Stats level", "FINEST");
					// data should contain info about level of stats - FINEST ??
					for (final String clusterNode : clusterNodes) {
						adHocCommands.execute(JID.jidInstance(jid.getLocalpart(), clusterNode), node, Action.execute, data, new AdHocCommansModule.AdHocCommansAsyncCallback() {

							@Override
							protected void onResponseReceived(String sessionid, String node, State status, JabberDataElement data) throws JaxmppException {
								item.setValues(clusterNode, data);
								finished();
							}

							@Override
							public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
								finished();
							}

							@Override
							public void onTimeout() throws JaxmppException {
								finished();
							}

							protected void finished() {
								if (counter.dec() > 0) {
									return;
								}
								boolean overflow = store.add(item);
								refreshGraphs(item, true);
							}

						});
					}
//						if (progressHandler != null) progressHandler.started();
				} catch (JaxmppException ex) {
					Logger.getLogger(CommandsWidget.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		};

		schedule(run, 60 * 1000);
	}

	private void schedule(final Runnable run, int time) {
		if (timer != null) {
			timer.cancel();
		}

		timer = new Timer() {

			@Override
			public void run() {
				run.run();
			}

		};
		timer.scheduleRepeating(time);
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

	private void refreshGraphs(StatsItem items, boolean overflow) {
		if (overflow) {
			packetsPerSecondGraph.removeData();
			totalQueuesWaitGraph.removeData();
			if (items.hasOpenUserConnections()) {
				userConnectionsGraph.removeData();
				userSessionsGraph.removeData();
			}
		}

		DateTimeFormat format = DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.HOUR24_MINUTE);
		String ts = format.format(new Date());
		JsArrayInteger data = (JsArrayInteger) JsArrayInteger.createArray();
		for (String clusterNode : clusterNodes) {
			StatsItem.Item item = items.getValues(clusterNode);
			data.push(item.lastMinutePackets);
		}
		packetsPerSecondGraph.addData(data, ts);
		data = (JsArrayInteger) JsArrayInteger.createArray();
		for (String clusterNode : clusterNodes) {
			StatsItem.Item item = items.getValues(clusterNode);
			data.push(item.totalInQueuesWait);
		}
		for (String clusterNode : clusterNodes) {
			StatsItem.Item item = items.getValues(clusterNode);
			data.push(item.totalOutQueuesWait);
		}
		totalQueuesWaitGraph.addData(data, ts);
		if (items.hasOpenUserConnections()) {
			data = (JsArrayInteger) JsArrayInteger.createArray();
			for (String clusterNode : clusterNodes) {
				StatsItem.Item item = items.getValues(clusterNode);
				data.push(item.maxUserConnections);
			}
			for (String clusterNode : clusterNodes) {
				StatsItem.Item item = items.getValues(clusterNode);
				data.push(item.openUserConnections);
			}
			userConnectionsGraph.addData(data, ts);
			data = (JsArrayInteger) JsArrayInteger.createArray();
			for (String clusterNode : clusterNodes) {
				StatsItem.Item item = items.getValues(clusterNode);
				data.push(item.maxUserSessions);
			}
			for (String clusterNode : clusterNodes) {
				StatsItem.Item item = items.getValues(clusterNode);
				data.push(item.openUserSessions);
			}
			userSessionsGraph.addData(data, ts);
		}
	}

	private void resetGraphs(boolean sessMan) {
		for (int i = 0; i < store.getMaxRecords(); i++) {
			JsArrayInteger data = (JsArrayInteger) JsArrayInteger.createArray();
			for (String clusterNode : clusterNodes) {
				data.push(0);
			}
			packetsPerSecondGraph.addData(data, " ");
			packetsPerSecondGraph.removeData();
			data = (JsArrayInteger) JsArrayInteger.createArray();
			for (String clusterNode : clusterNodes) {
				data.push(0);
				data.push(0);
			}
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

	private class Counter {

		private int value;

		public Counter(int start) {
			this.value = start;
		}

		public int dec() {
			this.value--;
			return this.value;
		}

	}

	private class ChartJSX extends ChartJS {

		protected final Map<String, JabberDataElement> data = new HashMap<String, JabberDataElement>();

		protected final String[] fieldNames;

		public ChartJSX(Map data, String[] fieldNames) {
			super(data);
			this.fieldNames = fieldNames;
		}

		public void addData(String node, JabberDataElement data) {
			this.data.put(node, data);
		}

		public void updateChart() throws XMLException {
			this.removeData();
			DateTimeFormat format = DateTimeFormat.getFormat(com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.HOUR24_MINUTE);
			String ts = format.format(new Date());
			JsArrayInteger arr = (JsArrayInteger) JsArrayInteger.createArray();
			for (String field : fieldNames) {
				for (String clusterNode : clusterNodes) {
					JabberDataElement data = this.data.get(clusterNode);
					int val = data == null ? -1 : getValue(data, field);
					arr.push(val);
				}
			}
			addData(arr, ts);
			data.clear();
		}

		private Integer getValue(JabberDataElement data, String field) throws XMLException {
			Field f = data.getField(field);
			if (f == null) {
				return null;
			}
			return Integer.parseInt("0" + f.getFieldValue().toString());
		}
	}

	private class NodesStatsGrid {

		private class NItem {

			private final String node;
			private final String uptime;
			private int onlineUsers;
//			private int messagesPerMinute;
//			private int presencesPerMinute;
			private final int lastMinPackets;
			private final int cpuPerc;
			private final int memPerc;

			NItem(String node, JabberDataElement data) throws XMLException {
				this.node = node;

				uptime = getValue(data, "message-router/Uptime");
				String v = getValue(data, "message-router/Last minute packets");
				lastMinPackets = Integer.parseInt("0" + v);
				v = getValue(data, "sess-man/Open user sessions");
				onlineUsers = Integer.parseInt("0" + v);
				v = getValue(data, "message-router/CPU usage [%]");
				cpuPerc = Integer.parseInt("0" + v.substring(2, 4));
				v = getValue(data, "message-router/HEAP usage [%]");
				memPerc = Integer.parseInt("0" + v.substring(0, 2));
			}

			final String getValue(JabberDataElement data, String name) throws XMLException {
				Field f = data.getField(name);
				if (f == null) {
					return null;
				}
				return f.getFieldValue().toString();
			}

			String getValue(int col) {
				switch (col) {
					case 0:
						return node;
					case 1:
						return uptime;
					case 2:
						return String.valueOf(onlineUsers);
					case 3:
						return String.valueOf(lastMinPackets);
					case 4:
						return String.valueOf(cpuPerc);
					case 5:
						return String.valueOf(memPerc);
				}
				return null;
			}
		}

		private Map<String, NItem> items = new HashMap<String, NItem>();

		private final String[] hnames = {"Node", "Uptime", "Online users",
			//"Messages/minute", "Presences/minute",
			"Packets/minute",
			"CPU%", "MEM%"};

		private TableElement table;

		public NodesStatsGrid() {
			table = Document.get().createTableElement();
			table.getStyle().setBorderWidth(0, Style.Unit.PX);
			table.setCellPadding(0);
			table.setCellSpacing(0);
		}

		public void update(String node, JabberDataElement data) throws XMLException {
			NItem item = new NItem(node, data);
			items.put(node, item);
		}

		public void refresh() {
			int len = table.getRows().getLength();
			for (int i = 0; i < len; i++) {
				table.deleteRow(0);
			}

			TableRowElement header = table.createTHead().insertRow(0);
			int i = 0;
			for (String hname : hnames) {
				TableCellElement hcell = header.insertCell(i);
				hcell.setInnerText(hname);
				hcell.getStyle().setFontWeight(Style.FontWeight.BOLD);
				hcell.getStyle().setPadding(6, Style.Unit.PX);
				hcell.getStyle().setBackgroundColor("lightgray");
				i++;
			}

			i = 1;
			for (String node : clusterNodes) {
				NItem item = items.get(node);
				if (item == null) {
					continue;
				}

				TableRowElement row = table.insertRow(i);

				row.getStyle().setBackgroundColor("rgba(" + colors.get(i - 1) + ",0.2)");
				int j = 0;
				for (String hname : hnames) {
					TableCellElement cell = row.insertCell(j);
					cell.setInnerText(item.getValue(j));
					cell.getStyle().setPadding(4, Style.Unit.PX);
					if (j > 1) {
						cell.setAlign("right");
					}
					j++;
				}
				i++;
			}
		}

		public Element getElement() {
			return table;
		}

		public void setVisible(boolean visible) {
			table.getStyle().setDisplay(visible ? Display.BLOCK : Display.NONE);
		}
	}
}
