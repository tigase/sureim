/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.disco;

import com.google.gwt.user.client.ui.IsWidget;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public interface DiscoView extends IsWidget {

        void refresh();
        
        void discover(JID jid, String node);
        
}
