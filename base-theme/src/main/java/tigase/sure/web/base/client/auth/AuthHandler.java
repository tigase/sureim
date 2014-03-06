/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.auth;

import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule.SaslError;

/**
 *
 * @author andrzej
 */
public interface AuthHandler {
        
        void authenticated(JID jid);
        
        void deauthenticated(String msg, SaslError saslError);
        
}
