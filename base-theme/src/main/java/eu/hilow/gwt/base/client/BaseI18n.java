/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

import com.google.gwt.i18n.client.Constants;

/**
 *
 * @author andrzej
 */
public interface BaseI18n extends Constants {
        
        @DefaultStringValue("Authenticate")
        String authenticate();
        
        @DefaultStringValue("XMPP ID")
        String jid();
        
        @DefaultStringValue("Password")
        String password();
        
        @DefaultStringValue("Tigase")
        String copyright();
        
        @DefaultStringValue("Terms of Service")
        String termsOfService();
        
        @DefaultStringValue("Privacy Policy")
        String privacyPolicy();
        
        @DefaultStringValue("Contact")
        String contactForm();
}
