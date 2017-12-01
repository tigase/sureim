/*
 * AppActivityMapper.java
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

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import tigase.sure.web.site.client.archive.ArchiveActivity;
import tigase.sure.web.site.client.archive.ArchivePlace;
import tigase.sure.web.site.client.auth.AuthActivity;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatActivity;
import tigase.sure.web.site.client.chat.ChatPlace;
import tigase.sure.web.site.client.disco.DiscoActivity;
import tigase.sure.web.site.client.disco.DiscoPlace;
import tigase.sure.web.site.client.management.ManagementActivity;
import tigase.sure.web.site.client.management.ManagementPlace;
import tigase.sure.web.site.client.pubsub.PubSubPublishActivity;
import tigase.sure.web.site.client.pubsub.PubSubPublishPlace;
import tigase.sure.web.site.client.settings.SettingsActivity;
import tigase.sure.web.site.client.settings.SettingsPlace;
import tigase.sure.web.site.client.stats.StatsActivity;
import tigase.sure.web.site.client.stats.StatsPlace;

/**
 * @author andrzej
 */
public class AppActivityMapper
		implements ActivityMapper {

	private ClientFactory clientFactory;

	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof AuthPlace) {
			return new AuthActivity((AuthPlace) place, clientFactory);
		} else if (place instanceof ChatPlace) {
			return new ChatActivity((ChatPlace) place, clientFactory);
		} else if (place instanceof ArchivePlace) {
			return new ArchiveActivity((ArchivePlace) place, clientFactory);
		} else if (place instanceof DiscoPlace) {
			return new DiscoActivity((DiscoPlace) place, clientFactory);
		} else if (place instanceof SettingsPlace) {
			return new SettingsActivity((SettingsPlace) place, clientFactory);
		} else if (place instanceof ManagementPlace) {
			return new ManagementActivity((ManagementPlace) place, clientFactory);
		} else if (place instanceof PubSubPublishPlace) {
			return new PubSubPublishActivity((PubSubPublishPlace) place, clientFactory);
		} else if (place instanceof StatsPlace) {
			return new StatsActivity((StatsPlace) place, clientFactory);
		}
		return null;
	}

}
