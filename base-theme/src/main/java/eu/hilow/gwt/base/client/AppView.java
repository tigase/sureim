/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author andrzej
 */
public class AppView extends ResizeComposite {
        
        private final ClientFactory factory;
        
        private final DockLayoutPanel panel;
        
        private ActionBar actionBar;
        private Widget center;
        private Widget sidebar;
        
        public AppView(ClientFactory factory) {
                this.factory = factory;
                
                panel = new DockLayoutPanel(Unit.EM);
                                
                initWidget(panel);
        }
        
        public void setActionBar(ActionBar actionBar) {
                this.actionBar = actionBar;
                panel.addNorth(actionBar, 3.0);
        }
        
        public ActionBar getActionBar() {
                return actionBar;
        }
        
        public void setCenter(Widget center) {
                this.center = center;
                panel.add(center);                
        }
        
        public Widget getCenter() {
                return center;
        }

        public void setSidebar(Widget widget) {
                this.sidebar = widget;
                panel.addWest(sidebar, 20);
        }
        
        public Widget getSidebar() {
                return sidebar;
        }
}
