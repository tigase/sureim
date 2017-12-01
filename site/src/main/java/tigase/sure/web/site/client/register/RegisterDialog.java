/*
 * RegisterDialog.java
 *
 * Tigase XMPP Web Client
 * Copyright (C) 2012-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
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
package tigase.sure.web.site.client.register;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.StreamFeaturesModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule;
import tigase.jaxmpp.core.client.xmpp.modules.registration.UnifiedRegistrationForm;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.gwt.client.Jaxmpp;
import tigase.jaxmpp.gwt.client.connectors.BoshConnector;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.MessageDialog;
import tigase.sure.web.site.client.Xode;
import tigase.sure.web.site.client.chat.ChatViewImpl;
import tigase.sure.web.site.client.disco.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class RegisterDialog
		extends DialogBox {

	private static final Logger log = Logger.getLogger("RegisterDialog");

	private final ClientFactory factory;
	private final Form form;
	private final Button ok;
	private DeckPanel deck;
	private Jaxmpp jaxmpp;
	private RegisterHandler regHandler;
	private String selectedDomain = null;

	public RegisterDialog(ClientFactory factory_) {
		super(true);
		factory = factory_;

		setStyleName("dialogBox");
		setTitle(factory.i18n().registerAccount());

		tigase.sure.web.base.client.widgets.FlowPanel panel = new tigase.sure.web.base.client.widgets.FlowPanel();
		panel.setWidth("400px");

		Label label = new Label(factory.i18n().registerAccount());
		label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
		label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		label.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		label.getElement().getStyle().setClear(Style.Clear.BOTH);
		panel.add(label);

		deck = new DeckPanel();
		deck.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		deck.getElement().getStyle().setClear(Style.Clear.BOTH);
		deck.getElement().getStyle().setPaddingBottom(1, Style.Unit.EM);
		deck.getElement().getStyle().setPaddingTop(1, Style.Unit.EM);
		panel.add(deck);

		FlexTable domainSelectionPanel = new FlexTable();
		Label domainNameLabel = new Label(factory.baseI18n().domain());
		domainSelectionPanel.setWidget(0, 0, domainNameLabel);

		Dictionary root = Dictionary.getDictionary("root");
		String domainsStr = root.get("hosted-domains");
		JSONArray domainsArr = (JSONArray) JSONParser.parseLenient(domainsStr);

		final ListBox domainSelector = new ListBox();
		List<String> knownDomains = new ArrayList<>();
		knownDomains.add(Window.Location.getHostName());
		for (int i = 0; i < domainsArr.size(); i++) {
			String serverName = ((JSONString) domainsArr.get(i)).stringValue();
			if (!knownDomains.contains(serverName)) {
				knownDomains.add(serverName);
			}
		}
		for (String serverName : knownDomains) {
			domainSelector.addItem(serverName);
		}
		domainSelector.addItem(factory.i18n().enterCustomValue());
		domainSelector.setSelectedIndex(0);
		domainSelector.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		domainSelectionPanel.setWidget(0, 1, domainSelector);

		final TextBox customDomain = new TextBox();
		customDomain.getElement().getStyle().setWidth(100, Style.Unit.PCT);
		customDomain.setVisible(false);
		customDomain.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectedDomain = customDomain.getValue();
			}
		});
		domainSelectionPanel.setWidget(1, 0, new Label(""));
		domainSelectionPanel.setWidget(1, 1, customDomain);

		selectedDomain = domainSelector.getSelectedValue();
		domainSelector.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectedDomain = domainSelector.getSelectedValue();
				if (factory.i18n().enterCustomValue().equals(selectedDomain)) {
					selectedDomain = null;
					customDomain.setVisible(true);
				} else {
					customDomain.setVisible(false);
				}
			}
		});

		for (int row = 0; row < domainSelectionPanel.getRowCount(); row++) {
			domainSelectionPanel.getFlexCellFormatter().setWidth(row, 0, "40%");
		}

		deck.add(domainSelectionPanel);

		form = new Form(factory_);
		form.setDisplayFieldDescription(false);

		deck.add(form);
		deck.showWidget(0);

		Button cancel = new Button(factory.baseI18n().cancel());
		cancel.setStyleName(factory.theme().style().button());
		cancel.addStyleName(factory.theme().style().left());
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (jaxmpp != null) {
					try {
						jaxmpp.disconnect();
					} catch (JaxmppException ex) {
						// ignoring error
					}
				}
				hide();
			}
		});
		cancel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		cancel.getElement().getStyle().setFloat(Style.Float.LEFT);
		cancel.getElement().getStyle().setClear(Style.Clear.BOTH);
		panel.add(cancel);

		ok = new Button(factory.baseI18n().next());
		ok.setStyleName(factory.theme().style().button());
		ok.addStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().right());
		ok.getElement().getStyle().setDisplay(Style.Display.BLOCK);
		ok.getElement().getStyle().setFloat(Style.Float.RIGHT);
		ok.getElement().getStyle().setClear(Style.Clear.RIGHT);
		panel.add(ok);

		ok.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				okClicked();
			}

		});
		setWidget(panel);
	}

	private void okClicked() {
		disableOkButton();
		String errorMessage = null;
		if (selectedDomain == null || selectedDomain.isEmpty()) {
			errorMessage = "Domain is required";
		}
		if (errorMessage != null) {
			MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), errorMessage);
			dlg.show();
			dlg.center();
			enableOkButton();
			return;
		}

		if (deck.getVisibleWidget() == 0) {
			retrieveRegistrationForm();
		} else {
			registerAccount();
		}
	}

	private void retrieveRegistrationForm() {
		try {
			jaxmpp = new Jaxmpp();
			regHandler = new RegisterHandler();
			jaxmpp.getModulesManager().register(new InBandRegistrationModule());
			final InBandRegistrationModule regModule = jaxmpp.getModulesManager()
					.getModule(InBandRegistrationModule.class);
			jaxmpp.getProperties()
					.setUserProperty(InBandRegistrationModule.IN_BAND_REGISTRATION_MODE_KEY, Boolean.TRUE);
			jaxmpp.getProperties().setUserProperty(SessionObject.SERVER_NAME, selectedDomain);
			jaxmpp.getProperties().setUserProperty(BoshConnector.BOSH_SERVICE_URL_KEY, Xode.getBoshUrl(selectedDomain));

			regModule.addNotSupportedErrorHandler(regHandler);
			regModule.addReceivedErrorHandler(regHandler);
			regModule.addReceivedRequestedFieldsHandler(regHandler);
			regModule.addReceivedTimeoutHandler(regHandler);

			jaxmpp.getModulesManager()
					.getModule(StreamFeaturesModule.class)
					.addStreamFeaturesReceivedHandler(new StreamFeaturesModule.StreamFeaturesReceivedHandler() {

						@Override
						public void onStreamFeaturesReceived(SessionObject sessionObject, Element featuresElement)
								throws JaxmppException {
							Element features = featuresElement;//ElementFactory.create(featuresElement);
							Element e = features.getChildrenNS("mechanisms", "urn:ietf:params:xml:ns:xmpp-sasl");
							if (e != null) {
								features.removeChild(e);
							}
							e = features.getChildrenNS("bind", "urn:ietf:params:xml:ns:xmpp-bind");
							if (e != null) {
								features.removeChild(e);
							}

							sessionObject.setProperty("StreamFeaturesModule#STREAM_FEATURES_ELEMENT", features);

							regModule.start();
						}
					});

			jaxmpp.login();
		} catch (XMLException ex) {
			Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
			enableOkButton();
		} catch (JaxmppException ex) {
			Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
			enableOkButton();
		}
	}

	private void registerAccount() {
		try {
			jaxmpp.getModule(InBandRegistrationModule.class)
					.register((UnifiedRegistrationForm) form.getData(), new AsyncCallback() {
						public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
							String message = null;

							if (error == null) {
								message = "Registration error";
							} else {
								switch (error) {
									case conflict:
										message = "Username not available. Choose another one.";
										break;
									default:
										message = error.name();
										break;
								}
							}

							showError(message, new Runnable() {
								public void run() {
									retrieveRegistrationForm();
								}
							});
						}

						public void onSuccess(Stanza responseStanza) throws JaxmppException {
							String message = "Registration successful";
							MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().success(), message);
							dlg.show();
							dlg.center();
							hide();
							jaxmpp.disconnect();
							enableOkButton();
						}

						public void onTimeout() throws JaxmppException {
							String message = "Server doesn't responses";
							showError(message, null);
						}
					});
		} catch (JaxmppException ex) {
			Logger.getLogger(ChatViewImpl.class.getName()).log(Level.SEVERE, null, ex);
			enableOkButton();
		}
	}

	private void disableOkButton() {
		ok.setEnabled(false);
		ok.removeStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().buttonDisabled());
	}

	private void enableOkButton() {
		ok.setEnabled(true);
		ok.removeStyleName(factory.theme().style().buttonDisabled());
		ok.addStyleName(factory.theme().style().buttonDefault());
	}

	private void showError(String message, Runnable after) throws JaxmppException {
		MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), message);
		dlg.show();
		dlg.center();
		jaxmpp.disconnect();
		enableOkButton();
		if (after != null) {
			after.run();
		}
	}

	private class RegisterHandler
			implements InBandRegistrationModule.NotSupportedErrorHandler,
					   InBandRegistrationModule.ReceivedErrorHandler,
					   InBandRegistrationModule.ReceivedRequestedFieldsHandler,
					   InBandRegistrationModule.ReceivedTimeoutHandler {

		@Override
		public void onNotSupportedError(SessionObject sessionObject) throws JaxmppException {
			showError("Registration not supported", new Runnable() {
				public void run() {
					RegisterDialog.this.hide();
				}
			});
		}

		@Override
		public void onReceivedError(SessionObject sessionObject, IQ responseStanza, ErrorCondition error)
				throws JaxmppException {
			String message = null;
			if (error == null) {
				message = "Registration error";
			} else {
				switch (error) {
					case conflict:
						message = "Username not available. Choose another one.";
						break;
					default:
						message = error.name();
						break;
				}
			}
			if (message != null) {
				showError(message, new Runnable() {
					public void run() {
						retrieveRegistrationForm();
					}
				});
			}
		}

		@Override
		public void onReceivedRequestedFields(SessionObject sessionObject, IQ responseStanza,
											  UnifiedRegistrationForm unifiedRegistrationForm) {
			try {
				form.setData(unifiedRegistrationForm);
				form.setColumnWidth(0, "40%");

				deck.showWidget(1);
			} catch (JaxmppException ex) {
				Logger.getLogger(RegisterDialog.class.getName()).log(Level.SEVERE, null, ex);
			}
			enableOkButton();
		}

		@Override
		public void onReceivedTimeout(SessionObject sessionObject) throws JaxmppException {
			showError("Server doesn't responses", null);
		}
	}
}
