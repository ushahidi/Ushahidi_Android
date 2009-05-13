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
