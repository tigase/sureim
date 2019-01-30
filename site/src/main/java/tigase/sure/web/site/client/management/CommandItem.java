/**
 * Sure.IM site - bootstrap configuration for all Tigase projects
 * Copyright (C) 2012 Tigase, Inc. (office@tigase.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
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
package tigase.sure.web.site.client.management;

import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;

/**
 * @author andrzej
 */
public class CommandItem
		extends DiscoveryModule.Item
		implements Comparable<CommandItem> {

	private String group;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public int compareTo(CommandItem o) {
		return getName().compareTo(o.getName());
	}

}
