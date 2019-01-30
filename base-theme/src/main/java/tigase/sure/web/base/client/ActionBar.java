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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;

/**
 * @author andrzej
 */
public class ActionBar
		extends ResizeComposite {

	private final AbsolutePanel actionPanel;
	private final ClientFactory factory;
	private final AbsolutePanel linkPanel;
	private final AbsolutePanel panel;
	private SuggestBox searchBox = null;

	public ActionBar(ClientFactory factory) {
		this.factory = factory;

		Style style = factory.theme().style();

		panel = new ResizablePanel();
		panel.setStyleName(style.actionBar());

		linkPanel = new AbsolutePanel();
		linkPanel.setStyleName(style.actionBarLink());
		linkPanel.addStyleName(style.left());
		panel.add(linkPanel);

		actionPanel = new AbsolutePanel();
		actionPanel.setStyleName(style.right());
		panel.add(actionPanel);

		initWidget(panel);
	}

	public Widget addAction(ImageResource image, ClickHandler handler) {
		final Image img = new Image(image);
		img.addClickHandler(handler);
		img.addStyleName(factory.theme().style().actionBarActionIcon());
		actionPanel.add(img);
		return img;
	}

	public IsWidget addLink(String text, ClickHandler handler) {
		Anchor label = new Anchor(text);
		label.addClickHandler(handler);
		label.addStyleName(factory.theme().style().actionBarLink());
		linkPanel.add(label);
		return label;
	}

	public void setSearchBox(SuggestBox box) {
		if (searchBox != null) {
			panel.remove(searchBox);
		}
		searchBox = box;
		if (box != null) {
			searchBox.setStyleName(factory.theme().style().actionBarSearch());
			searchBox.addStyleName(factory.theme().style().left());
			panel.add(searchBox);
		}
	}

	@Override
	public void onAttach() {
		super.onAttach();
		this.getElement().getParentElement().addClassName(factory.theme().style().actionBarHolder());
	}

}
