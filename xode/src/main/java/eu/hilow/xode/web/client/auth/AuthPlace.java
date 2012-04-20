/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.auth;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import eu.hilow.xode.web.client.chat.ChatPlace;

/**
 *
 * @author andrzej
 */
public class AuthPlace extends Place {
        
        public AuthPlace() {
                
        }
        
        public static class Tokenizer implements PlaceTokenizer<ChatPlace> {

                public ChatPlace getPlace(String token) {
                        return new ChatPlace();
                }

                public String getToken(ChatPlace place) {
                        return null;
                }
                
        }
                
}
