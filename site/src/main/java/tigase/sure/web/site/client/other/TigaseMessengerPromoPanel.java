/*
 * Sure.IM site - bootstrap configuration for all Tigase projects
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
package tigase.sure.web.site.client.other;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author andrzej
 */
public class TigaseMessengerPromoPanel
		extends Composite {

	public TigaseMessengerPromoPanel() {

		AbsolutePanel layout = new AbsolutePanel();

		Style style = layout.getElement().getStyle();
		//style.setColor("#333");
		style.setFloat(Style.Float.RIGHT);
		style.setProperty("clear", "right");
		style.setWidth(33, Style.Unit.PCT);
//                style.setHeight(300, Style.Unit.PX);
		style.setMarginLeft(2, Style.Unit.PCT);
		style.setMarginRight(2, Style.Unit.PCT);
		style.setMarginBottom(2, Style.Unit.PCT);
		style.setMarginTop(0, Style.Unit.PCT);

		AbsolutePanel textPanel = new AbsolutePanel();
		textPanel.getElement().getStyle().setColor("#333");

		layout.add(textPanel);

		HeadingElement clients = Document.get().createHElement(1);
		clients.getStyle().setColor("#357AE8");
		clients.getStyle().setProperty("padding", "0% 10%");
		clients.setInnerText("Our clients:");
		textPanel.getElement().appendChild(clients);

		addClient(textPanel, "SiskinIM (iOS/iPhone)", "https://siskin.im/");
		addClient(textPanel, "BeagleIM (macOS)", "https://beagle.im/");
		addClient(textPanel, "StorkIM (Android)", "https://stork.im/");

		initWidget(layout);
	}

	private void addClient(AbsolutePanel textPanel, String name, String link) {
		HeadingElement stork = Document.get().createHElement(2);
		stork.getStyle().setColor("#357AE8");
		stork.getStyle().setProperty("padding", "0% 10%");
		stork.setInnerText(name);
		AnchorElement storkA = Document.get().createAnchorElement();
		storkA.setAttribute("href", link);
		storkA.appendChild(stork);
		textPanel.getElement().appendChild(storkA);
	}

}
