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

package com.ushahidi.android.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.ushahidi.android.app.net.UshahidiHttpClient;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class ImageManager {
	//Images
	public static Drawable getImages(String fileName) {
		
		Drawable d = null;
	
		FileInputStream fIn;
		if( !TextUtils.isEmpty( fileName) ) {
			try {
				fIn = new FileInputStream(UshahidiService.savePath + fileName);
				d = Drawable.createFromStream(fIn, "src");
			} catch (FileNotFoundException e) {
			
				e.printStackTrace();
			}
		}
	
		return d;
	}
	
	
	public static void saveImage() {
		byte[] is;
		for (String image : UshahidiService.mNewIncidentsImages) {
			if (!TextUtils.isEmpty(image)) {
				File f = new File(UshahidiService.savePath + image);
				if (!f.exists()) {
					try {
						is = UshahidiHttpClient.fetchImage(UshahidiService.domain+"/media/uploads/"+image);
						if( is != null ) {
							writeImage( is, image );
						}
					} catch (MalformedURLException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				
				}
			}
		}
		
	}
	
	public static void saveThumbnail() {
		byte[] is;
		for (String image : UshahidiService.mNewIncidentsThumbnails) {
			if(!TextUtils.isEmpty(image )) {
				File f = new File(UshahidiService.savePath + image);
				if(!f.exists()) {
					try {
						is = UshahidiHttpClient.fetchImage(UshahidiService.domain+"/media/uploads/"+image);
						if( is != null ) {
							writeImage(is, image);
						}
					} catch (MalformedURLException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				
				}
			}
		}
		
	}
	
	public static void writeImage(byte[] data, String filename) {
		
		deleteImage(filename);
		
		if( data != null ) {
			FileOutputStream fOut;	
			try {
				fOut = new FileOutputStream(UshahidiService.savePath + filename);
				fOut.write(data);
				fOut.flush();
				fOut.close();
			} catch (final FileNotFoundException e) {
			
				e.printStackTrace();
			} catch (final IOException e) {
			
				e.printStackTrace();
			}
		}
		
	}
	
	public static void deleteImage(String filename) {
		
		File f = new File(UshahidiService.savePath + filename);
		if (f.exists()){
			f.delete();
		}
	}
	
}
