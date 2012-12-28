/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.auth;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import tigase.sure.web.site.client.ClientFactory;

/**
 *
 * @author andrzej
 */
public class AuthActivity extends AbstractActivity {

        private final ClientFactory factory;
        
        public AuthActivity(AuthPlace place, ClientFactory factory_) {
                this.factory = factory_;
        }
        
        public void start(AcceptsOneWidget panel, EventBus eventBus) {
                panel.setWidget(factory.authView().asWidget());
        }
        
}
