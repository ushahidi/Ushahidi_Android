package org.addhen.ushahidi.net;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class UshahidiHttpClient extends Service{
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	public List<IncidentData> getIncidents( String URL, String cat ) throws Exception{
		
		String category = "&name="+URLEncoder.encode( cat, "UTF-8");
    	List<IncidentData> incidentData = null;	
		
		StringBuilder uriBuilder = new StringBuilder(URL);
		uriBuilder.append("?task=incidents");
		uriBuilder.append("&by=catname");
		uriBuilder.append( category );
		uriBuilder.append("&resp=xml");
		String responseBody = httpRequest( uriBuilder );
		try {
			
			incidentData = buildIncidentData(responseBody);
		}catch( Exception e ){
			Log.i("Fetch data", "Exception "+e.toString()+uriBuilder );
		}
		return incidentData;
	}
	
	public List<Category> getCategories( String URL ) throws Exception{
		
		List<Category> categoryData = null;
		StringBuilder uriBuilder = new StringBuilder( URL );
		uriBuilder.append("?task=categories");
		uriBuilder.append("&resp=xml");
		
		String responseBody = httpRequest( uriBuilder );
		
		try{
			categoryData = buildCategoryData( responseBody );
		}catch( Exception e ){
			Log.e("Ushahidi Get Service", "Exception "+e.toString() );
		}
		
		return categoryData;
	}
	
	public String httpRequest( StringBuilder uriBuilder ) {
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet getRequest = new HttpGet( uriBuilder.toString() );
		
		String responseBody = "";
		
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpClient.execute(getRequest,responseHandler );
			
		}catch( ClientProtocolException cPE ){
			Log.i("ClientProtocolException ", "ClientProtocalException "+ cPE.toString());
		}catch( IOException ex ){
			Log.i("IOException ", "IOException "+ ex.toString());
		}
		return responseBody;
	}
	
	//get incident content
	public List<IncidentData> buildIncidentData(String raw) throws Exception {
		String thumbs[] = null;
		String t = "";
		List<IncidentData> incidentsData = new ArrayList<IncidentData>();
		/*IncidentData incidentData = new IncidentData();
		JSONArray mIncidents;
		
		try{
			mIncidents = new JSONArray(raw);
			for( int i = 0; i < mIncidents.length(); i++ ){
				JSONObject incidents = mIncidents.getJSONObject(i);
				JSONObject inc = incidents.getJSONObject("incident");
				//JSONObject inc = in.getJSONObject("incident");
				incidentData.setTitle( inc.getString("incidenttitle") );
				incidentData.setIBody( inc.getString("incidentdescription"));
				incidentData.setThumbnail(inc.getString("thumbnails") );
				incidentData.setICategory(inc.getString("categorytitle"));
				incidentData.setILocation(inc.getString("locationname"));
			}
			incidentsData.add(incidentData);
		}catch(JSONException e){
			e.printStackTrace();
		}*/
		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Document doc=builder.parse(new InputSource(new StringReader(raw)));
		
		NodeList titles = doc.getElementsByTagName("incidenttitle");
		
		for (int i=0;i<titles.getLength();i++) {
			Element title = (Element)titles.item(i);
			IncidentData incidentData = new IncidentData();
			
			incidentsData.add(incidentData);
			incidentData.setTitle(title.getFirstChild().getNodeValue());
		}
		
		NodeList bodies = doc.getElementsByTagName("incidentdescription");
		
		for (int i=0;i< bodies.getLength();i++) {
			
			Element body = (Element) bodies.item(i);
			IncidentData incidentData = incidentsData.get(i);
			
			incidentData.setIBody( body.getFirstChild().getNodeValue() );
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
	
	public List<Category> buildCategoryData( String raw ) throws Exception{	
		
		List<Category> categoriesData = new ArrayList<Category>();
			
		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
		Document doc=builder.parse(new InputSource(new StringReader(raw)));	
		
		//category title
		NodeList cTitles =doc.getElementsByTagName("category_title");
		for (int i=0;i < cTitles.getLength();i++) {
			
			Element cTitle = (Element) cTitles.item(i);
			Category category = new Category(); 
			categoriesData.add( category );
			
			category.setCTitle( cTitle.getFirstChild().getNodeValue() );
		}
		
		//category description
		NodeList cDescs = doc.getElementsByTagName("category_description");
			
		for (int i=0;i< cDescs.getLength();i++) {
				
			Element cDesc = (Element) cDescs.item(i);
			Category category = categoriesData.get(i);
				
			category.setCDesc( cDesc.getFirstChild().getNodeValue() );
		}
		
		//category id
		NodeList cIds = doc.getElementsByTagName("id");
			
		for (int i=0;i< cIds.getLength();i++) {
			Element cId = (Element) cIds.item(i);
			Category category = categoriesData.get(i);
				
			category.setCId( new Integer( cId.getFirstChild().getNodeValue()) );
		}
			
		//category color
		NodeList cColors = doc.getElementsByTagName("category_color");
			
		for (int i=0;i< cColors.getLength();i++) {
				
			Element cColor = (Element) cDescs.item(i);
			Category category = categoriesData.get(i);
				
			category.setCDesc( cColor.getFirstChild().getNodeValue() );
		}
			
		return categoriesData;
	}
	
	
	//build incident data.
	public class IncidentData {
		
		String iTitle = "";
		String iBody = "";
		String iThumbnail = "";
		String iCategory = "";
		String iLocation = "";
		
		public IncidentData() {
			
		}
		public String getTitle() {
			return iTitle;
		}
	
		public void setTitle(String title ) {
			this.iTitle = title;
		}
		
		public String getIBody() {
			return iBody;
		}
		
		public void setIBody( String iBody) {
			this.iBody = iBody;
		}
		
		public String getThumbnail() {
			return iThumbnail;
		}
		
		public void setThumbnail(String iThumbnail ) {
			this.iThumbnail = iThumbnail;
		}
		
		public String getICategory() {
			return iCategory;
		}
		
		public void setICategory( String iCategory ) {
			this.iCategory = iCategory;
		}
		
		public String getILocation() {
			return this.iLocation;
		}
		
		public void setILocation( String iLocation ) {
			this.iLocation =  iLocation;
		}
	}
	
public class Category {
		
		String cTitle = "";
		String cDesc = "";
		int cId;
		String cColor = "";
		
		public String getCTitle() {
			return cTitle;
		}
	
		public void setCTitle(String cTitle ) {
			this.cTitle = cTitle;
		}
		
		public String getCDesc() {
			return cDesc;
		}
		
		public void setCDesc( String cDesc ) {
			this.cDesc = cDesc;
		}
		
		public int getCId() {
			return cId;
		}
		
		public void setCId( int cId ) {
			this.cId = cId;
		}
		
		public String getCColor() {
			return cColor;
		}
		
		public void setICategory( String cColor ) {
			this.cColor = cColor;
		}
		
}

}

