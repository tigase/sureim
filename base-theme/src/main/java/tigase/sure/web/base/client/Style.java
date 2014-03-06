/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client;

import com.google.gwt.resources.client.CssResource;

/**
 *
 * @author andrzej
 */
public interface Style extends CssResource {
        
        String left();        
        String right();
        
        String navigationBar();        
        String navigationBarItem();
        String navigationBarItemActive();
        
        String footerBar();
        String footerBarItem();
        
        String actionBar();
        String actionBarLink();
        String actionBarSearch();
        String actionBarActionIcon();
        
        String rosterItem();
        String rosterItemName();
        String rosterItemStatus();

        String authPanel();
        String authLabel();
        String authTextBox();
        String authHeader();
        String authButton();
               
        String button();
        String buttonDefault();
        String buttonDisabled();
        
        String popupPanel();   
		
		String errorPanelStyle();
}
