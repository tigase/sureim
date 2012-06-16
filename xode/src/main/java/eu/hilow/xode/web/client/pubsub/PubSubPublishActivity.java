/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.pubsub;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import eu.hilow.xode.web.client.ClientFactory;

/**
 *
 * @author andrzej
 */
public class PubSubPublishActivity extends AbstractActivity {

        private final ClientFactory factory;
        
        public PubSubPublishActivity(PubSubPublishPlace place, ClientFactory factory) {
                this.factory = factory;
        }
        
        public void start(AcceptsOneWidget panel, EventBus eventBus) {
                panel.setWidget(factory.pubSubPublishView().asWidget());
        }
        
}
