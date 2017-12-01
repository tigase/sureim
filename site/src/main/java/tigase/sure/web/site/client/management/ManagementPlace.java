/*
 * ManagementPlace.java
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
package tigase.sure.web.site.client.management;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import tigase.sure.web.site.client.disco.DiscoPlace;

/**
 *
 * @author andrzej
 */
public class ManagementPlace extends Place {

	public ManagementPlace() {
	}

	public static class Tokenizer implements PlaceTokenizer<ManagementPlace> {

		public ManagementPlace getPlace(String token) {
			return new ManagementPlace();
		}

		public String getToken(ManagementPlace place) {
			return null;
		}

	}

}
