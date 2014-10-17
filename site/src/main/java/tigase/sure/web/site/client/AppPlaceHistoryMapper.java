/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import tigase.sure.web.site.client.archive.ArchivePlace;
import tigase.sure.web.site.client.auth.AuthPlace;
import tigase.sure.web.site.client.chat.ChatPlace;
import tigase.sure.web.site.client.disco.DiscoPlace;
import tigase.sure.web.site.client.management.ManagementPlace;
import tigase.sure.web.site.client.pubsub.PubSubPublishPlace;
import tigase.sure.web.site.client.settings.SettingsPlace;
import tigase.sure.web.site.client.stats.StatsPlace;

/**
 *
 * @author andrzej
 */
@WithTokenizers({ChatPlace.Tokenizer.class,ArchivePlace.Tokenizer.class,AuthPlace.Tokenizer.class,SettingsPlace.Tokenizer.class,DiscoPlace.Tokenizer.class,ManagementPlace.Tokenizer.class,PubSubPublishPlace.Tokenizer.class,StatsPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {      
        
}
