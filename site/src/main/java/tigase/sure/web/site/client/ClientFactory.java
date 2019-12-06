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
package tigase.sure.web.site.client;

import com.google.gwt.place.shared.PlaceController;
import tigase.sure.web.site.client.archive.ArchiveView;
import tigase.sure.web.site.client.auth.AuthView;
import tigase.sure.web.site.client.bookmarks.BookmarksManager;
import tigase.sure.web.site.client.chat.ChatView;
import tigase.sure.web.site.client.disco.DiscoView;
import tigase.sure.web.site.client.management.ManagementView;
import tigase.sure.web.site.client.pubsub.PubSubPublishView;
import tigase.sure.web.site.client.settings.SettingsView;
import tigase.sure.web.site.client.stats.StatsView;

/**
 * @author andrzej
 */
public interface ClientFactory
		extends tigase.sure.web.base.client.ClientFactory {

	ActionBarFactory actionBarFactory();

	ArchiveView archiveView();

	AuthView authView();

	BookmarksManager bookmarksManager();

	ChatView chatView();

	DiscoView discoView();

	I18n i18n();

	ManagementView managementView();

	PlaceController placeController();

	PubSubPublishView pubSubPublishView();

	SettingsView settingsView();

	StatsView statsView();

}
