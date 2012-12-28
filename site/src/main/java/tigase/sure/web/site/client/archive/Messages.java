package tigase.sure.web.site.client.archive;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;

import tigase.jaxmpp.ext.client.xmpp.modules.archive.Item;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.I18n;

public class Messages extends ResizeComposite {

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

//    private final static TableResources resources = GWT.create(TableResources.class);
    private final DateTimeFormat tf1;

    @UiField(provided=true)
    private final CellTable<Item> table;
    private final SimplePager pager;
    //private MessagesDataProvider provider;

    private final ClientFactory factory;
    private final I18n i18n;
    private final Controller controller;

    private Date date = null;

//    GlassPanel glass = null;
	
    public Messages(ClientFactory factory_, Controller controller) {
        this.factory = factory_;
        this.i18n = factory_.i18n();
        tf1 = DateTimeFormat.getFormat("HH:mm:ss");
        this.controller = controller;
//	glass = new GlassPanelProgressBar();

        table = new CellTable<Item>(50);//new CellTable<Item>(50, resources);
        table.setLoadingIndicator(null);
//        table.setRowStyles(new RowStyles<Item>() {
//            @Override
//            public String getStyleNames(Item row, int rowIndex) {
//               return Item.Type.FROM.equals(row.getType()) ? resources.cellTableStyle().from() : resources.cellTableStyle().to();
//            }
//        });

        TextColumn<Item> dateColumn = new TextColumn<Item>() {
            @Override
            public String getValue(Item object) {
                return tf1.format(object.getDate());
            }
        };
        table.addColumn(dateColumn, i18n.time());
//        table.addColumnStyleName(0, resources.cellTableStyle().time());

        TextColumn<Item> directionColumn = new TextColumn<Item>() {
            @Override
            public String getValue(Item object) {
                return (object.getType() == Item.Type.FROM) ? i18n.from() : i18n.to();
            }
        };
        table.addColumn(directionColumn, i18n.from());
//        table.addColumnStyleName(1, resources.cellTableStyle().direction());

        TextColumn<Item> bodyColumn = new TextColumn<Item>() {
            @Override
            public String getValue(Item object) {
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
	

    @Override
    public void onResize() {
        int height = this.getParent().getOffsetHeight();
        int rows = ((height - 60) / 20);
        if (rows < 0)
            rows = 15;
        table.setPageSize(rows);
    }

    @Override
    public void onAttach() {
        super.onAttach();
        this.onResize();
    }
}
