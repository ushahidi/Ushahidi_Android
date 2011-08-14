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
import org.xml.sax.SAXException;

import android.text.TextUtils;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HandleXml {

    protected static final int MEDIA_TYPE_IMAGE = 1;

    protected static final int MEDIA_TYPE_VIDEO = 2;

    // TODO: Is there a 3?
    protected static final int MEDIA_TYPE_NEWS = 4;

    public static List<IncidentsData> processIncidentsXml(String xmL) {
        Log.d("Incident", " Fetching Incident ");
        String xml = xmL.replaceAll("&([^;]+(?!(?:\\w|;)))", "&amp;$1");
        List<IncidentsData> listIncidentsData = new ArrayList<IncidentsData>();
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

        if (builder == null)
            return listIncidentsData;

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

        if (doc == null)
            return listIncidentsData;

        NodeList node = doc.getElementsByTagName("incident");

        for (int i = 0; i < node.getLength(); i++) {
            Node firstNode = node.item(i);
            if (firstNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            listIncidentsData.add(parseIncident((Element)firstNode));
        }
        // save images
        ImageManager.saveThumbnail(UshahidiPref.savePath);
        ImageManager.saveImage(UshahidiPref.savePath);
        return listIncidentsData;
    }

    private static IncidentsData parseIncident(Element element) {
        IncidentsData incidentData = new IncidentsData();

        NodeList idElementList = element.getElementsByTagName("id");
        if (idElementList.getLength() != 0) {
            Element idElement = (Element)idElementList.item(0);

            NodeList id = idElement.getChildNodes();

            incidentData.setIncidentId(Integer.parseInt((id.item(0)).getNodeValue()));
        }

        NodeList titleElementList = element.getElementsByTagName("title");
        if (titleElementList.getLength() != 0) {
            Element titleElement = (Element)titleElementList.item(0);

            NodeList title = titleElement.getChildNodes();
            incidentData.setIncidentTitle((title.item(0)).getNodeValue());
        }

        NodeList descElementList = element.getElementsByTagName("description");
        if (descElementList.getLength() != 0) {
            Element descElement = (Element)descElementList.item(0);

            NodeList desc = descElement.getChildNodes();
            incidentData.setIncidentDesc((desc.item(0)).getNodeValue());
        }

        NodeList dateElementList = element.getElementsByTagName("date");
        if (dateElementList.getLength() != 0) {
            Element dateElement = (Element)dateElementList.item(0);

            NodeList date = dateElement.getChildNodes();
            incidentData.setIncidentDate((date.item(0)).getNodeValue());
        }

        NodeList modeElementList = element.getElementsByTagName("mode");
        if (modeElementList.getLength() != 0) {
            Element modeElement = (Element)modeElementList.item(0);

            NodeList mode = modeElement.getChildNodes();
            incidentData.setIncidentMode(Integer.parseInt((mode.item(0)).getNodeValue()));
        }

        NodeList verifiedElementList = element.getElementsByTagName("verified");
        if (verifiedElementList.getLength() != 0) {
            Element verifiedElement = (Element)verifiedElementList.item(0);

            NodeList verified = verifiedElement.getChildNodes();
            incidentData.setIncidentVerified(Integer.parseInt((verified.item(0)).getNodeValue()));
        }

        // location
        NodeList locationElementList = element.getElementsByTagName("location");
        if (locationElementList.getLength() != 0) {
            Node locationNode = locationElementList.item(0);

            Element locationElement = (Element)locationNode;

            NodeList locationNameList = locationElement.getElementsByTagName("name");
            if (locationNameList.getLength() != 0) {
                Element locationInnerNameElement = (Element)locationNameList.item(0);
                NodeList locationInnerName = locationInnerNameElement.getChildNodes();
                incidentData.setIncidentLocation((locationInnerName.item(0)).getNodeValue());
            }

            NodeList locationLatitudeList = locationElement.getElementsByTagName("latitude");
            if (locationLatitudeList.getLength() != 0) {
                Element locationInnerLatitudeElement = (Element)locationLatitudeList.item(0);
                NodeList locationInnerLatitude = locationInnerLatitudeElement.getChildNodes();
                incidentData.setIncidentLocLatitude((locationInnerLatitude.item(0)).getNodeValue());
            }

            NodeList locationLongitudeList = locationElement.getElementsByTagName("longitude");
            if (locationLongitudeList.getLength() != 0) {
                Element locationInnerLongitudeElement = (Element)locationLongitudeList.item(0);
                NodeList locationInnerLongitude = locationInnerLongitudeElement.getChildNodes();
                incidentData.setIncidentLocLongitude((locationInnerLongitude.item(0))
                        .getNodeValue());
            }
        }
        // categories
        NodeList categoryList = element.getElementsByTagName("category");
        StringBuilder categories = new StringBuilder();
        for (int w = 0; w < categoryList.getLength(); w++) {

            Node categoryNode = categoryList.item(w);
            if (categoryNode.getNodeType() == Node.ELEMENT_NODE) {
                Element categoryElement = (Element)categoryNode;
                NodeList categoryNameList = categoryElement.getElementsByTagName("title");
                Element categoryInnerTitleElement = (Element)categoryNameList.item(0);
                NodeList categoryInnerTitle = categoryInnerTitleElement.getChildNodes();
                categories.append(categoryInnerTitle.item(0).getNodeValue() + ",");
            }
        }
        // Delete the last ","
        if (categories.length() > 0)
            categories.deleteCharAt(categories.length() - 1);
        incidentData.setIncidentCategories(categories.toString());

        StringBuilder thumbnail = new StringBuilder();
        StringBuilder image = new StringBuilder();
        // media
        NodeList mediaList = element.getElementsByTagName("media");
        for (int j = 0; j < mediaList.getLength(); j++) {

            Node mediaNode = mediaList.item(j);
            if (mediaNode.getNodeType() == Node.ELEMENT_NODE) {
                Element mediaElement = (Element)mediaNode;
                NodeList mediaThumbList = mediaElement.getElementsByTagName("thumb");

                if (mediaThumbList.getLength() != 0) {

                    Element mediaInnerThumbElement = (Element)mediaThumbList.item(0);
                    NodeList mediaThumb = mediaInnerThumbElement.getChildNodes();
                    String thumbName = (mediaThumb.item(0)).getNodeValue();
                    if (!TextUtils.isEmpty(thumbName)) {

                        UshahidiService.mNewIncidentsThumbnails.add(thumbName);
                    }

                    File thumbnailFilename = new File(thumbName);
                    thumbnail.append(thumbnailFilename.getName() + ",");
                }

                // Check media type
                NodeList mediaTypeList = mediaElement.getElementsByTagName("type");
                if (mediaTypeList.getLength() != 0) {
                    NodeList mediaTypes = ((Element)mediaTypeList.item(0)).getChildNodes();
                    String mediaType = mediaTypes.item(0).getNodeValue();
                    switch (Integer.parseInt(mediaType)) {
                        case MEDIA_TYPE_IMAGE:
                            NodeList mediaImageList = mediaElement.getElementsByTagName("link");

                            if (mediaImageList.getLength() != 0) {
                                String imageName = ((Element)mediaImageList.item(0)).getNodeValue();
                                if (!TextUtils.isEmpty(imageName)) {
                                    UshahidiService.mNewIncidentsImages.add(imageName);
                                    File imageFilename = new File(imageName);
                                    image.append(imageFilename.getName() + ",");
                                }
                            }
                            break;
                        case MEDIA_TYPE_VIDEO:
                            break;
                        case MEDIA_TYPE_NEWS:
                            break;
                    }
                }
            }
        }
        // Delete the last ","
        if (thumbnail.length() > 0)
            thumbnail.deleteCharAt(thumbnail.length() - 1);
        if (image.length() > 0)
            image.deleteCharAt(image.length() - 1);

        incidentData.setIncidentThumbnail(thumbnail.toString());
        incidentData.setIncidentImage(image.toString());

        return incidentData;
    }

    public static List<CategoriesData> processCategoriesXml(String xmL) {
        Log.d("Categories XML", "Fetching categories ");
        List<CategoriesData> categoriesData = new ArrayList<CategoriesData>();
        String xml = xmL.replaceAll("&([^;]+(?!(?:\\w|;)))", "&amp;$1");
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

        if (builder == null)
            return categoriesData;

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

        if (doc == null)
            return categoriesData;

        NodeList node = doc.getElementsByTagName("category");
        for (int i = 0; i < node.getLength(); i++) {
            Node firstNode = node.item(i);
            CategoriesData category = new CategoriesData();
            categoriesData.add(category);

            if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)firstNode;

                NodeList idElementList = element.getElementsByTagName("id");

                if (idElementList.getLength() != 0) {
                    Element idElement = (Element)idElementList.item(0);
                    if (idElement != null) {
                        NodeList id = idElement.getChildNodes();
                        category.setCategoryId(Integer.parseInt((id.item(0)).getNodeValue()));
                    }
                }

                NodeList titleElementList = element.getElementsByTagName("title");

                if (titleElementList.getLength() != 0) {
                    Element titleElement = (Element)titleElementList.item(0);

                    NodeList title = titleElement.getChildNodes();
                    category.setCategoryTitle((title.item(0)).getNodeValue());
                    categories += (title.item(0)).getNodeValue() + ", ";

                }

                NodeList descElementList = element.getElementsByTagName("description");

                if (descElementList.getLength() != 0) {
                    Element descElement = (Element)descElementList.item(0);

                    NodeList desc = descElement.getChildNodes();
                    category.setCategoryDescription((desc.item(0)).getNodeValue());

                }

                NodeList colorElementList = element.getElementsByTagName("color");

                if (colorElementList.getLength() != 0) {
                    Element colorElement = (Element)colorElementList.item(0);

                    NodeList color = colorElement.getChildNodes();
                    category.setCategoryColor((color.item(0)).getNodeValue());

                }

            }
        }

        return categoriesData;
    }

    protected static ImageManager getImageManager() {
        return UshahidiApplication.mImageManager;
    }

}
