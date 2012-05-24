/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.xode.web.client.settings;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.*;
import eu.hilow.gwt.base.client.widgets.FileInput;
import eu.hilow.gwt.base.client.widgets.View;
import eu.hilow.gwt.base.client.widgets.file.File;
import eu.hilow.gwt.base.client.widgets.file.FileReader;
import eu.hilow.xode.web.client.ClientFactory;
import eu.hilow.xode.web.client.MessageDialog;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCard;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule;
import tigase.jaxmpp.core.client.xmpp.modules.vcard.VCardModule.VCardAsyncCallback;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

/**
 *
 * @author andrzej
 */
public class PersonalInformationView extends ResizeComposite implements View {
        
        private static final Logger log = Logger.getLogger("PersonalInformationView");
        
        private final ClientFactory factory;

        private final FlexTable layout;
        private final Image avatar;
        private final TextBox fullname;
        private final TextBox nick;
        private final TextBox email;
        private final TextBox birthday;
        
        private VCard vcard = null;
        
        public PersonalInformationView(ClientFactory factory) {
                this.factory = factory;
                
                layout = new FlexTable();
                layout.addStyleName("settingsView");
                
                Label label = new Label(factory.i18n().avatar());
                layout.setWidget(0, 0, label);
                
                avatar = new Image();
                avatar.setHeight("160px");                
                avatar.setUrl(factory.theme().socialPerson().getSafeUri());
                layout.setWidget(0, 1, avatar);

                final FileInput avatarFile = new FileInput();
                final Element avatarEl = avatarFile.getElement();
                avatarEl.getStyle().setWidth(100, Style.Unit.PCT);
                layout.getCellFormatter().getElement(0, 1).appendChild(avatarEl);
                //layout.setWidget(0, 0, avatarFile);
                avatarFile.setChangeHandler(new ChangeHandler() {

                        public void onChange(ChangeEvent event) {
                                File file = avatarFile.getFiles().getItem(0);                                
                                FileReader reader = FileReader.newInstance();
                                reader.addLoadEndHandler(new com.google.gwt.user.client.rpc.AsyncCallback<String>() {
                                        
                                        public void onFailure(Throwable caught) {
                                                throw new UnsupportedOperationException("Not supported yet.");
                                        }

                                        public void onSuccess(String result) {
                                                avatar.setUrl(result);
                                        }
                                        
                                });
                                reader.readAsDataURL(file);
                        }
                        
                });                
                
                label = new Label(factory.i18n().fullName());
                layout.setWidget(1, 0, label);
                fullname = new TextBox();
                layout.setWidget(1, 1, fullname);
                
                label = new Label(factory.i18n().nick());
                layout.setWidget(2, 0, label);
                nick = new TextBox();
                layout.setWidget(2, 1, nick);
                
                label = new Label(factory.i18n().email());
                layout.setWidget(3, 0, label);
                email = new TextBox();
                layout.setWidget(3, 1, email);
                
                label = new Label(factory.i18n().birthday());
                layout.setWidget(4, 0, label);
                birthday = new TextBox();
                layout.setWidget(4, 1, birthday);                
                
                Button cancel = new Button(factory.baseI18n().cancel());
                cancel.setStyleName(factory.theme().style().button());
                cancel.addStyleName(factory.theme().style().left());
                layout.setWidget(5, 0, cancel);
                cancel.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                update();
                        }
                });                
                
                Button ok = new Button(factory.baseI18n().confirm());
                ok.setStyleName(factory.theme().style().button());
                ok.addStyleName(factory.theme().style().buttonDefault());
                ok.addStyleName(factory.theme().style().right());
                layout.setWidget(5, 1, ok);
                ok.addClickHandler(new ClickHandler() {

                        public void onClick(ClickEvent event) {
                                try {
                                        publish();
                                }
                                catch (Exception ex) {                                        
                                }
                        }
                });
                
                initWidget(layout);
        }

        public void update() {
                try {
                        if (!factory.jaxmpp().isConnected()) return;
                        
                        BareJID userJid = factory.jaxmpp().getSessionObject().getUserBareJid();
                        VCardModule vcardModule = factory.jaxmpp().getModulesManager().getModule(VCardModule.class);
                        vcardModule.retrieveVCard(JID.jidInstance(userJid), new VCardAsyncCallback() {
                                
                                @Override
                                protected void onVCardReceived(VCard vcard_) throws XMLException {
                                        vcard = vcard_;
                                        if (vcard.getPhotoType() != null && vcard.getPhotoVal() != null) {
                                                avatar.setUrl("data:"+vcard.getPhotoType()+";base64,"+vcard.getPhotoVal());                                                
                                        }
                                        else {
                                                avatar.setUrl(factory.theme().socialPerson().getSafeUri());
                                        }
                                        
                                        fullname.setText(vcard.getFullName());
                                        nick.setText(vcard.getNickName());
                                        email.setText(vcard.getHomeEmail());
                                        birthday.setText(vcard.getBday());
                                }

                                public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }

                                public void onTimeout() throws JaxmppException {
                                        throw new UnsupportedOperationException("Not supported yet.");
                                }
                                
                        });
                } catch (JaxmppException ex) {
                        log.log(Level.SEVERE, null, ex);
                }
        }
        
        public void publish() throws XMLException, JaxmppException {
                if (vcard == null) {
                        vcard = new VCard();
                }
                
                vcard.setFullName(fullname.getText());
                vcard.setNickName(nick.getText());
                vcard.setHomeEmail(email.getText());
                vcard.setBday(birthday.getText());

                if (!factory.theme().socialPerson().getSafeUri().asString().equals(avatar.getUrl())) {
                        String url = avatar.getUrl();
                        int idx = url.indexOf(":") + 1;
                        if (idx < 1) {
                                return;
                        }
                        int idx2 = url.indexOf(",");
                        if (idx2 < 1) {
                                return;
                        }
                        String[] params = url.substring(idx, idx2).split(";");
                        if (log.isLoggable(Level.FINEST)) {
                                log.finest("type = " + params[0] + ", encoding = " + params[1]);
                                log.finest("data = " + url.substring(idx2 + 1));
                        }
                        vcard.setPhotoType(params[0]);
                        vcard.setPhotoVal(url.substring(idx2 + 1));
                }
                
                
                if (!factory.jaxmpp().isConnected()) {
                        return;
                }

                IQ iq = IQ.create();
                iq.setType(StanzaType.set);
                iq.addChild(vcard.makeElement());
                factory.jaxmpp().getWriter().write(iq, new AsyncCallback() {

                        public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), error.getElementName());
                                dlg.show();
                                dlg.center();
                        }

                        public void onSuccess(Stanza responseStanza) throws JaxmppException {
                                update();
                        }

                        public void onTimeout() throws JaxmppException {
                                MessageDialog dlg = new MessageDialog(factory, factory.baseI18n().error(), factory.i18n().requestTimedOut());
                                dlg.show();
                                dlg.center();
                        }
                        
                });
        }
        
}
