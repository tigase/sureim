/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import eu.hilow.xode.web.client.chat.ChatPlace;

/**
 *
 * @author andrzej
 */
@WithTokenizers(ChatPlace.Tokenizer.class)
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
        
}
