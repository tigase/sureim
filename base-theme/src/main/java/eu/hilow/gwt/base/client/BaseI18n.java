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
        
        @DefaultStringValue("2012 Tigase, Inc. All rights reserved")
        String copyright();
        
        @DefaultStringValue("Terms of Service")
        String termsOfService();
        
        @DefaultStringValue("Privacy Policy")
        String privacyPolicy();
        
        @DefaultStringValue("Contact")
        String contactForm();
        
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
        
        @DefaultStringValue("Cancel")
        String cancel();
        
        @DefaultStringValue("Confirm")
        String confirm();
        
        @DefaultStringValue("Close")
        String close();
        
        @DefaultStringValue("Name")
        String name();
        
        @DefaultStringValue("Group")
        String group();
        
}
