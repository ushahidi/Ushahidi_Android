package org.addhen.ushahidi.net;

import java.io.IOException;

import org.addhen.ushahidi.UshahidiService;
import org.apache.http.HttpResponse;

public class Incidents {
	
	public static boolean getAllIncidentsFromWeb() throws IOException {
		HttpResponse response;
		String incidents = "";
		
		StringBuilder uriBuilder = new StringBuilder( UshahidiService.domain);
		uriBuilder.append("/api?task=incidents");
		uriBuilder.append("&by=all");
		uriBuilder.append("&limit="+UshahidiService.totalReports);
		uriBuilder.append("&resp=xml");
		
		response = UshahidiHttpClient.GetURL( uriBuilder.toString());
		
		if( response == null ) {
			return false;
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
