/*
 * StatsItem.java
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
package tigase.sure.web.site.client.stats;

import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.forms.Field;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andrzej
 */
public class StatsItem {

	private final String prefix;
	private final Map<String, Item> values = new HashMap<String, Item>();
	private boolean openUserConnections = false;

	private static Integer getValue(JabberDataElement data, String prefix, String field) throws XMLException {
		Field f = data.getField(prefix + field);
		if (f == null) {
			return null;
		}
		return Integer.parseInt("0" + f.getFieldValue().toString());
	}

	public StatsItem(String prefix) {
		this.prefix = prefix;
	}

	public void setValues(String clusterNode, JabberDataElement data) throws XMLException {
		Item item = new Item(data, prefix);
		if (item.openUserConnections != null) {
			openUserConnections = true;
		}
		values.put(clusterNode, item);
	}

	public Item getValues(String clusterNode) {
		Item item = values.get(clusterNode);
		if (item == null) {
			item = new Item();
		}
		return item;
	}

	public boolean hasOpenUserConnections() {
		return openUserConnections;
	}

	public class Item {

		public final Integer lastMinutePackets;
		public final Integer maxUserConnections;
		public final Integer maxUserSessions;
		// sess-man only
		public final Integer openUserConnections;
		public final Integer openUserSessions;
		public final Integer totalInQueuesWait;
		public final Integer totalOutQueuesWait;

		public Item(JabberDataElement data, String prefix) throws XMLException {
			lastMinutePackets = getValue(data, prefix, "/Last minute packets");
			totalInQueuesWait = getValue(data, prefix, "/Total In queues wait");
			totalOutQueuesWait = getValue(data, prefix, "/Total Out queues wait");
			openUserConnections = getValue(data, prefix, "/Open user connections");
			maxUserConnections = getValue(data, prefix, "/Maximum user connections");
			openUserSessions = getValue(data, prefix, "/Open user sessions");
			maxUserSessions = getValue(data, prefix, "/Maximum user sessions");
		}

		public Item() {
			lastMinutePackets = -1;
			totalInQueuesWait = -1;
			totalOutQueuesWait = -1;
			openUserConnections = -1;
			maxUserConnections = -1;
			openUserSessions = -1;
			maxUserSessions = -1;
		}
	}

}
