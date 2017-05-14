/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

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

        @DefaultStringValue("Login")
        String login();
        
        @DefaultStringValue("Domain")
        String domain();
        
        @DefaultStringValue("Password")
        String password();
        
        @DefaultStringValue("2016 Tigase, Inc. All rights reserved")
        String copyright();
		
		@DefaultStringValue("Version:")
		String version();
        
        @DefaultStringValue("Terms of Service")
        String termsOfService();
        
        @DefaultStringValue("Privacy Policy")
        String privacyPolicy();
        
        @DefaultStringValue("Contact")
        String contactForm();

        @DefaultStringValue("Support")
        String supportForm();
        
        @DefaultStringValue("Accept")
        String accept();
        
        @DefaultStringValue("Add")
        String add();
        
        @DefaultStringValue("Modify")
        String modify();
       
        @DefaultStringValue("Delete")
        String delete();

        @DefaultStringValue("Error")
        String error();

        @DefaultStringValue("Success")
        String success();
        
        @DefaultStringValue("Cancel")
        String cancel();

        @DefaultStringValue("Information")
        String info();
        
        @DefaultStringValue("Confirm")
        String confirm();
        
        @DefaultStringValue("Close")
        String close();
        
        @DefaultStringValue("Name")
        String name();
        
        @DefaultStringValue("Group")
        String group();

        @DefaultStringValue("Advanced")
        String advanced();
        
        @DefaultStringValue("Connection URL (Bosh/WebSocket)")
        String connectionUrlBoshWs();
		
		@DefaultStringValue("Next")
		String next();
}
