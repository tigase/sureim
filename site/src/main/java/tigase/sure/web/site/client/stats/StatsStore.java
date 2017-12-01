/*
 * StatsStore.java
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andrzej
 */
public class StatsStore {
	
	private static final int MAX_SIZE = 20;
	
	private final List<StatsItem> items = new ArrayList<StatsItem>();
	
	/**
	 * Adds item to store
	 * 
	 * @param item
	 * @return true if limit of items was reached and oldest one was removed
	 */
	public boolean add(StatsItem item) {
		items.add(item);
		if (items.size() > MAX_SIZE) {
			items.remove(0);
			return true;
		}
		return false;
	}
	
	public List<StatsItem> getItems() {
		return items;
	}
	
	public void clear() {
		items.clear();
	}
	
	public int getMaxRecords() {
		return MAX_SIZE;
	}
	
}
