/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.hilow.jaxmpp.ext.client.xmpp.modules.archive;

import java.util.ArrayList;
import java.util.List;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;

/**
 *
 * @author andrzej
 */
public class ResultSet<T> {

        private String first;
        private String last;
        private int count = 0;
	private List<T> items = new ArrayList<T>();

        public int getCount() {
            return count;
        }

        public String getFirst() {
            return first;
        }

	public List<T> getItems() {
		return items;
	}

        public String getLast() {
            return last;
        }

        void process(Element rsm) throws XMLException {
            Element e = getFirstChild(rsm, "count");
            if (e != null)
                setCount(Integer.valueOf(e.getValue()));

            e = getFirstChild(rsm, "first");
            if (e != null)
                setFirst(e.getValue());
            
            e = getFirstChild(rsm, "last");
            if (e != null)
                setLast(e.getValue());
        }

        void setCount(int count) {
            this.count = count;
        }

        void setFirst(String first) {
            this.first = first;
        }

	void setItems(List<T> items) {
		this.items = items;
	}

        void setLast(String last) {
            this.last = last;
        }
        
        private static Element getFirstChild(Element parent, String name) throws XMLException {
                List<Element> children = parent.getChildren(name);
                if (children == null ||  children.isEmpty())
                        return null;
                
                return children.get(0);
        }
}
