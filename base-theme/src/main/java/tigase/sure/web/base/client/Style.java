/*
 * Style.java
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
package tigase.sure.web.base.client;

import com.google.gwt.resources.client.CssResource;

/**
 * @author andrzej
 */
public interface Style
		extends CssResource {

	String left();

	String right();

	String navigationBar();

	String navigationBarItem();

	String navigationBarItemActive();

	String footerBar();

	String footerBarItem();

	String actionBar();

	String actionBarLink();

	String actionBarSearch();

	String actionBarActionIcon();

	String actionBarHolder();

	String rosterItem();

	String rosterItemName();

	String rosterItemStatus();

	String authPanel();

	String authLabel();

	String authTextBox();

	String authHeader();

	String authButton();

	String button();

	String buttonDefault();

	String buttonDisabled();

	String popupPanel();

	String errorPanelStyle();

	String sidebarLeft();

	String sidebarRight();
}
