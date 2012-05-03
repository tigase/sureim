/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

/**
 *
 * @author andrzej
 */
public class ResizablePanel extends AbsolutePanel implements RequiresResize {

        public ResizablePanel() {
        }
        
        public void onResize() {
                Style style = this.getElement().getStyle();
                style.setPosition(Position.ABSOLUTE);
                style.setLeft(0, Unit.EM);
                style.setRight(0, Unit.EM);
                style.setTop(0, Unit.EM);
                style.setBottom(0, Unit.EM);
        }
        
}

