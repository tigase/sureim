/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.management;

import tigase.jaxmpp.core.client.xmpp.modules.disco.DiscoveryModule;

/**
 *
 * @author andrzej
 */
public class CommandItem extends DiscoveryModule.Item implements Comparable<CommandItem> {
	private String group;

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	@Override
	public int compareTo(CommandItem o) {
		return getName().compareTo(o.getName());
	}
	
}
