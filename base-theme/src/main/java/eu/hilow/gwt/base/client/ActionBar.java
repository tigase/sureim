/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hilow.gwt.base.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author andrzej
 */
public class ActionBar extends ResizeComposite {

        private final ClientFactory factory;
        
        private final AbsolutePanel panel;
        private final AbsolutePanel linkPanel;
        private final AbsolutePanel actionPanel;
        
        private SuggestBox searchBox = null;
        
        public ActionBar(ClientFactory factory) {
                this.factory = factory;
                
                Style style = factory.theme().style();
                
                panel = new ResizablePanel();
                panel.setStyleName(style.actionBar());
                
                linkPanel = new AbsolutePanel();
                linkPanel.setStyleName(style.actionBarLink());
                linkPanel.addStyleName(style.left());
                panel.add(linkPanel);
                                
                actionPanel = new AbsolutePanel();
                actionPanel.setStyleName(style.right());
                panel.add(actionPanel);
        
                initWidget(panel);
        }
        
        public Widget addAction(ImageResource image, ClickHandler handler) {
                final Image img = new Image(image);
                img.addClickHandler(handler);
                img.addStyleName(factory.theme().style().actionBarActionIcon());
                actionPanel.add(img);
                return img;
        }
        
        public IsWidget addLink(String text, ClickHandler handler) {
                Anchor label = new Anchor(text);
                label.addClickHandler(handler);
                label.addStyleName(factory.theme().style().actionBarLink());
                linkPanel.add(label);
                return label;
        }
        
        public void setSearchBox(SuggestBox box) {
                if (searchBox != null) {
                        panel.remove(searchBox);
                }
                searchBox = box;
                if (box != null) {
                        searchBox.setStyleName(factory.theme().style().actionBarSearch());
                        searchBox.addStyleName(factory.theme().style().left());
                        panel.add(searchBox);
                }
        }
        
}
