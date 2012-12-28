/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.chat;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 * @author andrzej
 */
public class ChatPlace extends Place {
        
        public ChatPlace() {                
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
