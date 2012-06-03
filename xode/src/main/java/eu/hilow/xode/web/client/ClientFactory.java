/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import eu.hilow.gwt.base.client.auth.AbstractAuthView;
import eu.hilow.xode.web.client.archive.ArchiveView;
import eu.hilow.xode.web.client.auth.AuthView;
import eu.hilow.xode.web.client.chat.ChatView;
import eu.hilow.xode.web.client.disco.DiscoView;
import eu.hilow.xode.web.client.settings.SettingsView;

/**
 *
 * @author andrzej
 */
public interface ClientFactory extends eu.hilow.gwt.base.client.ClientFactory {

        ActionBarFactory actionBarFactory();
        
        ArchiveView archiveView();
        
        AuthView authView();
        
        ChatView chatView();

        DiscoView discoView();
        
        I18n i18n();
        
        PlaceController placeController();

        SettingsView settingsView();
        
}
