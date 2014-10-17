/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import tigase.sure.web.site.client.archive.ArchiveActivity;
import tigase.sure.web.site.client.archive.ArchivePlace;
import tigase.sure.web.site.client.auth.AuthActivity;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatActivity;
import tigase.sure.web.site.client.chat.ChatPlace;
import tigase.sure.web.site.client.disco.DiscoActivity;
import tigase.sure.web.site.client.disco.DiscoPlace;
import tigase.sure.web.site.client.management.ManagementActivity;
import tigase.sure.web.site.client.management.ManagementPlace;
import tigase.sure.web.site.client.pubsub.PubSubPublishActivity;
import tigase.sure.web.site.client.pubsub.PubSubPublishPlace;
import tigase.sure.web.site.client.settings.SettingsActivity;
import tigase.sure.web.site.client.settings.SettingsPlace;
import tigase.sure.web.site.client.stats.StatsActivity;
import tigase.sure.web.site.client.stats.StatsPlace;

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
        else if (place instanceof DiscoPlace) {
                return new DiscoActivity((DiscoPlace) place, clientFactory);
        }
        else if (place instanceof SettingsPlace) {
                return new SettingsActivity((SettingsPlace) place, clientFactory);
        }
		else if (place instanceof ManagementPlace) {
				return new ManagementActivity((ManagementPlace) place, clientFactory);
		}
        else if (place instanceof PubSubPublishPlace) {
                return new PubSubPublishActivity((PubSubPublishPlace) place, clientFactory);
        } else if (place instanceof StatsPlace) {
				return new StatsActivity((StatsPlace) place, clientFactory);
		}
        return null;
    }
    
}
