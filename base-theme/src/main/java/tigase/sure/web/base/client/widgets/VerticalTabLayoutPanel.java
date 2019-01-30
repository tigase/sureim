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
package tigase.sure.web.base.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;
import tigase.sure.web.base.client.ClientFactory;
import tigase.sure.web.base.client.ResizablePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class VerticalTabLayoutPanel
		extends ResizeComposite {

	private static final Logger log = Logger.getLogger("VerticalTabLayoutPanel");

	;
	private final ResizablePanel content;
	private final ClientFactory factory;
	private final DockLayoutPanel layout;
	private final VerticalPanel sidebar;
	private final List<View> views;

	public VerticalTabLayoutPanel(ClientFactory factory, Style.Float align, double size, Unit unit) {
		this.factory = factory;

		views = new ArrayList<View>();

		layout = new DockLayoutPanel(unit);

		sidebar = new VerticalPanel();
		sidebar.addStyleName(factory.theme().verticalTabPanelStyles().verticalTabLayoutPanelTabs());
		if (align == Style.Float.LEFT) {
			layout.addWest(sidebar, size);
		} else {
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
		for (int i = 0; i < count; i++) {
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

	public interface VerticalTabLayoutStyle
			extends CssResource {

		String verticalTabLayoutPanelTabs();

		String verticalTabLayoutPanelTab();

		String verticalTabLayoutPanelTabSelected();

	}
}
