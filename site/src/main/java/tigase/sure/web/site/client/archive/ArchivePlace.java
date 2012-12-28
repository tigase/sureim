/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.archive;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 * @author andrzej
 */
public class ArchivePlace extends Place {
        
        public ArchivePlace() {                
        }
        
        public static class Tokenizer implements PlaceTokenizer<ArchivePlace> {

                public ArchivePlace getPlace(String token) {
                        return new ArchivePlace();
                }

                public String getToken(ArchivePlace place) {
                        return null;
                }
                
        }
        
}
