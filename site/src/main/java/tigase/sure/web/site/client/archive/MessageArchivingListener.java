package tigase.sure.web.site.client.archive;

import tigase.jaxmpp.ext.client.xmpp.modules.archive.Chat;
import tigase.jaxmpp.ext.client.xmpp.modules.archive.ChatResultSet;
import tigase.jaxmpp.ext.client.xmpp.modules.archive.ResultSet;

public interface MessageArchivingListener {

        void onReceiveCollections(final ResultSet<Chat> rs);
        void onReceiveChat(final ChatResultSet rs);
}
