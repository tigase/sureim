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
	
	public StatsItem(JabberDataElement data, String prefix) throws XMLException {
		lastMinutePackets = getValue(data, prefix, "/Last minute packets");
		totalInQueuesWait = getValue(data, prefix, "/Total In queues wait");
		totalOutQueuesWait = getValue(data, prefix, "/Total Out queues wait");
	}
	
	private Integer getValue(JabberDataElement data, String prefix, String field) throws XMLException {
		Field f =  data.getField(prefix + field);
		if (f == null) 
			return null;
		return Integer.parseInt("0" + f.getFieldValue().toString());
	}
	
}
