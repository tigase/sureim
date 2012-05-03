/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.pubsub;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.utils.DateTimeFormat;

/**
 *
 * @author andrzej
 */
public class AtomEntry {

        private static final DateTimeFormat formatter = new DateTimeFormat();
        
        private String itemId;
        
        private String id;
        private String title;
        private String summary;
        private String content;
        
        private String authorName;
        private Date updated;
        
        public AtomEntry(Element e, String itemId) {        
                this.itemId = id;
                
                this.id = getValue(e, "id");
                this.title = getValue(e, "title");
                this.summary = getValue(e, "summary");
                this.content = getValue(e, "content");
                this.updated = getDate(e, "updated");
                
                try {
                        List<Element> authors = e.getChildren("author");
                        if (authors != null && !authors.isEmpty()) {
                                this.authorName = getValue(authors.get(0), "name");
                        }
                } catch (XMLException ex) {
                        Logger.getLogger(AtomEntry.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        public static String getValue(Element e, String tag) {
                try {
                        List<Element> elems = e.getChildren(tag);
                        String result = null;
                        
                        if (elems != null) {
                                result = "";
                                for (Element elem : elems) {
                                        result += elem.getValue();
                                        result += "\n";
                                }
                        }
                        
                        return result;
                } catch (XMLException ex) {
                        return null;
                }
        }
        
        public static Date getDate(Element e, String tag) {
                String val = getValue(e, tag);
                Date date = null;
                if (val != null) {
                        date = formatter.parse(val);
                }
                return date;
        }
        
        public String getTitle() {
                return title;
        }
        
        public String getSummary() {
                return summary;
        }
        
        public String getContent() {
                return content;
        }

        public String getAuthorName() {
                return authorName;
        }
        
        public Date getUpdated() {
                return updated;
        }
}
