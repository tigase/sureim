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
package tigase.sure.web.site.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author andrzej
 */
public class ProgressBar
		extends Widget {

	private static final String PERCENT_PATTERN = "#,##0%";
	private static final NumberFormat percentFormat = NumberFormat.getFormat(PERCENT_PATTERN);
	private final double max;
	private final Element percentageLabel;
	private final Element progress;
	private double percentage;

	public ProgressBar(double value, double max) {
		assert max != 0;
		this.max = max;

		progress = DOM.createElement("progress");
		progress.setAttribute("max", Double.toString(max));
		progress.setAttribute("value", Double.toString(value));

		percentageLabel = DOM.createElement("span");
		percentage = value / max;
		percentageLabel.setInnerHTML(percentFormat.format(percentage));
		progress.insertFirst(percentageLabel);

		setElement(progress);
	}

	public void setProgress(double value) {
		progress.setAttribute("value", Double.toString(value));
		percentage = value / max;
		percentageLabel.setInnerHTML(percentFormat.format(percentage));
	}

}
