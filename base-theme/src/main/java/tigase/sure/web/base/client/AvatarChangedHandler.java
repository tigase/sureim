/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tigase.sure.web.base.client;

import com.google.gwt.event.shared.EventHandler;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public interface AvatarChangedHandler extends EventHandler {

    public void avatarChanged(JID jid);
    
}
