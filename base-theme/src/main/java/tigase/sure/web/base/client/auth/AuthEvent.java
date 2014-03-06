/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.auth;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public class AuthEvent extends Event<AuthHandler> {

        public static final Type<AuthHandler> TYPE = new Type<AuthHandler>();
        
        private final JID jid;
        
        public AuthEvent(JID jid) {
                this.jid = jid;
        }
        
        @Override
        public Type<AuthHandler> getAssociatedType() {
                return TYPE;
        }

        @Override
        protected void dispatch(AuthHandler handler) {
                if (jid == null) {
					if (this instanceof AuthFailureEvent) {
						AuthFailureEvent e = (AuthFailureEvent) this;
						handler.deauthenticated(e.getMessage(), e.getSaslError());
					}
					else {
						handler.deauthenticated(null, null);
					}
                }
                else {
                        handler.authenticated(jid);
                }
        }

        
}
