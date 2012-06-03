/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.disco;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 * @author andrzej
 */
public class DiscoPlace extends Place {

        public DiscoPlace() {
        }
        
        public static class Tokenizer implements PlaceTokenizer<DiscoPlace> {

                public DiscoPlace getPlace(String token) {                        
                        return new DiscoPlace();
                }

                public String getToken(DiscoPlace place) {
                        return null;
                }
                
        }
        
}
