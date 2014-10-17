/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.logging.Logger;
import tigase.sure.web.site.client.ClientFactory;

/**
 *
 * @author andrzej
 */
public class StatsActivity extends AbstractActivity {

	private static final Logger log = Logger.getLogger(StatsActivity.class.getName());
			
	private final ClientFactory factory;

	public StatsActivity(StatsPlace place, ClientFactory factory) {
		this.factory = factory;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		final StatsView view = factory.statsView();
		panel.setWidget(view.asWidget());
		ChartJS.inject(new Callback<Void, Exception>() {

			@Override
			public void onFailure(Exception reason) {
				log.severe("failed to load ChartJS source file, ex = " + reason);
			}

			@Override
			public void onSuccess(Void result) {
				view.refresh();
			}

		});
	}
        
}