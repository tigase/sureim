/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.management;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrzej
 */
public class CommandGroupsList implements IsWidget {
	
	private final StackLayoutPanel stackLayout;
	private final Map<String,CommandList> panels = new HashMap<String,CommandList>();
	
	private static final Comparator<CommandItem> commandItemComparator = new Comparator<CommandItem>() {

		@Override
		public int compare(CommandItem o1, CommandItem o2) {
			int val = o1.getJid().compareTo(o2.getJid());
			if (val == 0) {
				val = o1.getName().compareTo(o2.getName());
			}
			return val;
		}
	};
	
	public CommandGroupsList(Style.Unit unit) {
		stackLayout = new StackLayoutPanel(unit);
	}
	
	public void addCommand(CommandItem item) {
		CommandList list = ensureGroup(item.getGroup());
		list.addCommand(item);
	}

	@Override
	public Widget asWidget() {
		return stackLayout;
	}
	
	public void reset() {
		stackLayout.clear();
		panels.clear();
	}
	
	public void refresh() {
		for (CommandList list : panels.values()) {
			list.refresh();
		}
		stackLayout.clear();
		List<String> groups = new ArrayList<String>(panels.keySet());
		Collections.sort(groups);
		for (String group : groups) {
			CommandList list = panels.get(group);
			Label label = new Label(group);
			stackLayout.add(list.asWidget(), label, 2);
			label.getElement().getStyle().setFontSize(1, Style.Unit.EM);
		}
	}
	
	private CommandList ensureGroup(String groupName) {
		CommandList list = panels.get(groupName);
		if (list == null) {
			list = new CommandList();
			panels.put(groupName, list);
		}
		return list;
	}

	private class CommandList implements IsWidget {
		
		private final FlowPanel panel = new tigase.sure.web.base.client.widgets.FlowPanel();
		private final List<CommandItem> commands = new ArrayList<CommandItem>();
		private boolean nameCollide = false;
		
		public void addCommand(CommandItem item) {
			CommandItem toRemove = null;
			for (CommandItem cmd : commands) {
				if (cmd.getName().equals(item.getName())) {
					if (cmd.getJid().equals(item.getJid())) {
						toRemove = cmd;
					} else {
						nameCollide = true;
					}
				}
			}
			if (toRemove != null) {
				commands.remove(toRemove);
			}
			commands.add(item);
		}
		
		@Override
		public Widget asWidget() {
			return panel;
		}
	
		public void refresh() {
			panel.clear();
			if (nameCollide) {
				Collections.sort(commands, commandItemComparator);
			} else {
				Collections.sort(commands);
			}
			
			for (CommandItem cmd : commands) {
				Label label = new Label(nameCollide ? (cmd.getJid() + ": " + cmd.getName()) : cmd.getName());
				panel.add(label);
			}
		}
	}
}
