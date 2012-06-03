/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.widgets;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author andrzej
 */
public class FlowPanel extends com.google.gwt.user.client.ui.FlowPanel implements RequiresResize, ProvidesResize {

        public void onResize() {
//                int height = this.getElement().getParentElement().getOffsetHeight();
//                this.setHeight(height+"px");
//                for (Widget child : getChildren()) {
//                        int cheight = child.getElement().getParentElement().getOffsetHeight();
//                        child.setHeight(cheight+"px");
//                        if (child instanceof RequiresResize) {
//                                ((RequiresResize) child).onResize();
//                        }
//                }
//                int height = this.getElement().getParentElement().getOffsetHeight();
//                this.setHeight(height+"px");
                for (Widget child : getChildren()) {
//                        int cheight = child.getElement().getParentElement().getOffsetHeight();
//                        child.setHeight(cheight+"px");
                        if (child instanceof RequiresResize) {
                                ((RequiresResize) child).onResize();
                        }
                }
        }
}
