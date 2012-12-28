/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.bookmarks;

import tigase.sure.web.site.client.ClientFactory;
import java.util.ArrayList;
import java.util.List;
import tigase.jaxmpp.core.client.PacketWriter;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.BookmarksModule;
import tigase.jaxmpp.core.client.xmpp.modules.BookmarksModule.BookmarksAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
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
        
        public void setBookmarks(List<Element> bookmarks) {
                this.bookmarks = bookmarks;                
                
                factory.eventBus().fireEvent(new BookmarksEvent(bookmarks));
        }
        
        public List<Element> getBookmarks() {
                return bookmarks;
        }
        
}
