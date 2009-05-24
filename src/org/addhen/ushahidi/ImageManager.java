package org.addhen.ushahidi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.addhen.ushahidi.net.UshahidiHttpClient;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

public class ImageManager {
	//Images
	public static Drawable getImages(String fileName) {
		
		Drawable d = null;
	
		FileInputStream fIn;
		if( !TextUtils.isEmpty( fileName) ) {
			try {
				fIn = new FileInputStream(UshahidiService.savePath + fileName );
				d = Drawable.createFromStream(fIn, "src");
			} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		return d;
	}
	
	
	public static void saveImage() {
		byte[] is;
		for( String image : UshahidiService.mNewIncidentsImages) {
			if(!TextUtils.isEmpty(image )) {
				File f = new File( UshahidiService.savePath + image );
				if(!f.exists()) {
					try {
						is = UshahidiHttpClient.fetchImage(UshahidiService.domain+"/media/uploads/"+image);
						if( is != null ) {
							writeImage( is, image );
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			}
		}
		
	}
	
	public static void writeImage(byte[] data, String filename) {
		
		File f = new File(UshahidiService.savePath + filename);
		if(f.exists()){
			f.delete();
		}
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(UshahidiService.savePath + filename);
			fOut.write(data);
			fOut.flush();
			fOut.close();
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
