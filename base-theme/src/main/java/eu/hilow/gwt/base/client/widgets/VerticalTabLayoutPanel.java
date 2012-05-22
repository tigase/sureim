/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;
import eu.hilow.gwt.base.client.ClientFactory;
import eu.hilow.gwt.base.client.ResizablePanel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author andrzej
 */
public class VerticalTabLayoutPanel extends ResizeComposite {

        public interface VerticalTabLayoutStyle extends CssResource {
                
                String verticalTabLayoutPanelTabs();                
                String verticalTabLayoutPanelTab();
                String verticalTabLayoutPanelTabSelected();
                
        };
        
        private static final Logger log = Logger.getLogger("VerticalTabLayoutPanel");
        
        private final ClientFactory factory;
        private final DockLayoutPanel layout;
        private final VerticalPanel sidebar;
        private final ResizablePanel content;
        
        private final List<View> views;
        
        public VerticalTabLayoutPanel(ClientFactory factory, Style.Float align, double size, Unit unit) {
                this.factory = factory;
                
                views = new ArrayList<View>();
                
                layout = new DockLayoutPanel(unit);
        
                sidebar = new VerticalPanel();
                sidebar.addStyleName(factory.theme().verticalTabPanelStyles().verticalTabLayoutPanelTabs());
                if (align == Style.Float.LEFT) {
                        layout.addWest(sidebar, size);
                }                
                else {
                        layout.addEast(sidebar, size);
                }
                
                content = new ResizablePanel();
                layout.add(content);
                
                initWidget(layout);
        }
        
        public void add(final View content, final Widget label) {
                label.addStyleName(factory.theme().verticalTabPanelStyles().verticalTabLayoutPanelTab());
                if (sidebar.getWidgetCount() == 0) {
                        label.addStyleName(factory.theme().verticalTabPanelStyles().verticalTabLayoutPanelTabSelected());
                        this.content.add(content);
                }
                sidebar.add(label);
                if (label instanceof HasClickHandlers) {
                        ((HasClickHandlers) label).addClickHandler(new ClickHandler() {
                                public void onClick(ClickEvent event) {
                                        itemSelected(sidebar.getWidgetIndex(label));
                                }                                
                        });
                }
                views.add(content);
        }
        
        public void add(View content, String label) {
                Widget labelWidget = new Label(label);
                add(content, labelWidget);
        }
        
        public void itemSelected(int idx) {
                int count = sidebar.getWidgetCount();
                for (int i=0; i<count; i++) {
                        Widget w = sidebar.getWidget(i);
                        log.info("deselecting item " + i + " w = " + ((w == null) ? "null" : w.toString()));
                        if (w != null) {
                                w.removeStyleName(factory.theme().verticalTabPanelStyles().verticalTabLayoutPanelTabSelected());
                        }
                }
                
                Widget w = sidebar.getWidget(idx);
                log.info("selecting item " + idx + " w = " + ((w == null) ? "null" : w.toString()));
                if (w != null) {
                        w.addStyleName(factory.theme().verticalTabPanelStyles().verticalTabLayoutPanelTabSelected());                        
                }
                
                this.content.clear();                
                View v = this.views.get(idx);
                if (v != null) {
                        v.update();
                        content.add(v);                        
                }
        }
}
