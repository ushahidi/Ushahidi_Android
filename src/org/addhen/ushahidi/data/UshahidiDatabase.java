/**
 * Code inspired from how AndTweet handles its database
 */
package org.addhen.ushahidi.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Ushahidi contact provider
 * 
 * @author henryaddo
 *
 */
public final class UshahidiDatabase 
{
	public static final String AUTHORITY = "org.addhen.ushahidi";
	
	public static final String USHAHIDI_DATE_FORMAT = "yyy-mm-dd HH:mm:ss";
	
	public UshahidiDatabase() {
		
	}
	
	/**
	 * Incidents table
	 * 
	 * @author henryaddo
	 *
	 */
	public static final class Incidents implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/incident");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.addhen.ushahidi.incident";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.addhen.ushahidi.incident";
		
		public static final String DEFAULT_SORT_ORDER = "incident_id DESC";
		
		//Table columns 
		public static final String INCIDENT_ID = "incident_id";
		public static final String INCIDENT_TITLE = "incident_title";
		public static final String INCIDENT_DESC = "incident_desc";
		public static final String INCIDENT_DATE = "incident_date";
		public static final String INCIDENT_MODE = "incident_mode";
		public static final String INCIDENT_VERIFIED = "incident_verified";
		public static final String INCIDENT_LOC_NAME = "incident_loc_name";
		public static final String INCIDENT_LOC_LATITUDE = "incident_loc_latitude";
		public static final String INCIDENT_LOC_LONGITUDE = "incident_loc_longitude";
		public static final String INCIDENT_CATEGORIES = "incident_categories";
		public static final String INCIDENT_MEDIA = "incident_media";
		
	}
	
	/**
	 * Locations table
	 * 
	 * @author henryaddo
	 */
	public static final class Locations implements BaseColumns
	{
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/incident");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.addhen.ushahidi.location";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.addhen.ushahidi.location";
		
		public static final String DEFAULT_SORT_ORDER = "location_id DESC";
		
		/**
		 * Table columns
		 */
		public static final String LOCATION_ID = "location_id";
		public static final String LOCATION_NAME = "location_name";
		public static final String LOCATION_LATITUDE = "location_latitude";
		public static final String LOCATION_LONGITUDE = "location_longitude";
		
	}
	
	/**
	 * Categories table
	 * 
	 * @author henryaddo
	 *
	 */
	public static final class Categories implements BaseColumns 
	{
		public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/category");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.addhen.ushahidi.category";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.addhen.ushahidi.category";
		
		public static final String DEFAULT_SORT_ORDER = "incident_id DESC";
		
		/**
		 * Table colums
		 */
		public static final String CATEGORY_ID = "category_id";
		public static final String CATEGORY_TITLE = "category_title";
		public static final String CATEGORY_DESC = "category_desc";
		public static final String CATEGORY_COLOR = "category_color";
	}
}
