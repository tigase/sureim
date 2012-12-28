/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.gwt.user.client.ui.Image;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public abstract class AbstractAvatarFactory {
        
        public abstract Image getAvatarForJid(BareJID jid);
        public abstract Image getAvatarForHash(String hash);
        public abstract void setAvatarForJid(BareJID jid, String data);
        public abstract void setAvatarForHash(String hash, String data);
     
        private final ClientFactory factory;        
        
        public AbstractAvatarFactory(ClientFactory factory) {
                this.factory = factory;
        }
        
        protected void notifyAvatarChange(BareJID jid) {                
                factory.eventBus().fireEvent(new AvatarChangedEvent(JID.jidInstance(jid)));
        }
}
