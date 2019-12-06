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
package tigase.sure.web.site.client.bookmarks;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.xml.Element;

import java.util.List;

/**
 * @author andrzej
 */
public class BookmarksEvent
		extends Event<BookmarksHandler> {

	public static final Type<BookmarksHandler> TYPE = new Type<BookmarksHandler>();

	private final List<Element> bookmarks;

	public BookmarksEvent(List<Element> bookmarks) {
		this.bookmarks = bookmarks;
	}

	@Override
	public Type<BookmarksHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BookmarksHandler handler) {
		handler.bookmarksChanged(bookmarks);
	}

}
