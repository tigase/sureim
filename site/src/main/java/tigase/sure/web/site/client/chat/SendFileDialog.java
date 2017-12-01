/*
 * SendFileDialog.java
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
package tigase.sure.web.site.client.chat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.httpfileupload.HttpFileUploadModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.widgets.ProgressBar;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author andrzej
 */
public class SendFileDialog
		extends DialogBox {

	private final ClientFactory factory;
	private final Handler handler;

	private final ProgressBar progressBar;

	public SendFileDialog(ClientFactory factory_, Handler handler) {
		super(true);
		this.factory = factory_;
		this.handler = handler;

		setStyleName("dialogBox");

		FlexTable table = new FlexTable();
		Label label = new Label(factory.i18n().shareWarning());
		table.setWidget(0, 0, label);
		table.getFlexCellFormatter().setColSpan(0, 0, 2);

		FileUpload upload = new FileUpload();

		table.setWidget(1, 0, upload);
		table.getFlexCellFormatter().setColSpan(1, 0, 2);
		upload.getElement().setAttribute("id", "file-upload-field");

		progressBar = new ProgressBar(0, 1);
		progressBar.setWidth("100%");
		progressBar.setHeight("3em");
		table.setWidget(2, 0, progressBar);
		table.getFlexCellFormatter().setColSpan(2, 0, 2);
		progressBar.setVisible(false);

		Button cancel = new Button(factory.baseI18n().cancel());
		cancel.setStyleName(factory.theme().style().button());
		cancel.addStyleName(factory.theme().style().left());
		table.setWidget(3, 0, cancel);
		cancel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				hide();
			}
		});

		Button join = new Button(factory.i18n().share());
		join.setStyleName(factory.theme().style().button());
		join.addStyleName(factory.theme().style().buttonDefault());
		join.addStyleName(factory.theme().style().right());
		table.setWidget(3, 1, join);
		join.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				sendFileClicked();
			}
		});

		setWidget(table);

		getElement().getStyle().setWidth(300, Style.Unit.PX);
	}

	private void sendFileClicked() {
		final JsFile jsFile = getFileObject();
		if (jsFile == null) {
			return;
		}

		try {
			progressBar.setVisible(true);
			factory.jaxmpp()
					.getModule(HttpFileUploadModule.class)
					.findHttpUploadComponents(
							BareJID.bareJIDInstance(factory.jaxmpp().getSessionObject().getUserBareJid().getDomain()),
							new HttpFileUploadModule.DiscoveryResultHandler() {
								@Override
								public void onResult(Map<JID, Long> results) {
									if (results.isEmpty()) {
										Window.alert("HTTP File Upload component is not available at your XMPP server");
										return;
									}

									JID componentJid = null;
									for (Map.Entry<JID, Long> e : results.entrySet()) {
										if (e.getValue() != null && e.getValue() < jsFile.getSize()) {
											continue;
										}
										componentJid = e.getKey();
										break;
									}
									if (componentJid == null) {
										Window.alert("File too big to transfer");
										return;
									}

									try {
										factory.jaxmpp()
												.getModule(HttpFileUploadModule.class)
												.requestUploadSlot(componentJid, jsFile.getName(),
																   (long) jsFile.getSize(), jsFile.getType(),
																   new HttpFileUploadModule.RequestUploadSlotHandler() {
																	   @Override
																	   public void onSuccess(
																			   HttpFileUploadModule.Slot slot)
																			   throws JaxmppException {
																		   GWT.log("allocated slot");
																		   uploadFile(slot, jsFile);
																	   }

																	   @Override
																	   public void onError(Stanza responseStanza,
																						   XMPPException.ErrorCondition error)
																			   throws JaxmppException {
																		   Element text = responseStanza.findChild(
																				   new String[]{"iq", "error", "text"});
																		   if (text != null) {
																			   Window.alert(text.getValue());
																		   } else {
																			   Window.alert(
																					   "It was not possible to upload file due to server error.");
																		   }
																	   }

																	   @Override
																	   public void onTimeout() throws JaxmppException {
																		   Window.alert(
																				   "It was not possible to upload file due to server error.");
																	   }
																   });
									} catch (JaxmppException ex) {
										Logger.getLogger(SendFileDialog.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							});
		} catch (JaxmppException ex) {
			Logger.getLogger(SendFileDialog.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void updateProgress(double value) {
		progressBar.setProgress(value);
	}

	private void fileUploaded(HttpFileUploadModule.Slot slot, JsFile file) {
		handler.fileUploaded(slot, file);
		this.hide();
	}

	private void fileUploadFailed(HttpFileUploadModule.Slot slot, JsFile file) {
		Window.alert("Upload of file " + file.getName() + " failed. Please try again later.");
		progressBar.setVisible(false);
	}

	private native JsFile getFileObject() /*-{
		var fileInputEl = $doc.getElementById('file-upload-field');
		if (fileInputEl.files.length == 0) {
			return null;
		}
		return fileInputEl.files[0];			
	}-*/;

	private native void uploadFile(HttpFileUploadModule.Slot slot, JsFile file) /*-{		
		var that = this;
		var xhr;
		if ($wnd.XMLHttpRequest) {
			xhr = new $wnd.XMLHttpRequest();
		} else {
			try {
				xhr = new $wnd.ActiveXObject('MSXML2.XMLHTTP.3.0');
			} catch (e) {
				xhr = new $wnd.ActiveXObject("Microsoft.XMLHTTP");
			}
		}
			
		xhr.upload.addEventListener("progress", function(e) {
			if (e.lengthComputable) {
				console.log("upload progress: ", e.loaded / e.total);
				that.@tigase.sure.web.site.client.chat.SendFileDialog::updateProgress(D)(e.loaded / e.total);
			}
		});
		xhr.upload.addEventListener("load", function(e) {
			console.log("uploaded");
			that.@tigase.sure.web.site.client.chat.SendFileDialog::fileUploaded(Ltigase/jaxmpp/core/client/xmpp/modules/httpfileupload/HttpFileUploadModule$Slot;Ltigase/sure/web/site/client/chat/SendFileDialog$JsFile;)(slot, file);
		});
		xhr.upload.addEventListener("error", function(e) {
			console.log("upload failed", e);
			that.@tigase.sure.web.site.client.chat.SendFileDialog::fileUploadFailed(Ltigase/jaxmpp/core/client/xmpp/modules/httpfileupload/HttpFileUploadModule$Slot;Ltigase/sure/web/site/client/chat/SendFileDialog$JsFile;)(slot, file);
		});
			
		xhr.open("PUT", slot.@tigase.jaxmpp.core.client.xmpp.modules.httpfileupload.HttpFileUploadModule.Slot::getPutUri()());
		xhr.overrideMimeType(file.type);
		xhr.send(file);
	}-*/;

	public interface Handler {

		public void fileUploaded(HttpFileUploadModule.Slot slot, JsFile file);

	}

	public final static class JsFile
			extends JavaScriptObject {

		protected JsFile() {
		}

		public native String getName() /*-{
			return this.name;
		}-*/;

		public native int getSize() /*-{
			return this.size;
		}-*/;

		public native String getType() /*-{
			return this.type;
		}-*/;

	}
}
