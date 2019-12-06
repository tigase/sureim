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

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import tigase.sure.web.site.client.archive.ArchivePlace;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatPlace;
import tigase.sure.web.site.client.disco.DiscoPlace;
import tigase.sure.web.site.client.management.ManagementPlace;
import tigase.sure.web.site.client.pubsub.PubSubPublishPlace;
import tigase.sure.web.site.client.settings.SettingsPlace;
import tigase.sure.web.site.client.stats.StatsPlace;

/**
 * @author andrzej
 */
@WithTokenizers({ChatPlace.Tokenizer.class, ArchivePlace.Tokenizer.class, AuthPlace.Tokenizer.class,
				 SettingsPlace.Tokenizer.class, DiscoPlace.Tokenizer.class, ManagementPlace.Tokenizer.class,
				 PubSubPublishPlace.Tokenizer.class, StatsPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper
		extends PlaceHistoryMapper {

}
