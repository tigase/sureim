/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import eu.hilow.gwt.base.client.ActionBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrzej
 */
public class ActionBarFactory {
        
        private final ClientFactory factory;
        private final List<ActionBar> all = new ArrayList<ActionBar>();        
        private final Map<String,Link> links = new HashMap<String,Link>();        
        
        ActionBarFactory(ClientFactory factory_) {
                this.factory = factory_;
        }
        
        public ActionBar createActionBar(IsWidget widget) {
                ActionBar bar = new ActionBar(factory);
                all.add(bar);
                
                for (Link link : links.values()) {
                        IsWidget w = bar.addLink(link.name, link.handler);
                        link.widgets.add(w);
                }
                
                return bar;
        }
        
        public void addLink(String id, String name, ClickHandler handler) {
                Link link = new Link();
                link.id = id;
                link.name = name;
                link.handler = handler;
                
                links.put(id, link);
                
                for (ActionBar bar : all) {
                        IsWidget w = bar.addLink(name, handler);
                        link.widgets.add(w);
                }
        }
        
        public void setWaitingEvents(String id, int count) {
                Link action = links.get(id);
                //String label = count > 0 ? action.name + " (" + count + ")" : action.name;
                
                for (IsWidget w : action.widgets) {
                        //((Label) w).setText(label);
                        if (count > 0) {
                                ((Label) w).getElement().getStyle().setColor("#DD4B39");
                        }
                        else {
                                ((Label) w).getElement().getStyle().clearColor();
                        }                        
                }
        }
        
        public void setVisible(String id, boolean visible) {
                Link action = links.get(id);
                
                for (IsWidget w : action.widgets) {
                        ((Label) w).setVisible(visible);
                }                
        }
        
        private class Link {
                
                public String id;
                public String name;
                public ClickHandler handler;
                public List<IsWidget> widgets = new ArrayList<IsWidget>();
                
        }
}
