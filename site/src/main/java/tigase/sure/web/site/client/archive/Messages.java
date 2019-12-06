/*
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
package tigase.sure.web.site.client.archive;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ChatItem;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.I18n;

import java.util.Date;

public class Messages
		extends Composite {

//    interface TableResources extends CellTable.Resources {
//        @Source({CellTable.Style.DEFAULT_CSS,"Messages.css"})
//        TableStyle cellTableStyle();
//    }
//
//    interface TableStyle extends CellTable.Style {
//        String time();
//        String direction();
//
//        String from();
//        String to();        
//    }

	private final Controller controller;
	private final ClientFactory factory;
	private final I18n i18n;
	//private MessagesDataProvider provider;
	private final SimplePager pager;
	@UiField(provided = true)
	private final CellTable<ChatItem> table;
	//    private final static TableResources resources = GWT.create(TableResources.class);
	private final DateTimeFormat tf1;
	private Date date = null;

//    GlassPanel glass = null;

	public Messages(ClientFactory factory_, Controller controller) {
		this.factory = factory_;
		this.i18n = factory_.i18n();
		tf1 = DateTimeFormat.getFormat("HH:mm:ss");
		this.controller = controller;
//	glass = new GlassPanelProgressBar();

		table = new CellTable<ChatItem>(50);//new CellTable<Item>(50, resources);
		table.setLoadingIndicator(null);
//        table.setRowStyles(new RowStyles<Item>() {
//            @Override
//            public String getStyleNames(Item row, int rowIndex) {
//               return Item.Type.FROM.equals(row.getType()) ? resources.cellTableStyle().from() : resources.cellTableStyle().to();
//            }
//        });

		TextColumn<ChatItem> dateColumn = new TextColumn<ChatItem>() {
			@Override
			public String getValue(ChatItem object) {
				return tf1.format(object.getDate());
			}
		};
		table.addColumn(dateColumn, i18n.time());
//        table.addColumnStyleName(0, resources.cellTableStyle().time());

		TextColumn<ChatItem> directionColumn = new TextColumn<ChatItem>() {
			@Override
			public String getValue(ChatItem object) {
				return (object.getType() == ChatItem.Type.FROM) ? i18n.from() : i18n.to();
			}
		};
		table.addColumn(directionColumn, i18n.from());
//        table.addColumnStyleName(1, resources.cellTableStyle().direction());

		TextColumn<ChatItem> bodyColumn = new TextColumn<ChatItem>() {
			@Override
			public String getValue(ChatItem object) {
				return object.getBody();
			}
		};
		table.addColumn(bodyColumn, i18n.message());

		table.setWidth("100%");
		controller.getMessageProvider().addDataDisplay(table);

		pager = new SimplePager();
		pager.setDisplay(table);

		VerticalPanel vbox = new VerticalPanel();
		vbox.add(table);
		vbox.add(pager);
		vbox.setWidth("100%");
		vbox.setHeight("100%");

		controller.setPageSize(table.getPageSize());

		table.setHeight("100%");

		AbsolutePanel panel = new AbsolutePanel();
		panel.setWidth("100%");
		//panel.addStyleName(aresources.getCss().messagesClass());
		panel.add(vbox);
		initWidget(panel);
		DOM.setElementAttribute(this.getElement(), "id", "chats");
	}

	//@Override
	public void onResize() {
		int height = this.getParent().getOffsetHeight();
		int rows = ((height - 60) / 20);
		if (rows < 0) {
			rows = 15;
		}
		table.setPageSize(rows);
	}

	@Override
	public void onAttach() {
		super.onAttach();
		this.onResize();
	}
}
