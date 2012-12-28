/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tigase.jaxmpp.ext.client.xmpp.modules.archive;

import java.util.Date;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.utils.DateTimeFormat;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class Chat {

	private Date start;

	private String subject;

	private JID withJid;

	public Date getStart() {
		return start;
	}

	public String getSubject() {
		return subject;
	}

	public JID getWithJid() {
		return withJid;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setWithJid(JID withJid) {
		this.withJid = withJid;
	}

        void process(Element chat, DateTimeFormat df1) throws XMLException {
		setWithJid(JID.jidInstance(chat.getAttribute("with")));
		setStart(df1.parse(chat.getAttribute("start")));
		setSubject(chat.getAttribute("subject"));
        }
}
