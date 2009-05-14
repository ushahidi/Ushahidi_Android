package org.addhen.ushahidi.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class HandleXml {
	private static final String TAG = "HandleXml";
	
	private static List<IncidentsData> processIncidentsXml( String xml ) {
		
		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Document doc=builder.parse(new InputSource(new StringReader( xml )));
		
		NodeList ids = doc.getElementsByTagName("id");
		
		List<IncidentsData> listIncidentsData = new ArrayList<IncidentsData>();
		
		for( int i = 0; i < ids.getLength(); i++ ) {
			Element id = (Element) ids.item(i);
			IncidentsData incidentData = new IncidentsData();
			
			listIncidentsData.add( incidentData );
			incidentData.setIncidentId(Integer.parseInt( id.getFirstChild().getNodeValue()));
		}
		
		NodeList titles = doc.getElementsByTagName("title");
		
		for (int i=0;i<titles.getLength();i++) {
			Element title = (Element)titles.item(i);
			IncidentsData incidentData = listIncidentsData.get(i);
			
			incidentData.setIncidentTitle(title.getFirstChild().getNodeValue());
		}
		
		NodeList descriptions = doc.getElementsByTagName("description");
		
		for (int i=0;i< descriptions.getLength();i++) {
			
			Element description = (Element) descriptions.item(i);
			IncidentsData incidentData = listIncidentsData.get(i);
			
			incidentData.setIncidentDesc( description.getFirstChild().getNodeValue() );
		}
		
		NodeList thumbnails = doc.getElementsByTagName("thumb");
		
		for (int i=0;i< thumbnails.getLength();i++) {
			
			Element thumbnail= (Element) thumbnails.item(i);
			IncidentData incidentData = incidentsData.get(i);
			t +=thumbnail.getFirstChild().getNodeValue()+",";
			//incidentData.setThumbnail("");
			incidentData.setThumbnail(thumbnail.getFirstChild().getNodeValue());
		}
		
		Log.i("thumbnails", t);
		
		NodeList categories = doc.getElementsByTagName("categorytitle");
		
		for (int i=0;i< categories.getLength();i++) {
			Element category = (Element) categories.item(i);
			IncidentData incidentData = incidentsData.get(i);
			
			incidentData.setICategory( category.getFirstChild().getNodeValue());
		}
		
		NodeList iLocations = doc.getElementsByTagName("locationname");
		
		for (int i=0;i< iLocations.getLength();i++) {
			Element iLocation = (Element) iLocations.item(i);
			IncidentData incidentData = incidentsData.get(i);
			
			incidentData.setILocation(iLocation.getFirstChild().getNodeValue());
		}
		
		return incidentsData;
		
	}
	
}
