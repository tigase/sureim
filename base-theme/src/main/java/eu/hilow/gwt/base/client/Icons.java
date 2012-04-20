/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

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

        @Source("icons/1-navigation-accept.png")
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
}
