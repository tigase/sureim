/*
 * BookmarksManager.java
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
package tigase.sure.web.site.client.bookmarks;

import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.BookmarksModule;
import tigase.jaxmpp.core.client.xmpp.modules.BookmarksModule.BookmarksAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.site.client.ClientFactory;

import java.util.List;

/**
 * @author andrzej
 */
public class BookmarksManager {

	private final ClientFactory factory;

	private List<Element> bookmarks = null;

	public BookmarksManager(ClientFactory factory) {
		this.factory = factory;
	}

	public void retrieve() throws JaxmppException {
		BookmarksModule module = factory.jaxmpp().getModulesManager().getModule(BookmarksModule.class);
		module.retrieveBookmarks(new BookmarksAsyncCallback() {

			@Override
			public void onBookmarksReceived(List<Element> bookmarks) {
				setBookmarks(bookmarks);
			}

			public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
				setBookmarks(null);
			}

			public void onTimeout() throws JaxmppException {
				setBookmarks(null);
			}

		});
	}

	public List<Element> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List<Element> bookmarks) {
		this.bookmarks = bookmarks;

		factory.eventBus().fireEvent(new BookmarksEvent(bookmarks));
	}

}
