/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andrzej
 */
public class StatsStore {
	
	private static final int MAX_SIZE = 20;
	
	private final List<StatsItem> items = new ArrayList<StatsItem>();
	
	public void add(StatsItem item) {
		items.add(item);
		if (items.size() > MAX_SIZE) {
			items.remove(0);
		}
	}
	
	public List<StatsItem> getItems() {
		return items;
	}
	
	public void clear() {
		items.clear();
	}
	
}
