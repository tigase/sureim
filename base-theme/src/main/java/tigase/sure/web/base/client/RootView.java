/*
 * RootView.java
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
package tigase.sure.web.base.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author andrzej
 */
public class RootView extends ResizeComposite {
        
        private final DockLayoutPanel dockLayout;
        private final AbsolutePanel navPanel;
        private final AbsolutePanel footerPanel;
        private Widget centerWidget;
        
        public RootView(ClientFactory factory) {
                
                Style style = factory.theme().style();
                
                dockLayout = new DockLayoutPanel(Unit.EM);

                navPanel = new AbsolutePanel();                
                navPanel.setStyleName(style.navigationBar());
                navPanel.addStyleName("navigationBar");
                
                Dictionary root = Dictionary.getDictionary("root");
				String version = null;
				try {
					version = root.get("version");
				} catch (Throwable ex) {}
                String navStr = root.get("navigation");
                JSONArray navArr = (JSONArray) JSONParser.parseLenient(navStr);
                
                String host = Window.Location.getHostName();
                
                for (int i=0; i < navArr.size(); i++) {
                        JSONObject obj = (JSONObject) navArr.get(i);
                        
                        final String url = ((JSONString) obj.get("url")).stringValue();
                        final String label = ((JSONString) obj.get("label")).stringValue();
                        boolean active = url.contains(host);//((JSONBoolean) obj.get("active")).booleanValue();
                        if (active) {
                                Window.setTitle(label);
                        }
                        
                        Anchor link = new Anchor(label);
                        link.setStyleName(style.navigationBarItem());
                        link.addClickHandler(new ClickHandler() {

                                public void onClick(ClickEvent event) {
                                        Window.open(url, label, null);
                                }
                                
                        });
                        
                        if (obj.containsKey("position") && "right".equals(((JSONString) obj.get("position")).stringValue())) {
                                link.addStyleName(style.right());
                        }
                        else {
                                link.addStyleName(style.left());
                        }
                        
                        if (active) {
                                link.addStyleName(style.navigationBarItemActive());
                        }
                        
                        navPanel.add(link);
                }
                
                dockLayout.addNorth(navPanel, 2.0);
                
                footerPanel = new AbsolutePanel();
                footerPanel.addStyleName(factory.theme().style().footerBar());
                Anchor anchor  = new Anchor("© " + factory.baseI18n().copyright());                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                footerPanel.add(anchor);
				if (version != null && !version.isEmpty()) {
					anchor = new Anchor(factory.baseI18n().version() + " " + version);
					anchor.addStyleName(factory.theme().style().footerBarItem());
					footerPanel.add(anchor);
				}
                anchor = new Anchor(factory.baseI18n().termsOfService(), "terms.txt");                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                anchor.setTarget("_blank");
                footerPanel.add(anchor);
                anchor = new Anchor(factory.baseI18n().privacyPolicy(), "privacy.txt");                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                anchor.setTarget("_blank");
                footerPanel.add(anchor);
//                anchor = new Anchor(factory.baseI18n().contactForm(), root.get("contact-link"));                
//                anchor.addStyleName(factory.theme().style().footerBarItem());
//                anchor.setTarget("_blank");
//                footerPanel.add(anchor);
                anchor = new Anchor(factory.baseI18n().supportForm(), root.get("support-link"));                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                anchor.setTarget("_blank");
                footerPanel.add(anchor);
                
                dockLayout.addSouth(footerPanel, 1.4);
                
                initWidget(dockLayout);
                
        }

        public void setCenter(Widget widget) {
                if (centerWidget != null || widget == null)
                        return;
                
                this.centerWidget = widget;
                dockLayout.add(widget);
        }
        
        public Widget getCenter() {
                return centerWidget;
        }
        
		public AbsolutePanel getNav() { 
			return navPanel;
		}
}
