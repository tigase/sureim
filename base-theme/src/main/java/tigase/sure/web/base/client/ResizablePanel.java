/**
 * Sure.IM base theme library - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
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
package tigase.sure.web.base.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * @author andrzej
 */
public class ResizablePanel
		extends AbsolutePanel
		implements RequiresResize {

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

