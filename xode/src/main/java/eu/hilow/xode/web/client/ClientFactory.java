/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import eu.hilow.gwt.base.client.auth.AuthView;
import eu.hilow.xode.web.client.chat.ChatView;

/**
 *
 * @author andrzej
 */
public interface ClientFactory extends eu.hilow.gwt.base.client.ClientFactory {

        AuthView authView();
        
        ChatView chatView();
        
        PlaceController placeController();
        
}
