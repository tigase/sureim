/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import eu.hilow.xode.web.client.archive.ArchiveActivity;
import eu.hilow.xode.web.client.archive.ArchivePlace;
import eu.hilow.xode.web.client.auth.AuthActivity;
import eu.hilow.xode.web.client.auth.AuthPlace;
import eu.hilow.xode.web.client.chat.ChatActivity;
import eu.hilow.xode.web.client.chat.ChatPlace;
import eu.hilow.xode.web.client.settings.SettingsActivity;
import eu.hilow.xode.web.client.settings.SettingsPlace;

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
        else if (place instanceof ChatPlace) {
                return new ChatActivity((ChatPlace) place, clientFactory);
        }
        else if (place instanceof ArchivePlace) {
                return new ArchiveActivity((ArchivePlace) place, clientFactory);
        }
        else if (place instanceof SettingsPlace) {
                return new SettingsActivity((SettingsPlace) place, clientFactory);
        }
        return null;
    }
    
}
