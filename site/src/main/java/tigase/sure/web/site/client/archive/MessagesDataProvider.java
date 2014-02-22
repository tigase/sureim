/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.archive;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import tigase.sure.web.site.client.ClientFactory;
import tigase.jaxmpp.ext.client.xmpp.modules.archive.Chat;
import tigase.jaxmpp.ext.client.xmpp.modules.archive.ChatResultSet;
import tigase.jaxmpp.ext.client.xmpp.modules.archive.ResultSet;

/**
 *
 * @author andrzej
 */
public class MessagesDataProvider extends AsyncDataProvider implements MessageArchivingListener {

        private final Controller controller;

        public MessagesDataProvider(Controller controller) {
                this.controller = controller;
                controller.addMessageArchivingListener(this);
        }

        @Override
        protected void onRangeChanged(HasData display) {
                Range range = display.getVisibleRange();

                Integer start = range.getStart();
                if (start == 0) {
                        start = null;
                } else {
                        start--;
                }

                controller.getMessages(null, start);
        }

        @Override
        public void onReceiveChat(ChatResultSet rs) {
                if (rs == null) {
                        updateRowCount(0, true);
                        return;
                }

                Integer first = 0;
                if (rs.getIndex() != null) {
                        first = rs.getIndex();
                }
                updateRowData(first - 1, rs.getItems());
                updateRowCount(rs.getCount(), false);

//        messages.setVisible(true);
//        messages.glass.hide();
        }

//    @Override
//    public void onReceiveSetChat(Packet iq, ChatResultSet rs) {
//        // Not used
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
        @Override
        public void onReceiveCollections(ResultSet<Chat> rs) {
                // Not used
                //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dispose() {
                controller.removeMessageArchivingListener(this);
        }
}
