/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.stats;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 *
 * @author andrzej
 */
public class StatsPlace extends Place {

	public StatsPlace() {
	}

	public static class Tokenizer implements PlaceTokenizer<StatsPlace> {

		@Override
		public StatsPlace getPlace(String token) {
			return new StatsPlace();
		}

		@Override
		public String getToken(StatsPlace place) {
			return null;
		}

	}

}
