/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import java.util.HashMap;
import java.util.Map;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.forms.Field;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;

/**
 *
 * @author andrzej
 */
public class StatsItem {

	public class Item {
		public final Integer lastMinutePackets;
		public final Integer totalInQueuesWait;
		public final Integer totalOutQueuesWait;
		// sess-man only
		public final Integer openUserConnections;
		public final Integer maxUserConnections;
		public final Integer openUserSessions;
		public final Integer maxUserSessions;

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
	
	private final Map<String,Item> values = new HashMap<String,Item>();
	private final String prefix;
	
	private boolean openUserConnections = false;
	
	public StatsItem(String prefix) {
		this.prefix = prefix;
	}
	
	public void setValues(String clusterNode, JabberDataElement data) throws XMLException {
		Item item = new Item(data, prefix);
		if (item.openUserConnections != null)
			openUserConnections = true;
		values.put(clusterNode, item);
	}
	
	public Item getValues(String clusterNode) {
		Item item = values.get(clusterNode);
		if (item == null)
			item = new Item();
		return item;
	}
	
	public boolean hasOpenUserConnections() {
		return openUserConnections;
	}
	
	private static Integer getValue(JabberDataElement data, String prefix, String field) throws XMLException {
		Field f =  data.getField(prefix + field);
		if (f == null) 
			return null;
		return Integer.parseInt("0" + f.getFieldValue().toString());
	}
	
}
