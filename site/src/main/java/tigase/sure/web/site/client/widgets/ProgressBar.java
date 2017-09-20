/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author andrzej
 */
public class ProgressBar extends Widget {
    private static final String PERCENT_PATTERN = "#,##0%";
    private static final NumberFormat percentFormat = NumberFormat.getFormat(PERCENT_PATTERN);

    private final Element progress;
    private final Element percentageLabel;
    private double percentage;
    private final double max;

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
