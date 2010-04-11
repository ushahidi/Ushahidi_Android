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

package org.addhen.ushahidi.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

public class PagedCursorAdapter extends SimpleCursorAdapter implements FilterQueryProvider {

	private ContentResolver mContentResolver;
	private String[] mProjection;
	private Uri mUri;
	private String mSortOrder;
	private Cursor mCursor;
	//private LayoutInflater mInflater;

	/**
	 * 
	 * @param context
	 * @param layout
	 * @param c
	 * @param from
	 * @param to
	 */
	public PagedCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, Uri uri, String[] projection, String sortOrder) {
		super(context, layout, c, from, to);
		mContentResolver = context.getContentResolver();
		mProjection = projection;
		mUri = uri;
		mSortOrder = sortOrder;
		setFilterQueryProvider(this);
		//mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	public Cursor runQuery(CharSequence constraint) {
		if (constraint != null) {
			if (mSortOrder.indexOf("LIMIT 0,") > 0) {
				String newSortOrder = mSortOrder.substring(0, mSortOrder.indexOf("LIMIT 0,"));
				mSortOrder = newSortOrder;
			}
			mSortOrder += " " + constraint.toString().trim();
		}
		if (mCursor != null && !mCursor.isClosed()) mCursor.close();
		return mContentResolver.query(mUri, mProjection, null, null, mSortOrder);
	}
}
