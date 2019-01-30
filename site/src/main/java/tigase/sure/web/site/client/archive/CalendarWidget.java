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
package tigase.sure.web.site.client.archive;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.ui.*;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ChatResultSet;
import tigase.jaxmpp.core.client.xmpp.modules.xep0136.ResultSet;
import tigase.sure.web.site.client.ClientFactory;
import tigase.sure.web.site.client.I18n;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import eu.hilow.xode.client.ui.Icon;
//import eu.hilow.xode.client.ui.PanelHeader;
//import eu.hilow.xode.client.ui.Panel;

public class CalendarWidget
		extends Composite
		implements ClickHandler, /*
 * MouseOverHandler,
 */ MessageArchivingListener {

	private static final Logger log = Logger.getLogger("Calendar");
	private static final long SEC_MILIS = 1000;
	private static final long MIN_MILIS = 60 * SEC_MILIS;
	private static final long HOUR_MILIS = 60 * MIN_MILIS;
	private static final long DAY_MILIS = 24 * HOUR_MILIS;
	private final CalendarWidget cal;
	private final Controller controller;
	private final ClientFactory factory;
	private final Grid grid;
	private Date date = new Date();
	private I18n i18n;
	private Label label = null;
	private AbsolutePanel outer = new AbsolutePanel();

	public CalendarWidget(ClientFactory factory_, Controller controller) {
		this.factory = factory_;
//                this.resources = resources;
//                this.css = resources.getCss();
		this.i18n = factory.i18n();
		date.setDate(1);
		date.setTime(((long) (date.getTime() / DAY_MILIS)) * DAY_MILIS);
		cal = this;
		this.controller = controller;
		grid = new Grid(8, 7);
//		grid.getRowFormatter().addStyleName(0, css.tableHeaderClass());
		grid.clear(true);
		label = new Label();
//                MainResources mainResources = controller.getFactory().getMainResources();
//		Image prev = new Image(mainResources.goPreviousIcon());
		Image prev = new Image(factory.theme().navigationPreviousItem());
		prev.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				date.setMonth(date.getMonth() - 1);
				update();
			}
		});
//		Image next = new Image(mainResources.goNextIcon());
		Image next = new Image(factory.theme().navigationNextItem());
		next.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				date.setMonth(date.getMonth() + 1);
				update();
			}
		});
		Grid header = new Grid(1, 3);
		header.setWidget(0, 0, prev);
		header.setStyleName("calendar-widget-header");
		header.setWidget(0, 1, label);
		header.setWidget(0, 2, next);

//		header = new PanelHeader(controller.getFactory().getMainCss(), prev, label, next);
		outer.add(header);
		outer.add(grid);
//		repaint();
		controller.addMessageArchivingListener(this);

//                outer.setStyleName(css.sidebarItemClass());
//                grid.setStyleName(css.calendarDaysClass());

		initWidget(outer);

		update();
//		outer.setVisible(false);
		onReceiveCollections(null);
	}

	public void update() {
		//msgs.setVisible(false);
		label.setText(getMonthName(date.getMonth()) + " " + (date.getYear() + 1900));
		//grid.setStyleName("ui-datepicker-calendar");
		grid.setWidth("100%;");
		grid.setCellSpacing(2);

		log.finest("requesting days");
		for (int i = 0; i < 7; i++) {
			grid.setText(0, i, getDayNameShort(i));
		}

		try {
			controller.listCollections(date, null);
		} catch (UmbrellaException e) {
			for (Throwable ex : e.getCauses()) {
				log.log(Level.FINE, ex.getMessage());
			}
		}

	}

	@Override
	public void onClick(ClickEvent event) {
		CellHTML cell = (CellHTML) event.getSource();
		Date date = new Date();
		date.setYear(this.date.getYear());
		date.setMonth(this.date.getMonth());
		date.setDate(cell.getDay());
		date.setTime(((long) (date.getTime() / DAY_MILIS)) * DAY_MILIS);
		controller.getMessages(date, null);
//		cell.getDay();
	}

	//	@Override
//	public void onMouseOver(MouseOverEvent event) {
//		CellHTML cell = (CellHTML)event.getSource();
//		cell.removeStyleName("ui-state-default");
//		cell.addStyleName("ui-state-active");
//	}
	@Override
	public void setVisible(boolean v) {
		super.setVisible(v);
//		if(!v)
//			msgs.setVisible(false);
	}

	@Override
	public void onReceiveCollections(ResultSet<Chat> rs) {
		log.finest("processing days");
		List<Integer> days = new ArrayList<Integer>(rs == null ? 0 : rs.getItems().size());
		if (rs != null) {
			for (Chat chat : rs.getItems()) {
				days.add(chat.getStart().getDate());
			}
		}

		int firstDay = getFirstDay();
		int numOfDays = getDaysInMonth(date.getYear(), date.getMonth());
		int j = 0;
		for (int i = 1; i < 7; i++) {
			for (int k = 0; k < 7; k++, j++) {
				int dnum = j - firstDay;
				if (j <= firstDay || dnum > numOfDays) {
					grid.setText(i, k, String.valueOf(" "));
				} else {
					if (days != null && days.contains(dnum)) {
						HTML html = new CellHTML("<b>" + dnum + "</b>", dnum);
//                            html.addStyleName(css.hasMessagesClass());
//                            html.addStyleName(css.dayClass());
						html.addStyleName("calendar-widget-day");
						html.addStyleName("calendar-widget-day-messages-available");
						html.addClickHandler(cal);
						grid.setWidget(i, k, html);
					} else {
						HTML html = new CellHTML(Integer.toString(dnum), dnum);
						html.addStyleName("calendar-widget-day");
//                            html.addStyleName(css.dayClass());
						grid.setWidget(i, k, html);
					}
				}
			}
		}
		outer.setVisible(true);
		//    chat.getStart()
	}

	@Override
	public void onReceiveChat(ChatResultSet rs) {
		// Not needed
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	private int getDaysInMonth(int year, int month) {
		switch (month) {
			case 1:
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					return 29; // leap year
				} else {
					return 28;
				}
			case 3:
				return 30;
			case 5:
				return 30;
			case 8:
				return 30;
			case 10:
				return 30;
			default:
				return 31;
		}
	}

	private String getMonthName(int i) {
		switch (i) {
			case 0:
				return i18n.january();
			case 1:
				return i18n.february();
			case 2:
				return i18n.march();
			case 3:
				return i18n.april();
			case 4:
				return i18n.may();
			case 5:
				return i18n.june();
			case 6:
				return i18n.july();
			case 7:
				return i18n.august();
			case 8:
				return i18n.september();
			case 9:
				return i18n.october();
			case 10:
				return i18n.november();
			case 11:
				return i18n.december();
			default:
				return "Unknown";
		}
	}

	private String getDayNameShort(int i) {
		switch (i) {
			case 0:
				return i18n.mondayShort();
			case 1:
				return i18n.tuesdayShort();
			case 2:
				return i18n.wednesdayShort();
			case 3:
				return i18n.thursdayShort();
			case 4:
				return i18n.fridayShort();
			case 5:
				return i18n.saturdayShort();
			case 6:
				return i18n.sundayShort();
			default:
				return "Unknown";
		}
	}

	//    @Override
//    public void onReceiveSetChat(Packet iq, ChatResultSet rs) {
//        // Not needed
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
	private int getFirstDay() {
		int firstDay = new Date(date.getYear() + 1900, date.getMonth(), 1).getDay();

		if (firstDay < 6) {
			firstDay += 4;

			if (firstDay > 6) {
				firstDay -= 7;
			}
		} else {
			firstDay = 6;
		}

		return firstDay;
	}

}
