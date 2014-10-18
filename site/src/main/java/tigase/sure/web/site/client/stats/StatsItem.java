/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.forms.Field;
import tigase.jaxmpp.core.client.xmpp.forms.JabberDataElement;

/**
 *
 * @author andrzej
 */
public class StatsItem {

	public final Integer lastMinutePackets;
	public final Integer totalInQueuesWait;
	public final Integer totalOutQueuesWait;
	// sess-man only
	public final Integer openUserConnections;
	public final Integer maxUserConnections;
	public final Integer openUserSessions;
	public final Integer maxUserSessions;
	
	public StatsItem(JabberDataElement data, String prefix) throws XMLException {
		lastMinutePackets = getValue(data, prefix, "/Last minute packets");
		totalInQueuesWait = getValue(data, prefix, "/Total In queues wait");
		totalOutQueuesWait = getValue(data, prefix, "/Total Out queues wait");
		openUserConnections = getValue(data, prefix, "/Open user connections");
		maxUserConnections = getValue(data, prefix, "/Maximum user connections");
		openUserSessions = getValue(data, prefix, "/Open user sessions");
		maxUserSessions = getValue(data, prefix, "/Maximum user sessions");
	}
	
	private Integer getValue(JabberDataElement data, String prefix, String field) throws XMLException {
		Field f =  data.getField(prefix + field);
		if (f == null) 
			return null;
		return Integer.parseInt("0" + f.getFieldValue().toString());
	}
	
}
