/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.i18n.client.Constants;

/**
 *
 * @author andrzej
 */
public interface I18n extends Constants {
	
	@DefaultStringValue("Logout")
    String logout();
	
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
        
        @DefaultStringValue("Request timed out")
        String requestTimedOut();
        
        @DefaultStringValue("Register account")
        String registerAccount();
        
        @DefaultStringValue("Register")
        String register();
        
		// -- Management
		@DefaultStringValue("Management")
		String management();
		
		@DefaultStringValue("Other")
		String commandUndefinedGroup();
		
		// -- Statistics
		@DefaultStringValue("Statistics")
		String statistics();
		
        // -- Settings 
        // ---- General
        @DefaultStringValue("General")
        String generalSettings();
        
        @DefaultStringValue("Change password")
        String changePassword();
        
        @DefaultStringValue("Password")
        String password();
        
        @DefaultStringValue("Security")
        String security();
        
        @DefaultStringValue("New password")
        String newPassword();
        
        @DefaultStringValue("Confirm password")
        String confirmPassword();
        
        // ---- Personal information
        @DefaultStringValue("Personal information")
        String personalInformation();
        
        @DefaultStringValue("Avatar")
        String avatar();
        
        @DefaultStringValue("Full name")
        String fullName();
        
        @DefaultStringValue("Nick")
        String nick();
        
        @DefaultStringValue("Email")
        String email();
        
        @DefaultStringValue("Birthday")
        String birthday();
                
        // -- Discovery
        @DefaultStringValue("Discovery")
        String discovery();

        @DefaultStringValue("Browse")
        String browse();
        
        @DefaultStringValue("Execute command")
        String executeCommand();

        @DefaultStringValue("Available commands")
        String availableCommands();              
        
        // -- MUC
        @DefaultStringValue("Room")
        String room();
        
        @DefaultStringValue("Server")
        String server();
        
        @DefaultStringValue("Join to room")
        String joinRoom();
        
        @DefaultStringValue("Join")
        String join();
}       
