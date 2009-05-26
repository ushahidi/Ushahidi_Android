package org.addhen.ushahidi;
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
			String filename = "PictureUpload" + randomString() + ".jpg";
			ImageManager.writeImage(data, filename);
			UshahidiService.fileName = filename;
		}
	}