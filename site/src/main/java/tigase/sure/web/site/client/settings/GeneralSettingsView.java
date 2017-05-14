/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.settings;

import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

import tigase.sure.web.base.client.widgets.View;
import tigase.sure.web.site.client.ClientFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xmpp.modules.registration.InBandRegistrationModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

/**
 *
 * @author andrzej
 */
public class GeneralSettingsView extends Composite implements View {

	private final ClientFactory factory;
	private final VerticalPanel layout;

	public GeneralSettingsView(ClientFactory factory_) {
		this.factory = factory_;
		layout = new VerticalPanel();
		layout.addStyleName("settingsView");

		FlexTable panel = new FlexTable();

		Label label = new Label(factory.i18n().security());
		panel.setWidget(0, 0, label);

		label = new Label(factory.i18n().password());
		panel.setWidget(1, 0, label);
		Anchor anchor = new Anchor(factory.i18n().changePassword());
		panel.setWidget(1, 1, anchor);
		anchor.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				showChangePasswordDlg();
			}

		});

		label = new Label(factory.i18n().account());
		panel.setWidget(2, 0, label);
		anchor = new Anchor(factory.i18n().removeAccount());
		panel.setWidget(2, 1, anchor);
		anchor.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				showRemoveAccountDlg();
			}

		});

		panel.addStyleName("settingsPanel");

		layout.add(panel);

		initWidget(layout);
	}

	private void showChangePasswordDlg() {
		final DialogBox dlg = new DialogBox(true);

		dlg.setStyleName("dialogBox");
		dlg.setTitle(factory.i18n().changePassword());

		final FlexTable table = new FlexTable();
		Label label = new Label(factory.i18n().changePassword());
		label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
		label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		table.setWidget(0, 0, label);
		label = new Label(factory.i18n().newPassword());
		table.setWidget(1, 0, label);
		final TextBox pass1 = new PasswordTextBox();
		table.setWidget(1, 1, pass1);
		label = new Label(factory.i18n().confirmPassword());
		table.setWidget(2, 0, label);
		final TextBox pass2 = new PasswordTextBox();
		table.setWidget(2, 1, pass2);

		Button cancel = new Button(factory.baseI18n().cancel());
		cancel.setStyleName(factory.theme().style().button());
		table.setWidget(3, 0, cancel);
		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dlg.hide();
			}

		});

		Button ok = new Button(factory.baseI18n().confirm());
		ok.setStyleName(factory.theme().style().button());
		ok.addStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().right());
		table.setWidget(3, 1, ok);
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String p1 = pass1.getText();
				String p2 = pass2.getText();

				if (p1 == null || p1.isEmpty() || p2 == null || p2.isEmpty() || !p2.equals(p1)) {
					Label label = new Label("Passwords are empty or different!");
					label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
					table.setWidget(4, 0, label);
					return;
				}

				String username = factory.jaxmpp().getSessionObject().getUserBareJid().getLocalpart();
				String domain = factory.jaxmpp().getSessionObject().getUserBareJid().getDomain();
				final Logger log = Logger.getLogger(SettingsViewImpl.class.getName());
				try {
					factory.jaxmpp().getModulesManager().register(new InBandRegistrationModule());
					InBandRegistrationModule module = factory.jaxmpp().getModulesManager().getModule(InBandRegistrationModule.class);

					factory.jaxmpp().getSessionObject().setProperty(SessionObject.DOMAIN_NAME, domain);

					log.log(Level.FINE, "module: " + module + ", username: " + username
							+ ", domain: " + domain + ", p1: " + p1 + ", p2: " + p2);

					module.register(username, p1, null, null);

					dlg.hide();
				} catch (JaxmppException ex) {
					log.log(Level.SEVERE, "There was an exception: " + ex.getLocalizedMessage(), ex);

				}
				dlg.hide();
			}

		});

		dlg.setWidget(table);

		dlg.show();
		dlg.center();
	}

	private void showRemoveAccountDlg() {
		final DialogBox dlg = new DialogBox(true);

		dlg.setStyleName("dialogBox");
		dlg.setTitle(factory.i18n().removeAccount());

		final FlexTable table = new FlexTable();
		Label label = new Label(factory.i18n().removeAccount());
		label.getElement().getStyle().setFontSize(1.2, Style.Unit.EM);
		label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		table.setWidget(0, 0, label);
		
		table.setText(1, 0, factory.i18n().accountRemovalWarning());
		table.getFlexCellFormatter().setColSpan(1, 0, 2);

		Button cancel = new Button(factory.baseI18n().cancel());
		cancel.setStyleName(factory.theme().style().button());
		table.setWidget(2, 0, cancel);
		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dlg.hide();
			}

		});

		Button ok = new Button(factory.baseI18n().confirm());
		ok.setStyleName(factory.theme().style().button());
		ok.addStyleName(factory.theme().style().buttonDefault());
		ok.addStyleName(factory.theme().style().right());
		table.setWidget(2, 1, ok);
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					factory.jaxmpp().getModulesManager().register(new InBandRegistrationModule());
					factory.jaxmpp().getModule(InBandRegistrationModule.class).removeAccount(new AsyncCallback() {
						@Override
						public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
							throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
						}

						@Override
						public void onSuccess(Stanza responseStanza) throws JaxmppException {
						}

						@Override
						public void onTimeout() throws JaxmppException {
							throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
						}
					});
				} catch (JaxmppException ex) {
					Logger.getLogger(GeneralSettingsView.class.getName()).log(Level.SEVERE, null, ex);
				}
				dlg.hide();
			}

		});

		dlg.setWidget(table);

		dlg.show();
		dlg.center();
	}

	public void update() {
	}

}
