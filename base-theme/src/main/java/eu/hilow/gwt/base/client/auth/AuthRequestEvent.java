/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client.auth;

import com.google.web.bindery.event.shared.Event;

import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public class AuthRequestEvent extends Event<AuthRequestHandler> {

        public static final Type<AuthRequestHandler> TYPE = new Type<AuthRequestHandler>();
        
        private final JID jid;
        private final String password;
        private final String url;
        
        public AuthRequestEvent(JID j, String p) {
                this.jid = j;
                this.password = p;                
                this.url = null;
        }

        public AuthRequestEvent(JID j, String p, String url) {
                this.jid = j;
                this.password = p;
                this.url = url;
        }
        
        @Override
        public Type<AuthRequestHandler> getAssociatedType() {
                return TYPE;
        }


        @Override
        protected void dispatch(AuthRequestHandler handler) {
                handler.authenticate(jid, password, url);
        }
        
}
