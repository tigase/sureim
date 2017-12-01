/*
 * ActionBarFactory.java
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
package tigase.sure.web.site.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import tigase.sure.web.base.client.ActionBar;

import java.util.*;

/**
 * @author andrzej
 */
public class ActionBarFactory {

	private final List<ActionBar> all = new ArrayList<ActionBar>();
	private final ClientFactory factory;
	private final Map<String, Link> links = new HashMap<String, Link>();

	ActionBarFactory(ClientFactory factory_) {
		this.factory = factory_;
	}

	public ActionBar createActionBar(IsWidget widget) {
		ActionBar bar = new ActionBar(factory);
		all.add(bar);

		List<Link> tmp = new ArrayList<Link>(links.values());
		Collections.sort(tmp);
		for (Link link : tmp) {
			IsWidget w = bar.addLink(link.name, link.handler);
			link.widgets.add(w);
		}

		return bar;
	}

	public void addLink(String id, String name, ClickHandler handler) {
		Link link = new Link();
		link.id = id;
		link.name = name;
		link.handler = handler;
		link.order = links.size();

		links.put(id, link);

		for (ActionBar bar : all) {
			IsWidget w = bar.addLink(name, handler);
			link.widgets.add(w);
		}
	}

	public void setWaitingEvents(String id, int count) {
		Link action = links.get(id);
		//String label = count > 0 ? action.name + " (" + count + ")" : action.name;

		for (IsWidget w : action.widgets) {
			//((Label) w).setText(label);
			if (count > 0) {
				((Anchor) w).getElement().getStyle().setColor("#DD4B39");
			} else {
				((Anchor) w).getElement().getStyle().clearColor();
			}
		}
	}

	public void setVisible(String id, boolean visible) {
		Link action = links.get(id);

		for (IsWidget w : action.widgets) {
			((Anchor) w).setVisible(visible);
		}
	}

	private class Link
			implements Comparable<Link> {

		public ClickHandler handler;
		public String id;
		public String name;
		public int order;
		public List<IsWidget> widgets = new ArrayList<IsWidget>();

		@Override
		public int compareTo(Link o) {
			return order - o.order;
		}

	}
}
