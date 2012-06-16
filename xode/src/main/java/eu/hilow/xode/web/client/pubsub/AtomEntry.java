/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.pubsub;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.xml.DefaultElement;
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
        
        public AtomEntry() {                
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
        
        public String getId() {
                return id;
        }
        
        public void setId(String id) {
                this.id = id;
                this.itemId = id;
        }
        
        public String getTitle() {
                return title;
        }
        
        public void setTitle(String title) {
                this.title = title;
        }
        
        public String getSummary() {
                return summary;
        }
        
        public void setSummary(String summary) {
                this.summary = summary;
        }
        
        public String getContent() {
                return content;
        }
        
        public void setContent(String content) {
                this.content = content;
        }

        public String getAuthorName() {
                return authorName;
        }
        
        public void setAuthorName(String name) {
                this.authorName = name;
        }
        
        public Date getUpdated() {
                return updated;
        }
        
        public void setUpdated(Date date) {
                this.updated = date;
        }
        
        public Element toElement() throws XMLException {
                Element entry = new DefaultElement("entry");
                entry.setXMLNS("http://www.w3.org/2005/Atom");

                if (id != null) {
                        Element idEl = new DefaultElement("id");
                        idEl.setValue(id);
                        entry.addChild(idEl);
                }

                Element title = new DefaultElement("title");
                title.setValue(this.title);
                entry.addChild(title);

                Element summary = new DefaultElement("summary");
                entry.addChild(summary);

                Element content = new DefaultElement("content");
                content.setValue(this.content);
                entry.addChild(content);

                Element author = new DefaultElement("author");
                Element authorName = new DefaultElement("name");
                authorName.setValue(this.authorName);
                author.addChild(authorName);
                entry.addChild(author);

                Element updated = new DefaultElement("updated");
                String updatedStr = formatter.format(this.updated == null ? new Date() : this.updated);
                updated.setValue(updatedStr);
                entry.addChild(updated);
                
                return entry;
        }
        
}
