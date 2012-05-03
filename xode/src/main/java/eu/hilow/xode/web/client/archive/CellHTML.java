package eu.hilow.xode.web.client.archive;

import com.google.gwt.user.client.ui.HTML;

public class CellHTML extends HTML {

	private int day;
	
	public CellHTML(String text, int day) {
		super(text);
		this.day = day;
	}
	
	public int getDay() {
		return day;
	}
}
