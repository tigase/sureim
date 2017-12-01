/*
 * DiscoItemCell.java
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
package tigase.sure.web.site.client.disco;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import tigase.sure.web.base.client.ClientFactory;
import java.util.Collection;
import java.util.Iterator;
import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule.Identity;

/**
 *
 * @author andrzej
 */
public class DiscoItemCell extends AbstractCell<DiscoItem> implements Cell<DiscoItem> {

        private final ClientFactory factory;
        
        public DiscoItemCell(ClientFactory factory_) {
                this.factory = factory_;
        }

        @Override
        public void render(Context context, DiscoItem value, SafeHtmlBuilder sb) {
                if (value == null) return;
                
                Collection<Identity> identities = value.getIdentities();
                Identity identity = identities != null ? identities.iterator().next() : null;

                Image avatar = null;//factory.avatarFactory().getAvatarForJid(value.getJid().getBareJid());
                if (avatar == null) {
                        if (identities != null) {
                                Iterator<Identity> iter = identities.iterator();
                                while (avatar == null && iter.hasNext()) {
                                        Identity id = iter.next();
                                        if ("gateway".equals(id.getCategory())) {
                                                avatar = new Image(factory.theme().gateway());
                                        }
                                        else if ("conference".equals(id.getCategory())) {
                                                avatar = new Image(factory.theme().muc());
                                        }
                                }
                        }
                        
                        if (avatar == null) {
                                avatar = new Image(factory.theme().collectionsCloud());
                        }
                }
                sb.appendHtmlConstant("<table><tr><td rowspan='2'>");
                avatar.setSize("32px", "32px");
                sb.appendHtmlConstant(avatar.toString());
                sb.appendHtmlConstant("</td>");
                sb.appendHtmlConstant("<td colspan='2'><strong>");
                String name = (identity != null && identity.getName() != null) ? identity.getName() : value.getName();
                if (name == null) {
                        sb.appendEscaped(value.getJid().toString());
                }
                else {
                        sb.appendEscaped(name);
                }
                sb.appendHtmlConstant("</strong></td></tr><tr><td><small>");
                sb.appendEscaped(value.getJid().toString());
                sb.appendHtmlConstant("</small></td><td><small>");
                if (value.getNode() != null) {
                        sb.appendEscaped(value.getNode());
                }
                sb.appendHtmlConstant("</small></td></tr></table>");
        }
        
}
