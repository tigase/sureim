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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.*;

/**
 * @author andrzej
 */
public class CommandGroupsList
		implements IsWidget {

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
	private final Map<String, CommandList> panels = new HashMap<String, CommandList>();
	private final StackLayoutPanel stackLayout;
	private Handler selectionHandler;
	private final SelectionChangeEvent.Handler selectionChangeHandler = new SelectionChangeEvent.Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			CommandItem item = (CommandItem) ((SingleSelectionModel) event.getSource()).getSelectedObject();
			if (item != null) {
				selectionHandler.onSelectionChange(item);
			}
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

	private interface CommandList
			extends IsWidget {

		void addCommand(CommandItem item);

		void refresh();

	}

	public interface Handler {

		void onSelectionChange(CommandItem item);
	}

	private class CommandItemCell
			extends AbstractCell<CommandItem>
			implements Cell<CommandItem> {

		private CellList list;

		@Override
		public void render(Context context, CommandItem value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<table>")
					.appendHtmlConstant("<tr>")
					.appendHtmlConstant("<td><b>")
					.appendEscaped(value.getName())
					.appendHtmlConstant("</b></td>")
					.appendHtmlConstant("</tr>")
					.appendHtmlConstant("<tr>")
					.appendHtmlConstant("<td style='font-size:10px;padding-left:10px'>")
					.appendEscaped(value.getJid().toString())
					.appendHtmlConstant("</td>")
					.appendHtmlConstant("</tr>")
					.appendHtmlConstant("</table>");
			//sb.appendHtmlConstant("<b>").appendEscaped(value.getName()).appendHtmlConstant("</b>");
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, CommandItem value, NativeEvent event,
								   ValueUpdater<CommandItem> valueUpdater) {
			if (list != null && list.getSelectionModel() != null) {
				((SingleSelectionModel) list.getSelectionModel()).clear();
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}

		@Override
		public Set<String> getConsumedEvents() {
			return Collections.singleton("click");
		}

		public void setList(CellList list) {
			this.list = list;
		}
	}

	private class CommandList1
			implements CommandList {

		private final List<CommandItem> commands = new ArrayList<CommandItem>();
		private final FlowPanel panel = new tigase.sure.web.base.client.widgets.FlowPanel();
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

	private class CommandList2
			implements CommandList {

		private final List<CommandItem> commands = new ArrayList<CommandItem>();
		private final CellList<CommandItem> list;
		private final ListDataProvider<CommandItem> provider;
		private boolean nameCollide = false;

		public CommandList2() {
			provider = new ListDataProvider<CommandItem>();
			CommandItemCell cell = new CommandItemCell();
			list = new CellList<CommandItem>(cell);
			cell.setList(list);
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
}
