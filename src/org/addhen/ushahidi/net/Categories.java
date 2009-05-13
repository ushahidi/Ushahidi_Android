package org.addhen.ushahidi.net;

import java.io.IOException;

import org.addhen.ushahidi.UshahidiService;
import org.apache.http.HttpResponse;

public class Categories {
	
public static boolean getAllCategoriesFromWeb() throws IOException {
		
		HttpResponse response;
		String incidents = "";
		
		StringBuilder uriBuilder = new StringBuilder( UshahidiService.domain);
		uriBuilder.append("/api?task=categories");
		uriBuilder.append("&by=all");
		uriBuilder.append("&resp=xml");
		
		response = UshahidiHttpClient.GetURL( uriBuilder.toString() );
		
		if( response == null ) {
			return true;
		}
		
		final int statusCode = response.getStatusLine().getStatusCode();
		
		if( statusCode == 200 ) {
			incidents = UshahidiHttpClient.GetText(response);
			UshahidiService.incidentsResponse = incidents;
			return true;
		
		} else {
			return false;
		}
	}

}
