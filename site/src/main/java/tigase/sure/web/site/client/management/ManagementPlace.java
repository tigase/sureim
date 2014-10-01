/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.site.client.management;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import tigase.sure.web.site.client.disco.DiscoPlace;

/**
 *
 * @author andrzej
 */
public class ManagementPlace extends Place {

	public ManagementPlace() {
	}

	public static class Tokenizer implements PlaceTokenizer<ManagementPlace> {

		public ManagementPlace getPlace(String token) {
			return new ManagementPlace();
		}

		public String getToken(ManagementPlace place) {
			return null;
		}

	}

}
