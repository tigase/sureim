/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.auth;

import tigase.jaxmpp.core.client.xmpp.modules.auth.SaslModule.SaslError;

/**
 *
 * @author andrzej
 */
public class AuthFailureEvent extends AuthEvent {
	
	private final SaslError saslError;
	private final String msg;
	
	public AuthFailureEvent(String msg) {
		super(null);
		this.msg = msg;
		this.saslError = null;
	}
	
	public AuthFailureEvent(SaslError error) {
		super(null);
		this.msg = null;
		this.saslError = error;
	}
	
	public SaslError getSaslError() {
		return saslError;
	}
	
	public String getMessage() {
		return msg;
	}
}
