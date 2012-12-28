/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.disco;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import tigase.sure.web.site.client.ClientFactory;

/**
 *
 * @author andrzej
 */
public class DiscoActivity extends AbstractActivity {

        private final ClientFactory factory;
        
        public DiscoActivity(DiscoPlace place, ClientFactory factory) {
                this.factory = factory;
        }
        
        public void start(AcceptsOneWidget panel, EventBus eventBus) {
                DiscoView view = factory.discoView();
                panel.setWidget(view.asWidget());
                view.refresh();                
        }
        
}
