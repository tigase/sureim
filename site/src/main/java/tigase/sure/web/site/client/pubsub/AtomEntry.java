/*
 * AtomEntry.java
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
package tigase.sure.web.site.client.pubsub;

import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.ElementFactory;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.utils.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                Element entry = ElementFactory.create("entry");
                entry.setXMLNS("http://www.w3.org/2005/Atom");

                if (id != null) {
                        Element idEl = ElementFactory.create("id");
                        idEl.setValue(id);
                        entry.addChild(idEl);
                }

                Element title = ElementFactory.create("title");
                title.setValue(this.title);
                entry.addChild(title);

                Element summary = ElementFactory.create("summary");
                entry.addChild(summary);

                Element content = ElementFactory.create("content");
                content.setValue(this.content);
                entry.addChild(content);

                Element author = ElementFactory.create("author");
                Element authorName = ElementFactory.create("name");
                authorName.setValue(this.authorName);
                author.addChild(authorName);
                entry.addChild(author);

                Element updated = ElementFactory.create("updated");
                String updatedStr = formatter.format(this.updated == null ? new Date() : this.updated);
                updated.setValue(updatedStr);
                entry.addChild(updated);
                
                return entry;
        }
        
}
