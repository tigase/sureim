/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.settings;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 * @author andrzej
 */
public class SettingsPlace extends Place {
        
        public SettingsPlace() {                
        }
        
        public static class Tokenizer implements PlaceTokenizer<SettingsPlace> {

                public SettingsPlace getPlace(String token) {
                        return new SettingsPlace();
                }

                public String getToken(SettingsPlace place) {
                        return null;
                }
                
        }
        
}
