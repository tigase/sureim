/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
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
        private Widget sidebarLeft;
        private Widget sidebarRight;
        
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
        
        public void setLeftSidebar(Widget widget) {
                setLeftSidebar(widget, 20);
        }
        
        public void setLeftSidebar(final Widget widget, double size) {
                this.sidebarLeft = widget;
				new AttachEvent.Handler() {
					HandlerRegistration reg = widget.addAttachHandler(this);
					
					@Override
					public void onAttachOrDetach(AttachEvent event) {
						if (event.isAttached()) {
							widget.getElement().addClassName(factory.theme().style().sidebarLeft());
							reg.removeHandler();
						}
					}
				};				
                panel.addWest(sidebarLeft, size);
        }
        
        public void setRightSidebar(Widget widget) {
                setRightSidebar(widget, 20);
        }

        public void setRightSidebar(final Widget widget, double size) {
                this.sidebarRight = widget;
				new AttachEvent.Handler() {
					HandlerRegistration reg = widget.addAttachHandler(this);
					
					@Override
					public void onAttachOrDetach(AttachEvent event) {
						if (event.isAttached()) {
							widget.getElement().addClassName(factory.theme().style().sidebarRight());
							reg.removeHandler();
						}
					}
				};
                panel.addEast(sidebarRight, size);
        }

        public Widget getSidebarLeft() {
                return sidebarLeft;
        }

        public Widget getSidebarRight() {
                return sidebarRight;
        }
}
