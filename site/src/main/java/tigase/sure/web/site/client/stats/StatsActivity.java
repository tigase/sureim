/*
 * StatsActivity.java
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
package tigase.sure.web.site.client.stats;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import tigase.sure.web.site.client.ClientFactory;

import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class StatsActivity
		extends AbstractActivity {

	private static final Logger log = Logger.getLogger(StatsActivity.class.getName());

	private final ClientFactory factory;

	public StatsActivity(StatsPlace place, ClientFactory factory) {
		this.factory = factory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		final StatsView view = factory.statsView();
		panel.setWidget(view.asWidget());
		ChartJS.inject(new Callback<Void, Exception>() {

			@Override
			public void onFailure(Exception reason) {
				log.severe("failed to load ChartJS source file, ex = " + reason);
			}

			@Override
			public void onSuccess(Void result) {
				view.refresh();
			}

		});
	}

}