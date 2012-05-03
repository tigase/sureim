package eu.hilow.jaxmpp.ext.client.xmpp.modules.archive;

public class ChatResultSet extends ResultSet<Item> {

        private Chat chat = new Chat();

        public Chat getChat() {
            return chat;
        }

        void setChat(Chat chat) {
            this.chat = chat;
        }

}
