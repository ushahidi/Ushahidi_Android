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

package org.addhen.ushahidi;
import java.io.File;
import java.util.Random;

public class SaveIncidentsImage extends Thread {
		byte[] data;
		String Text;
		private static Random random = new Random();

		protected static String randomString() {
			return Long.toString(random.nextLong(), 36);
		}
		
		public SaveIncidentsImage(byte[] data){
			this.data = data;
		}
		
		public void run() {
			String filename = "pictureupload" + randomString() + ".jpg";
			ImageManager.writeImage(data, filename);
			UshahidiService.fileName = filename;
			
			File f = new File(UshahidiService.savePath + filename);
			if(f.exists()){
				f.delete();
			}
			
		}
	}