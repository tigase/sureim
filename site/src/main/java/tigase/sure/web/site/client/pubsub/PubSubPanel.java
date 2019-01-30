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
package tigase.sure.web.site.client.pubsub;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubErrorCondition;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule.RetrieveItemsAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.sure.web.base.client.ResizablePanel;
import tigase.sure.web.base.client.widgets.Markdown;
import tigase.sure.web.site.client.ClientFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class PubSubPanel
		extends Composite {

	private static final Logger log = Logger.getLogger("PubSubPanel");
	private final RetrieveItemsAsyncCallback callback = new RetrieveItemsAsyncCallbackImpl();
	private final ClientFactory factory;
	private final VerticalPanel itemsPanel;
	private final Comparator<AtomEntry> timestampEntryComparator = new ComparatorImpl();

	public PubSubPanel(ClientFactory clientFactory) {
		factory = clientFactory;

		VerticalPanel root = new VerticalPanel();

		ResizablePanel topPanel = new ResizablePanel();
		root.add(topPanel);

		itemsPanel = new VerticalPanel();
		root.add(itemsPanel);

		initWidget(root);
	}

	public void requestEntries(BareJID jid, String node) {

		PubSubModule pubsub = factory.jaxmpp().getModulesManager().getModule(PubSubModule.class);
		try {
			if (log.isLoggable(Level.FINEST)) {
				log.finest("requesting items for node = " + node);
			}
			pubsub.retrieveItem(jid, node, callback);
		} catch (XMLException ex) {
			log.warning(ex.getMessage());
		} catch (JaxmppException ex) {
			log.warning(ex.getMessage());
		}

	}

	private void updateEntries(List<AtomEntry> entries) {
		itemsPanel.clear();

		Collections.sort(entries, timestampEntryComparator);

		if (log.isLoggable(Level.FINEST)) {
			log.finest("updating entries...");
		}

		for (AtomEntry entry : entries) {
			SafeHtmlBuilder safeHtml = new SafeHtmlBuilder();
			safeHtml.appendHtmlConstant("<h2 style='margin-bottom: 3px;'>");
			safeHtml.appendEscaped(entry.getTitle());
			safeHtml.appendHtmlConstant("</h2>");
			safeHtml.appendHtmlConstant("<span style='font-size: 0.7em; color: #555; display: block;'>");
			if (entry.getAuthorName() != null) {
				safeHtml.appendEscaped(factory.i18n().submittedBy() + " " + entry.getAuthorName());

			}
			if (entry.getUpdated() != null) {
				String dateStr = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(entry.getUpdated());
				safeHtml.appendEscaped(" " + factory.i18n().onDate() + " " + dateStr);
			}
			safeHtml.appendHtmlConstant("</span>");
			if (entry.getContent() != null) {
				String result = Markdown.parse(entry.getContent());
				safeHtml.appendHtmlConstant(result);
			}
			HTML html = new HTML(safeHtml.toSafeHtml());
			itemsPanel.add(html);
		}

	}

	private static class ComparatorImpl
			implements Comparator<AtomEntry> {

		public ComparatorImpl() {
		}

		public int compare(AtomEntry t1, AtomEntry t2) {
			if (t1 == null || t2 == null) {
				return 0;
			}
			if (t1.getUpdated() == null) {
				return -1;
			}
			if (t2.getUpdated() == null) {
				return 1;
			}
			return (int) (t2.getUpdated().getTime() - t1.getUpdated().getTime());
		}
	}

	private class RetrieveItemsAsyncCallbackImpl
			extends RetrieveItemsAsyncCallback {

		public RetrieveItemsAsyncCallbackImpl() {
		}

		public void onTimeout() throws JaxmppException {
			log.warning("pubsub request timed out");
		}

		@Override
		protected void onRetrieve(IQ responseStanza, String nodeName, Collection<Item> items) {

			if (log.isLoggable(Level.FINEST)) {
				log.finest("received items = " + items.size());
			}

			List<AtomEntry> entries = new ArrayList<AtomEntry>();

			for (Item it : items) {
				try {
					Element payload = it.getPayload();
					if (log.isLoggable(Level.FINEST)) {
						log.finest("payload = " + payload.getAsString());
					}
					AtomEntry entry = new AtomEntry(payload, it.getId());
					entries.add(entry);
				} catch (XMLException ex) {
					log.warning(ex.getMessage());
				}
			}

			updateEntries(entries);
		}

		@Override
		protected void onEror(IQ response, ErrorCondition errorCondition, PubSubErrorCondition pubSubErrorCondition) {
			log.warning("pubsubs error = " + errorCondition.name());
		}
	}

}
