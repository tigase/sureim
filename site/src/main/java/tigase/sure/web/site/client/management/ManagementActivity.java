/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.management;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import tigase.sure.web.site.client.ClientFactory;

/**
 *
 * @author andrzej
 */
public class ManagementActivity extends AbstractActivity {

        private final ClientFactory factory;
        
        public ManagementActivity(ManagementPlace place, ClientFactory factory) {
                this.factory = factory;
        }
        
        public void start(AcceptsOneWidget panel, EventBus eventBus) {
                ManagementView view = factory.managementView();
                panel.setWidget(view.asWidget());
                view.refresh();                
        }
        
}