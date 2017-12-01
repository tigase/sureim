/*
 * ChartJS.java
 *
 * Tigase XMPP Web Client
 * Copyright (C) 2012-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
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
package tigase.sure.web.site.client.stats;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SimplePanel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrzej
 */
public class ChartJS extends SimplePanel {
	
	private static boolean injected = false;
	
	private JavaScriptObject chart;
	private CanvasElement canvas;
	private Element legend;
	private Map data;
	
	public static final void inject(final Callback<Void,Exception> callback) {
		if (injected) {
			callback.onSuccess(null);
			return;
		}
		
		Callback<Void,Exception> wrapper = new Callback<Void, Exception>() {

			@Override
			public void onFailure(Exception reason) {
				callback.onFailure(reason);
			}

			@Override
			public void onSuccess(Void result) {
				injected = true;
				callback.onSuccess(result);
			}
		};
		
		ScriptInjector.fromUrl(GWT.getHostPageBaseURL()+"js/Chart.min.js").setRemoveTag(false).setCallback(wrapper).inject();
	}
	
	public ChartJS(Map data) {
		canvas = Document.get().createCanvasElement();
		getElement().appendChild(canvas);
		canvas.getStyle().setFloat(Style.Float.LEFT);
		legend = Document.get().createDivElement();
		legend.getStyle().setFloat(Style.Float.LEFT);
		getElement().appendChild(legend);
		this.data = data;
		sinkEvents(Event.ONCLICK);	
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		JavaScriptObject dataJs = (JavaScriptObject) prepareData(this.data);
		createChart(dataJs);
	}
	
	public native void createChart(JavaScriptObject data) /*-{
		try {
			if (this.@tigase.sure.web.site.client.stats.ChartJS::chart)
				return;
			console.log("creating chart with data", data);
			var canvas = this.@tigase.sure.web.site.client.stats.ChartJS::canvas;
			var ctx = canvas.getContext("2d");
			var chart = new Chart(ctx).Line(data, {
				animationSteps: 6,
				legendTemplate: "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"color:<%=datasets[i].strokeColor%>\"><%if(datasets[i].label){%><%=datasets[i].label%><%}%></span></li><%}%></ul>"
			});
			this.@tigase.sure.web.site.client.stats.ChartJS::chart = chart;
			this.@tigase.sure.web.site.client.stats.ChartJS::legend.innerHTML = chart.generateLegend();
		} catch (ex) {
			console.log('Exception creating chart', data, ex);
		}
	}-*/;
	
	public void setPixelWidth(int width) {
		canvas.setWidth(width);
	}
	
	public void setPixelHeight(int height) {
		canvas.setHeight(height);
	}
	
	@Override
	public void setHeight(String height) {
		canvas.getStyle().setProperty("height", height);
	}
	
	@Override
	public void setWidth(String width) {
		canvas.getStyle().setProperty("width", width);
	}
	
//	public static ChartJS createLineChart(String id, Map data) {
//		ChartJS instance = new ChartJS();
//		JavaScriptObject params = (JavaScriptObject) prepareData(data);
//		instance.createLineChartImpl(id, params);
//		return instance;
//	}
	
//	public native void addToElement(Element parent) /*-{
//		parent.appendChild(this.@tigase.sure.web.site.client.stats.ChartJS::el);
//	}-*/;
//	
//	private native void createLineChartImpl(String id, JavaScriptObject data) /*-{
//		try {
//			var el = document.createElement("canvas");
//			el.setAttribute("id", id);
//			el.setAttribute("width", "90%");
//			el.setAttribute("height", "300px");
//			this.@tigase.sure.web.site.client.stats.ChartJS::el = el;
//			var ctx = el.getContext("2d");
//			var this_ = this;
//			window.setTimeout(function() {
//				try {
//					var chart = new Chart(ctx).Line(data, options);
//					this_.@tigase.sure.web.site.client.stats.ChartJS::chart = chart;
//				} catch (ex) {
//					console.log('Exception creating chart', ctx, ex);
//				}
//			}, 10);
//		} catch (ex) {
//			console.log('Exception creating chart', id, data, ex);
//		}		
//	}-*/;
	
	public native void update() /*-{
		this.@tigase.sure.web.site.client.stats.ChartJS::chart.update();
	}-*/;
	
	public native void addData(JsArrayInteger values, String label) /*-{
		console.log("adding values", values, "with label", label);
		try {
			this.@tigase.sure.web.site.client.stats.ChartJS::chart.addData(values, label);
		} catch (ex) { console.log("exception adding data", ex); }
	}-*/;
	
	public native void removeData() /*-{
		this.@tigase.sure.web.site.client.stats.ChartJS::chart.removeData();
	}-*/;
	
	public static Object prepareData(Object source) {
		if (source instanceof Map) {
			JavaScriptObject map = JavaScriptObject.createObject();
			for (String key : ((Map<String,Object>)source).keySet()) {
				Object sval = ((Map<String,Object>)source).get(key);
				sval = prepareData(sval);
				if (sval instanceof Integer) {
					mapPut(map, key, ((Integer) sval).intValue());
				} else {
					mapPut(map, key, sval);
				}
			}
			return map;
		} else if (source instanceof List) {
			JavaScriptObject arr = JsArray.createArray();
			for (Object o : ((List) source)) {
				o = prepareData(o);
				if (o instanceof Integer) {
					arrayPush(arr, ((Integer) o).intValue());
				} else {
					arrayPush(arr, o);
				}
			}
			return arr;
		} else {
			return source;
		}
	}

	private static native void arrayPush(JavaScriptObject arr, int val) /*-{
		arr.push(val);
	}-*/;
	
	private static native void arrayPush(JavaScriptObject arr, Object val) /*-{
		arr.push(val);
	}-*/;
	
	private static native void mapPut(JavaScriptObject map, String key, Object val) /*-{
		map[key] = val;
	}-*/;

	private static native void mapPut(JavaScriptObject map, String key, int val) /*-{
		map[key] = val;
	}-*/;	
}
