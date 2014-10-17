/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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
	private Element canvas;
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
			console.log("creating chart with data", data);
			var canvas = this.@tigase.sure.web.site.client.stats.ChartJS::canvas;
			var ctx = canvas.getContext("2d");
			var chart = new Chart(ctx).Line(data);
			this.@tigase.sure.web.site.client.stats.ChartJS::chart = chart;
		} catch (ex) {
			console.log('Exception creating chart', data, ex);
		}
	}-*/;
	
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
