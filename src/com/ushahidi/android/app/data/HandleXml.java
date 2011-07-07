/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package com.ushahidi.android.app.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.ushahidi.android.app.ImageManager;
import com.ushahidi.android.app.UshahidiApplication;
import com.ushahidi.android.app.UshahidiPref;
import com.ushahidi.android.app.UshahidiService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HandleXml {

    public static List<IncidentsData> processIncidentsXml(String xml) {
        String categories = "";
        String thumbnail = "";
        String image = "";

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
            // encode the xml to UTF -8
            ByteArrayInputStream encXML = new ByteArrayInputStream(xml.getBytes("UTF8"));
            doc = builder.parse(encXML);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<IncidentsData> listIncidentsData = new ArrayList<IncidentsData>();

        NodeList node = doc.getElementsByTagName("incident");

        for (int i = 0; i < node.getLength(); i++) {

            Node firstNode = node.item(i);
            IncidentsData incidentData = new IncidentsData();
            listIncidentsData.add(incidentData);

            if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)firstNode;

                NodeList idElementList = element.getElementsByTagName("id");
                Element idElement = (Element)idElementList.item(0);

                NodeList id = idElement.getChildNodes();

                incidentData.setIncidentId(Integer.parseInt((id.item(0)).getNodeValue()));

                NodeList titleElementList = element.getElementsByTagName("title");
                Element titleElement = (Element)titleElementList.item(0);

                NodeList title = titleElement.getChildNodes();
                incidentData.setIncidentTitle((title.item(0)).getNodeValue());

                NodeList descElementList = element.getElementsByTagName("description");
                Element descElement = (Element)descElementList.item(0);

                NodeList desc = descElement.getChildNodes();
                incidentData.setIncidentDesc((desc.item(0)).getNodeValue());

                NodeList dateElementList = element.getElementsByTagName("date");
                Element dateElement = (Element)dateElementList.item(0);

                NodeList date = dateElement.getChildNodes();
                incidentData.setIncidentDate((date.item(0)).getNodeValue());

                NodeList modeElementList = element.getElementsByTagName("mode");
                Element modeElement = (Element)modeElementList.item(0);

                NodeList mode = modeElement.getChildNodes();
                incidentData.setIncidentMode(Integer.parseInt((mode.item(0)).getNodeValue()));

                NodeList verifiedElementList = element.getElementsByTagName("verified");
                Element verifiedElement = (Element)verifiedElementList.item(0);

                NodeList verified = verifiedElement.getChildNodes();
                incidentData
                        .setIncidentVerified(Integer.parseInt((verified.item(0)).getNodeValue()));

                // location
                NodeList locationElementList = element.getElementsByTagName("location");

                Node locationNode = locationElementList.item(0);

                Element locationElement = (Element)locationNode;
                NodeList locationNameList = locationElement.getElementsByTagName("name");

                Element locationInnerNameElement = (Element)locationNameList.item(0);
                NodeList locationInnerName = locationInnerNameElement.getChildNodes();
                incidentData.setIncidentLocation((locationInnerName.item(0)).getNodeValue());

                NodeList locationLatitudeList = locationElement.getElementsByTagName("latitude");

                Element locationInnerLatitudeElement = (Element)locationLatitudeList.item(0);
                NodeList locationInnerLatitude = locationInnerLatitudeElement.getChildNodes();
                incidentData.setIncidentLocLatitude((locationInnerLatitude.item(0)).getNodeValue());

                NodeList locationLongitudeList = locationElement.getElementsByTagName("longitude");

                Element locationInnerLongitudeElement = (Element)locationLongitudeList.item(0);
                NodeList locationInnerLongitude = locationInnerLongitudeElement.getChildNodes();
                incidentData.setIncidentLocLongitude((locationInnerLongitude.item(0))
                        .getNodeValue());

                // categories
                NodeList categoryList = element.getElementsByTagName("category");
                for (int w = 0; w < categoryList.getLength(); w++) {

                    Node categoryNode = categoryList.item(w);
                    if (categoryNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element categoryElement = (Element)categoryNode;
                        NodeList categoryNameList = categoryElement.getElementsByTagName("title");
                        Element categoryInnerTitleElement = (Element)categoryNameList.item(0);
                        NodeList categoryInnerTitle = categoryInnerTitleElement.getChildNodes();
                        categories += (w == categoryList.getLength() - 1) ? (categoryInnerTitle
                                .item(0)).getNodeValue() : (categoryInnerTitle.item(0))
                                .getNodeValue() + ",";
                    }
                }

                incidentData.setIncidentCategories(categories);
                categories = "";
                // UshahidiService.mNewIncidentsImages.clear();
                // UshahidiService.mNewIncidentsThumbnails.clear();
                // categories
                NodeList mediaList = element.getElementsByTagName("media");
                for (int j = 0; j < mediaList.getLength(); j++) {

                    Node mediaNode = mediaList.item(j);
                    if (mediaNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element mediaElement = (Element)mediaNode;
                        NodeList mediaThumbList = mediaElement.getElementsByTagName("thumb");

                        if (mediaThumbList.getLength() != 0) {

                            Element mediaInnerThumbElement = (Element)mediaThumbList.item(0);
                            NodeList mediaThumb = mediaInnerThumbElement.getChildNodes();

                            if (!(mediaThumb.item(0)).getNodeValue().equals("")) {

                                UshahidiService.mNewIncidentsThumbnails.add((mediaThumb.item(0))
                                        .getNodeValue());
                            }

                            File thumbnailFilename = new File((mediaThumb.item(0)).getNodeValue());
                            thumbnail += (j == mediaList.getLength() - 1) ? thumbnailFilename
                                    .getName() : thumbnailFilename.getName() + ",";
                        }

                        NodeList mediaImageList = mediaElement.getElementsByTagName("link");

                        if (mediaImageList.getLength() != 0) {
                            Element mediaInnerImageElement = (Element)mediaImageList.item(0);
                            NodeList mediaImage = mediaInnerImageElement.getChildNodes();
                            if (!(mediaImage.item(0)).getNodeValue().equals("")) {
                                UshahidiService.mNewIncidentsImages.add((mediaImage.item(0))
                                        .getNodeValue());
                            }
                            // if( j != 0) {
                            File imageFilename = new File((mediaImage.item(0)).getNodeValue());
                            image += imageFilename.getName() + ",";
                            // }
                            // image += (j == mediaImageList.getLength() -1)? (
                            // (Node)mediaImage.item(0)).getNodeValue(): (
                            // (Node)mediaImage.item(0)).getNodeValue()+",";

                        }
                    }
                }

                incidentData.setIncidentThumbnail(thumbnail);
                incidentData.setIncidentImage(image);
                thumbnail = "";
                image = "";

            }

        }

        // save images
        ImageManager.saveThumbnail(UshahidiPref.savePath);
        ImageManager.saveImage(UshahidiPref.savePath);

        return listIncidentsData;

    }

    public static List<CategoriesData> processCategoriesXml(String xml) {
        Log.d("StringXML", "XML: " + xml);
        List<CategoriesData> categoriesData = new ArrayList<CategoriesData>();
        String categories = "";
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

        if (builder == null) return categoriesData;

        try {

            // encode the xml to UTF -8
            ByteArrayInputStream encXML = new ByteArrayInputStream(xml.getBytes("UTF8"));
            doc = builder.parse(encXML);

        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (doc == null) return categoriesData;

        NodeList node = doc.getElementsByTagName("category");
        for (int i = 0; i < node.getLength(); i++) {
            Node firstNode = node.item(i);
            CategoriesData category = new CategoriesData();
            categoriesData.add(category);

            if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)firstNode;

                NodeList idElementList = element.getElementsByTagName("id");
                Element idElement = (Element)idElementList.item(0);

                NodeList id = idElement.getChildNodes();
                category.setCategoryId(Integer.parseInt((id.item(0)).getNodeValue()));

                NodeList titleElementList = element.getElementsByTagName("title");
                Element titleElement = (Element)titleElementList.item(0);

                NodeList title = titleElement.getChildNodes();
                category.setCategoryTitle((title.item(0)).getNodeValue());
                categories += (title.item(0)).getNodeValue() + ", ";

                NodeList descElementList = element.getElementsByTagName("description");
                Element descElement = (Element)descElementList.item(0);

                NodeList desc = descElement.getChildNodes();
                category.setCategoryDescription((desc.item(0)).getNodeValue());

                NodeList dateElementList = element.getElementsByTagName("color");
                Element dateElement = (Element)dateElementList.item(0);

                NodeList date = dateElement.getChildNodes();
                category.setCategoryColor((date.item(0)).getNodeValue());

            }
        }

        return categoriesData;
    }

    protected static ImageManager getImageManager() {
        return UshahidiApplication.mImageManager;
    }

}
