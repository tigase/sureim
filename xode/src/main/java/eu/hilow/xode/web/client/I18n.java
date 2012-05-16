/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.i18n.client.Constants;

/**
 *
 * @author andrzej
 */
public interface I18n extends Constants {
        
        @DefaultStringValue("Chat")
        String chat();
        
        @DefaultStringValue("Archive")
        String archive();
        
        @DefaultStringValue("Time")
        String time();
        
        @DefaultStringValue("From")
        String from();
        
        @DefaultStringValue("To")
        String to();
        
        @DefaultStringValue("Message")
        String message();
        
        // Months
        	@DefaultStringValue("January")
	String january();
	
	@DefaultStringValue("February")
	String february();
	
	@DefaultStringValue("March")
	String march();
	
	@DefaultStringValue("April")
	String april();
	
	@DefaultStringValue("May")
	String may();
	
	@DefaultStringValue("June")
	String june();
	
	@DefaultStringValue("July")
	String july();
	
	@DefaultStringValue("August")
	String august();
	
	@DefaultStringValue("September")
	String september();
	
	@DefaultStringValue("October")
	String october();
	
	@DefaultStringValue("November")
	String november();
	
	@DefaultStringValue("December")
	String december();
        
        // Days - short
	@DefaultStringValue("Mon")
	String mondayShort();
	
	@DefaultStringValue("Tue")
	String tuesdayShort();

	@DefaultStringValue("Wed")
	String wednesdayShort();

	@DefaultStringValue("Thu")
	String thursdayShort();

	@DefaultStringValue("Fri")
	String fridayShort();

	@DefaultStringValue("Sat")
	String saturdayShort();

	@DefaultStringValue("Sun")
	String sundayShort();
        
        @DefaultStringValue("Submitted by")
        String submittedBy();
        
        @DefaultStringValue("on")
        String onDate();
        
        @DefaultStringValue("Sure.IM, Tigase.IM, Jabber.ME and more...")
        String authViewHeaderTitle();
        
        @DefaultStringValue("Add contact")
        String addContact();
        
        @DefaultStringValue("Modify contact")
        String modifyContact();               
        
        @DefaultStringValue("Subscription request")
        String subscriptionRequestContact();
        
        @DefaultStringValue("from user")
        String subscriptionRequestMessageContact();
}
