/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import eu.hilow.xode.web.client.archive.ArchivePlace;
import eu.hilow.xode.web.client.auth.AuthPlace;
import eu.hilow.xode.web.client.chat.ChatPlace;
import eu.hilow.xode.web.client.settings.SettingsPlace;

/**
 *
 * @author andrzej
 */
@WithTokenizers({ChatPlace.Tokenizer.class,ArchivePlace.Tokenizer.class,AuthPlace.Tokenizer.class,SettingsPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {      
        
}
