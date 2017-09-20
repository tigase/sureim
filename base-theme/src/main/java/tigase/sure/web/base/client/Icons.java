/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author andrzej
 */
public interface Icons extends ClientBundle {
        
        @Source("icons/1-navigation-accept.png")
        ImageResource navigationAccept();
        
        @Source("icons/1-navigation-back.png")
        ImageResource navigationBack();

        @Source("icons/1-navigation-cancel.png")
        ImageResource navigationCancel();

        @Source("icons/1-navigation-forward.png")
        ImageResource navigationForward();
        
        @Source("icons/1-navigation-next-item.png")
        ImageResource navigationNextItem();

        @Source("icons/1-navigation-previous-item.png")
        ImageResource navigationPreviousItem();

        @Source("icons/1-navigation-refresh.png")
        ImageResource navigationRefresh();
        
        @Source("icons/6-social-person.png")
        ImageResource socialPerson();
        
        @Source("icons/status-available.png")
        ImageResource statusAvailable();
        
        @Source("icons/status-away.png")
        ImageResource statusAway();
        
        @Source("icons/status-busy.png")
        ImageResource statusBusy();
        
        @Source("icons/status-unavailable.png")
        ImageResource statusUnavailable();
        
        @Source("icons/2-action-settings.png")
        ImageResource settings();
        
        @Source("icons/4-collections-cloud.png")
        ImageResource collectionsCloud();

        @Source("icons/5-content-import-export.png")
        ImageResource gateway();
        
        @Source("icons/6-social-add-person.png")
        ImageResource addPerson();
        
        @Source("icons/6-social-cc-bcc.png")
        ImageResource muc();
        
        @Source("icons/4-collections-labels.png")
        ImageResource bookmarks();
        
        @Source("icons/10-device-access-mic.png")
        ImageResource microfoneOn();
                
        @Source("icons/10-device-access-mic-muted.png")
        ImageResource microfoneOff();
		
		@Source("icons/6-social-share.png")
		ImageResource socialShare();
        
}
