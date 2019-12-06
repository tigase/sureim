/*
 * Sure.IM site - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.sure.web.site.client.settings;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ResizeComposite;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;
import tigase.sure.web.base.client.AppView;
import tigase.sure.web.base.client.widgets.VerticalTabLayoutPanel;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.events.ServerFeaturesChangedEvent;
import tigase.sure.web.site.client.events.ServerFeaturesChangedHandler;

import java.util.Collection;

/**
 * @author andrzej
 */
public class SettingsViewImpl
		extends ResizeComposite
		implements SettingsView {

	private final ClientFactory factory;
	private final PersonalInformationView personalInformation;
	private Collection<String> features;
	private final ServerFeaturesChangedHandler serverFeaturesChangedHandler = new ServerFeaturesChangedHandler() {

		public void serverFeaturesChanged(Collection<DiscoveryModule.Identity> identities,
										  Collection<String> features_) {
			features = features_;
		}

	};

//        private final ToogleButton messageArchivingButton;
	private GeneralSettingsView generalSettings;
	private VerticalTabLayoutPanel layout;

	public SettingsViewImpl(ClientFactory factory_) {
		factory = factory_;

		AppView appView = new AppView(factory);
		appView.setActionBar(factory.actionBarFactory().createActionBar(this));

		layout = new VerticalTabLayoutPanel(factory, Style.Float.LEFT, 20, Unit.EM);

		generalSettings = new GeneralSettingsView(factory);

		layout.add(generalSettings, factory.i18n().generalSettings());

		personalInformation = new PersonalInformationView(factory);
		layout.add(personalInformation, factory.i18n().personalInformation());

		layout.itemSelected(0);

//                messageArchivingButton = new ToogleButton("Enabled", "Disabled");
//                layout.add(messageArchivingButton);

		appView.setCenter(layout);

		factory.eventBus().addHandler(ServerFeaturesChangedEvent.TYPE, serverFeaturesChangedHandler);

		initWidget(appView);
	}

	public void refreshItems() {

//                messageArchivingButton.setDisabled(features.contains("urn:xmpp:archive:auto"));

	}

}
