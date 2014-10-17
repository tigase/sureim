/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import tigase.sure.web.base.client.auth.AbstractAuthView;
import tigase.sure.web.site.client.archive.ArchiveView;
import tigase.sure.web.site.client.auth.AuthView;
import tigase.sure.web.site.client.bookmarks.BookmarksManager;
import tigase.sure.web.site.client.chat.ChatView;
import tigase.sure.web.site.client.disco.DiscoView;
import tigase.sure.web.site.client.management.ManagementView;
import tigase.sure.web.site.client.pubsub.PubSubPublishView;
import tigase.sure.web.site.client.settings.SettingsView;
import tigase.sure.web.site.client.stats.StatsView;

/**
 *
 * @author andrzej
 */
public interface ClientFactory extends tigase.sure.web.base.client.ClientFactory {

        ActionBarFactory actionBarFactory();
        
        ArchiveView archiveView();
        
        AuthView authView();
   
        BookmarksManager bookmarksManager();
        
        ChatView chatView();

        DiscoView discoView();
        
        I18n i18n();
		
		ManagementView managementView();
        
        PlaceController placeController();

        PubSubPublishView pubSubPublishView();
        
        SettingsView settingsView();
		
		StatsView statsView();
        
}
