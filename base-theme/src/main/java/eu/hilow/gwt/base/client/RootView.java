/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

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
                
                Dictionary root = Dictionary.getDictionary("root");
                String navStr = root.get("navigation");
                JSONArray navArr = (JSONArray) JSONParser.parseLenient(navStr);
                
                for (int i=0; i < navArr.size(); i++) {
                        JSONObject obj = (JSONObject) navArr.get(i);
                        
                        final String url = ((JSONString) obj.get("url")).stringValue();
                        final String label = ((JSONString) obj.get("label")).stringValue();
                        boolean active = ((JSONBoolean) obj.get("active")).booleanValue();
                        
                        Label link = new Label(label);
                        link.setStyleName(style.navigationBarItem());
                        link.addClickHandler(new ClickHandler() {

                                public void onClick(ClickEvent event) {
                                        Window.open(url, label, null);
                                }
                                
                        });
                        
                        link.addStyleName(style.left());
                        
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
                anchor = new Anchor(factory.baseI18n().termsOfService(), "terms.txt");                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                anchor.setTarget("_blank");
                footerPanel.add(anchor);
                anchor = new Anchor(factory.baseI18n().privacyPolicy(), "privacy.txt");                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                anchor.setTarget("_blank");
                footerPanel.add(anchor);
                anchor = new Anchor(factory.baseI18n().contactForm(), "mailto:support@tigase.com");                
                anchor.addStyleName(factory.theme().style().footerBarItem());
                anchor.setTarget("_blank");
                footerPanel.add(anchor);
                
                dockLayout.addSouth(footerPanel, 1.1);
                
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
        
}
