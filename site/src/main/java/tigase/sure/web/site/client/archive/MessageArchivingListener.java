package tigase.sure.web.site.client.archive;

import tigase.jaxmpp.core.client.xmpp.modules.xep0136.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ChatResultSet;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ResultSet;

public interface MessageArchivingListener {

        void onReceiveCollections(final ResultSet<Chat> rs);
        void onReceiveChat(final ChatResultSet rs);
}
