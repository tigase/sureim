/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.pubsub;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 * @author andrzej
 */
public class PubSubPublishPlace extends Place {
        
        public PubSubPublishPlace() {                
        }
        
        public static class Tokenizer implements PlaceTokenizer<PubSubPublishPlace> {

                public PubSubPublishPlace getPlace(String token) {
                        return new PubSubPublishPlace();
                }

                public String getToken(PubSubPublishPlace place) {
                        return null;
                }
                
        }
        
}
