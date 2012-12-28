/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.bookmarks;

import java.util.List;
import tigase.jaxmpp.core.client.xml.Element;

/**
 *
 * @author andrzej
 */
public interface BookmarksHandler {

        void bookmarksChanged(List<Element> bookmarks);
        
}
