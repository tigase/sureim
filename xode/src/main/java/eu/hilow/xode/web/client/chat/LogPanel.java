/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.chat;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import eu.hilow.xode.web.client.ClientFactory;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.utils.delay.XmppDelay;

/**
 *
 * @author andrzej
 */
public class LogPanel extends Composite {

        private enum Sender {

                local,
                remote,
                unknown
        };
        private final ScrollPanel scroll;
        private final String name;
        private final String jid;
        private final ClientFactory factory;
        private DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm");
        private Sender lastFrom = Sender.unknown;
        private HTMLPanel log;

        public LogPanel(ClientFactory factory, String jid, String name) {
                this.name = name;
                this.jid = jid;
                this.factory = factory;
                log = new HTMLPanel("<div id=\"Chat-" + jid + "\"></div>");
                scroll = new ScrollPanel(log);
                initWidget(scroll);
        }

        public void appendMessage(Message msg) {
                try {
                        String body = msg.getBody();

                        if (msg == null || body == null) {
                                return;
                        }

                        SafeHtmlBuilder sb = new SafeHtmlBuilder();
                        boolean sameSender = (lastFrom == Sender.local && msg.getFrom() == null) || (lastFrom == Sender.remote && msg.getFrom() != null);
                        if (sameSender) {
                                sb.appendHtmlConstant("<div class=\"time_consecutive\">");
                                XmppDelay delay = XmppDelay.extract(msg);
                                Date time = delay != null ? delay.getStamp() : null;//new Date();
                                if (time == null)
                                        time = new Date();
                                sb.appendEscaped(timeFormat.format(time));
                                sb.appendHtmlConstant("</div>");
                                sb.appendHtmlConstant("<p>");
                                //            body = body.replaceAll("\n", "<br/>");
                                sb.appendEscaped(body);
                                sb.appendHtmlConstant("</p><div id=\"insert-" + jid + "\"></div>");
                        }
                        else {
                                sb.appendHtmlConstant("<div class=\"container " + (msg.getFrom() != null ? "" : "outcontainer") + "\">"
                                        + "<div class=\"sender " + (msg.getFrom() != null ? "incoming" : "outgoing") + "\">");
                                String from = msg.getFrom() == null ? "Me" : name;
                                sb.appendEscaped(from);
                                sb.appendHtmlConstant("</div>"
                                        + "<div class=\"time_initial\">");
                                XmppDelay delay = XmppDelay.extract(msg);
                                Date time = delay != null ? delay.getStamp() : null;//new Date();
                                if (time == null)
                                        time = new Date();
                                sb.appendEscaped(timeFormat.format(time));
                                sb.appendHtmlConstant("</div>"
                                        + "<div class=\"buddyicon\">");

                                Image avatar = factory.avatarFactory().getAvatarForJid(msg.getFrom() != null 
                                        ? msg.getFrom().getBareJid() : (BareJID) factory.jaxmpp().getProperties().getUserProperty(SessionObject.USER_BARE_JID));
                                avatar.setSize("32px", "32px");
                                sb.appendHtmlConstant(avatar.toString());
                                //sb.appendHtmlConstant("<img src=\"http://vectortuts.s3.amazonaws.com/tuts/57_Shiny_Buddies/preview.jpg\" width=\"32\" height=\"32\" />");

                                sb.appendHtmlConstant("</div>"
                                        + "<div class=\"message\"><p>");
                                //body = body.replaceAll("\n", "<br/>");
                                sb.appendEscaped(body);
                                sb.appendHtmlConstant("</p></div><div id=\"insert-" + jid + "\"></div>");
                                sb.appendHtmlConstant("</div>");
                        }

                        HTML html = new HTML(sb.toSafeHtml());
                        html.setWordWrap(true);
                        if (!sameSender) {
                                Element insert = log.getElementById("insert-" + jid);
                                if (insert != null) {
                                        insert.removeFromParent();
                                }
                                log.add(html, "Chat-" + jid);
                        }
                        else {
                                log.addAndReplaceElement(html, "insert-" + jid);
                        }

                        lastFrom = msg.getFrom() == null ? Sender.local : Sender.remote;

                        scroll.scrollToBottom();
                }
                catch (XMLException ex) {
                        Logger.getLogger("LogPanel").log(Level.WARNING, "Exception processing message", ex);
                }
        }

        public void activated() {
                scroll.scrollToBottom();
        }
        
//    private VerticalPanel log = new VerticalPanel();
//
//    public LogPanel(String name) {
//        this.name = name;
//        scroll = new ScrollPanel(log);
//        initWidget(scroll);
//    }
//
//    public void appendMessage(Message msg) {
//        String body = msg.getBody();
//
//        if (msg == null)
//            return;
//
//        SafeHtmlBuilder sb = new SafeHtmlBuilder();
//        sb.appendHtmlConstant("<div style=\"width: 100%;\"><p>");
//        sb.appendHtmlConstant("<span style=\"font-weight: bold;\">");
//
//        String from = msg.getFrom() == null ? "Me" : name;
//        sb.appendEscaped(from);
//
//        sb.appendHtmlConstant(": </span>");
//
//        body = body.replaceAll("\n", "<br/>");
//
//        sb.appendEscaped(body);
//        sb.appendHtmlConstant("</p></div>");
//
//        HTML html = new HTML(sb.toSafeHtml());
//        html.setWordWrap(true);
//        log.add(html);
//
//        scroll.scrollToBottom();
//    }
}
