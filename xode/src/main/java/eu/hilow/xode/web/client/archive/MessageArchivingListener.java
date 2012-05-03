package eu.hilow.xode.web.client.archive;

import eu.hilow.jaxmpp.ext.client.xmpp.modules.archive.Chat;
import eu.hilow.jaxmpp.ext.client.xmpp.modules.archive.ChatResultSet;
import eu.hilow.jaxmpp.ext.client.xmpp.modules.archive.ResultSet;

public interface MessageArchivingListener {

        void onReceiveCollections(final ResultSet<Chat> rs);
        void onReceiveChat(final ChatResultSet rs);
}
