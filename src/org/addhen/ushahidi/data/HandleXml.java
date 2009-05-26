package org.addhen.ushahidi.data;

import org.addhen.ushahidi.ImageManager;
import org.addhen.ushahidi.UshahidiApplication;
import org.addhen.ushahidi.UshahidiService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class HandleXml {
	
	public static List<IncidentsData> processIncidentsXml( String xml ) {
		
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			doc = builder.parse(new InputSource(new StringReader( xml )));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<IncidentsData> listIncidentsData = new ArrayList<IncidentsData>();
		
		NodeList node = doc.getElementsByTagName("incident");
		String categories = "";
		String media = "";
		
		for( int i = 0; i < node.getLength(); i++ ) {
			
			Node firstNode = node.item(i);
			IncidentsData incidentData = new IncidentsData();
			listIncidentsData.add( incidentData );
			
			if( firstNode.getNodeType() == Node.ELEMENT_NODE ) {
				Element element = (Element) firstNode;
				
				NodeList idElementList = element.getElementsByTagName("id");
				Element idElement = (Element) idElementList.item(0);
				
				NodeList id = idElement.getChildNodes();
		
				incidentData.setIncidentId(Integer.parseInt(((Node) id.item(0)).getNodeValue()));

				NodeList titleElementList = element.getElementsByTagName("title");
				Element titleElement = (Element) titleElementList.item(0);
				
				NodeList title = titleElement.getChildNodes();
				incidentData.setIncidentTitle(((Node) title.item(0)).getNodeValue());
				
				NodeList descElementList = element.getElementsByTagName("description");
				Element descElement = (Element) descElementList.item(0);
				
				NodeList desc = descElement.getChildNodes();
				incidentData.setIncidentDesc(((Node) desc.item(0)).getNodeValue());
				
				NodeList dateElementList = element.getElementsByTagName("date");
				Element dateElement = (Element) dateElementList.item(0);
				
				NodeList date = dateElement.getChildNodes();
				incidentData.setIncidentDate(((Node) date.item(0)).getNodeValue());
				
				NodeList modeElementList = element.getElementsByTagName("mode");
				Element modeElement = (Element) modeElementList.item(0);
				
				NodeList mode = modeElement.getChildNodes();
				incidentData.setIncidentMode(Integer.parseInt( ((Node) mode.item(0)).getNodeValue() ));
				
				NodeList verifiedElementList = element.getElementsByTagName("verified");
				Element verifiedElement = (Element) verifiedElementList.item(0);
				
				NodeList verified = verifiedElement.getChildNodes();
				incidentData.setIncidentVerified(Integer.parseInt(((Node) verified.item(0)).getNodeValue()));
				
				//location
				NodeList locationElementList = element.getElementsByTagName
				("location");
				
				Node locationNode = locationElementList.item(0);
				
				Element locationElement = (Element) locationNode;
				NodeList locationNameList = locationElement.getElementsByTagName("name");
				
				Element locationInnerNameElement = (Element) locationNameList.item(0);
				NodeList locationInnerName = locationInnerNameElement.getChildNodes();
				incidentData.setIncidentLocation(((Node) locationInnerName.item(0)).getNodeValue());
				
				NodeList locationLatitudeList = locationElement.getElementsByTagName("latitude");
				
				Element locationInnerLatitudeElement = (Element) locationLatitudeList.item(0);
				NodeList locationInnerLatitude = locationInnerLatitudeElement.getChildNodes();
				incidentData.setIncidentLocLatitude(((Node)locationInnerLatitude.item(0)).getNodeValue());
								
				NodeList locationLongitudeList = locationElement.getElementsByTagName("longitude");
				
				Element locationInnerLongitudeElement = (Element) locationLongitudeList.item(0);
				NodeList locationInnerLongitude = locationInnerLongitudeElement.getChildNodes();
				incidentData.setIncidentLocLongitude(((Node)locationInnerLongitude.item(0)).getNodeValue());
				
				//categories
				NodeList categoryList = element.getElementsByTagName
				("category");
				for( int w=0; w < categoryList.getLength(); w++ ) { 
					
					Node categoryNode = categoryList.item(w);
					if (categoryNode.getNodeType() == Node.ELEMENT_NODE) {
						Element categoryElement = (Element) categoryNode;
						NodeList categoryNameList = categoryElement.getElementsByTagName("title");
						Element categoryInnerTitleElement = (Element) categoryNameList.item(0);
						NodeList categoryInnerTitle = categoryInnerTitleElement.getChildNodes();
						categories +=  (w == categoryList.getLength() - 1) ?  ((Node)categoryInnerTitle.item(0)).getNodeValue() : ((Node)categoryInnerTitle.item(0)).getNodeValue()+",";
					}
				}
				
				incidentData.setIncidentCategories(categories);
				categories = "";
				
				//categories
				NodeList mediaList = element.getElementsByTagName
				("media");
				for( int j=0; j < mediaList.getLength(); j++ ) { 
					
					Node mediaNode = mediaList.item(j);
					if (mediaNode.getNodeType() == Node.ELEMENT_NODE) {
						Element mediaElement = (Element) mediaNode;
						NodeList mediaThumbList = mediaElement.getElementsByTagName("thumb");
						
						if( mediaThumbList.getLength() != 0) {
						
							Element mediaInnerThumbElement = (Element) mediaThumbList.item(0);
							NodeList mediaThumb = mediaInnerThumbElement.getChildNodes();
							UshahidiService.mNewIncidentsImages.add( ((Node)mediaThumb.item(0)).getNodeValue() );
							
							media += (j == mediaList.getLength() -1)? ( (Node)mediaThumb.item(0)).getNodeValue(): ( (Node)mediaThumb.item(0)).getNodeValue()+",";
						}
					}
				}
				incidentData.setIncidentMedia(media);
				
				media = "";
					
			}
			
		}
		
		//save images
		ImageManager.saveImage();
		
		return listIncidentsData;
		
		
	}
	
	public static List<CategoriesData> processCategoriesXml( String xml ) {
		List<CategoriesData> categoriesData = new ArrayList<CategoriesData>();
		
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			doc = builder.parse(new InputSource(new StringReader( xml )));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		NodeList node = doc.getElementsByTagName("category");
		for( int i = 0; i < node.getLength(); i++ ) {
			Node firstNode = node.item(i);
			CategoriesData category = new CategoriesData(); 
			categoriesData.add( category );
			
			if( firstNode.getNodeType() == Node.ELEMENT_NODE ) {
				Element element = (Element) firstNode;
				
				NodeList idElementList = element.getElementsByTagName("id");
				Element idElement = (Element) idElementList.item(0);
				
				NodeList id = idElement.getChildNodes();
				category.setCategoryTitle(((Node) id.item(0)).getNodeValue());
				
				NodeList titleElementList = element.getElementsByTagName("title");
				Element titleElement = (Element) titleElementList.item(0);
				
				NodeList title = titleElement.getChildNodes();
				category.setCategoryTitle(((Node) title.item(0)).getNodeValue());
				
				NodeList descElementList = element.getElementsByTagName("description");
				Element descElement = (Element) descElementList.item(0);
				
				NodeList desc = descElement.getChildNodes();
				category.setCategoryDescription( ((Node) desc.item(0)).getNodeValue());
				
				NodeList dateElementList = element.getElementsByTagName("color");
				Element dateElement = (Element) dateElementList.item(0);
				
				NodeList date = dateElement.getChildNodes();
				category.setCategoryColor( ((Node) date.item(0)).getNodeValue());
				
			}
		}
	
		return categoriesData;
	}
	
	protected static ImageManager getImageManager() {
	    return UshahidiApplication.mImageManager;
	}
	
}
