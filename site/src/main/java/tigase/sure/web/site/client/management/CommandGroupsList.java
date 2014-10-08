/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.management;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
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
	
	public interface Handler {
		void onSelectionChange(CommandItem item);
	}
	
	private final StackLayoutPanel stackLayout;
	private final Map<String,CommandList> panels = new HashMap<String,CommandList>();
	private final SelectionChangeEvent.Handler selectionChangeHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			CommandItem item = (CommandItem) ((SingleSelectionModel) event.getSource()).getSelectedObject();
			if (item != null) {
				selectionHandler.onSelectionChange(item);
			}
		}
	};
	private Handler selectionHandler;
	
	private static final Comparator<CommandItem> commandItemComparator = new Comparator<CommandItem>() {

		@Override
		public int compare(CommandItem o1, CommandItem o2) {
			int val = o1.getName().compareTo(o2.getName());
			if (val == 0) {
				val = o1.getJid().compareTo(o2.getJid());
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
	
	public void setSelectionHandler(Handler handler) {
		this.selectionHandler = handler;
	}
	
	private CommandList ensureGroup(String groupName) {
		CommandList list = panels.get(groupName);
		if (list == null) {
			list = new CommandList2();
			panels.put(groupName, list);
		}
		return list;
	}
	
	private interface CommandList extends IsWidget {
		
		void addCommand(CommandItem item);
		void refresh();
		
	}

	private class CommandItemCell extends AbstractCell<CommandItem> implements Cell<CommandItem> {

		@Override
		public void render(Context context, CommandItem value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<table>").appendHtmlConstant("<tr>")
					.appendHtmlConstant("<td><b>").appendEscaped(value.getName()).appendHtmlConstant("</b></td>")
					.appendHtmlConstant("</tr>")
					.appendHtmlConstant("<tr>").appendHtmlConstant("<td style='font-size:10px;padding-left:10px'>").appendEscaped(value.getJid().toString())
					.appendHtmlConstant("</td>").appendHtmlConstant("</tr>")
					.appendHtmlConstant("</table>");
			//sb.appendHtmlConstant("<b>").appendEscaped(value.getName()).appendHtmlConstant("</b>");
		}
		
	}
	
	private class CommandList2 implements CommandList {

		private final ListDataProvider<CommandItem> provider;
		private final CellList<CommandItem> list;
		private final List<CommandItem> commands = new ArrayList<CommandItem>();
		private boolean nameCollide = false;
		
		public CommandList2() {
			provider = new ListDataProvider<CommandItem>();
            list = new CellList<CommandItem>(new CommandItemCell());
			list.setSelectionModel(new SingleSelectionModel());
			list.getElement().getStyle().setBackgroundColor("transparent");
			list.getElement().getStyle().setOverflowY(Style.Overflow.SCROLL);
            provider.addDataDisplay(list);					
			list.getSelectionModel().addSelectionChangeHandler(selectionChangeHandler);
		}
		
		@Override
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
				provider.getList().remove(toRemove);
			}
			commands.add(item);
			provider.getList().add(item);
		}

		@Override
		public void refresh() {
			if (nameCollide) {
				Collections.sort(provider.getList(), commandItemComparator);
			} else {
				Collections.sort(provider.getList());
			}			
			provider.refresh();
		}

		@Override
		public Widget asWidget() {
			return list;
		}
		
	}
	
	private class CommandList1 implements CommandList {
		
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
