/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import eu.hilow.xode.web.client.auth.AuthActivity;
import eu.hilow.xode.web.client.auth.AuthPlace;
import eu.hilow.xode.web.client.chat.ChatActivity;
import eu.hilow.xode.web.client.chat.ChatPlace;

/**
 *
 * @author andrzej
 */
public class AppActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof AuthPlace) {
                return new AuthActivity((AuthPlace) place, clientFactory);
        }
        else if (place instanceof ChatPlace)
                return new ChatActivity((ChatPlace) place, clientFactory);
        return null;
    }
    
}
