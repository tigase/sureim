/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.bookmarks;

import com.google.web.bindery.event.shared.Event;
import java.util.List;
import tigase.jaxmpp.core.client.xml.Element;

/**
 *
 * @author andrzej
 */
public class BookmarksEvent extends Event<BookmarksHandler> {

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
