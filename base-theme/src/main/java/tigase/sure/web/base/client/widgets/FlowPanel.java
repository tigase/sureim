/*
 * FlowPanel.java
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
package tigase.sure.web.base.client.widgets;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author andrzej
 */
public class FlowPanel
		extends com.google.gwt.user.client.ui.FlowPanel
		implements RequiresResize, ProvidesResize {

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
