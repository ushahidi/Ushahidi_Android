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