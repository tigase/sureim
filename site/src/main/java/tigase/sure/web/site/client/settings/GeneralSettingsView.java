/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.settings;

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
                
                panel.addStyleName("settingsPanel");
                
                layout.add(panel);             
                
                initWidget(layout);
        }

        private void showChangePasswordDlg() {
                final DialogBox dlg = new DialogBox(true);
                
                dlg.setStyleName("dialogBox");
                dlg.setTitle(factory.i18n().changePassword());
                
                FlexTable table = new FlexTable();
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

                        public void onClick(ClickEvent event) {
                                String p1 = pass1.getText();
                                String p2 = pass2.getText();
                                
                                if (p1 == null || p1.isEmpty() || p2 == null || p2.isEmpty())
                                        return;
                                
                                String username = factory.jaxmpp().getSessionObject().getUserBareJid().getLocalpart();
                                try {
                                        factory.jaxmpp().getModulesManager().getModule(InBandRegistrationModule.class).register(username, p1, null, null);
                                        dlg.hide();
                                } catch (JaxmppException ex) {
                                        Logger.getLogger(SettingsViewImpl.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                        
                });
                                
                dlg.setWidget(table);
                
                dlg.show();;
                dlg.center();
        }

        public void update() {
        }
        
}
