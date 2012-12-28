/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tigase.sure.web.base.client;

import com.google.web.bindery.event.shared.Event;
import tigase.jaxmpp.core.client.JID;

/**
 *
 * @author andrzej
 */
public class AvatarChangedEvent extends Event<AvatarChangedHandler> {

    public static final Type<AvatarChangedHandler> TYPE = new Type<AvatarChangedHandler>();

    private final JID jid;

    public AvatarChangedEvent(JID jid) {
        this.jid = jid;
    }

    @Override
    public Type<AvatarChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AvatarChangedHandler handler) {
        handler.avatarChanged(jid);
    }

}
