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
package com.ushahidi.android.app.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.ImageViewWorker;

/**
 * @author eyedol
 * 
 */
public class PopupAdapter implements InfoWindowAdapter {

	protected LayoutInflater inflater = null;

	public TextView title;

	protected TextView snippet;

	protected ImageView badge;

	public PopupAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View v = inflater.inflate(R.layout.infowindow_content, null);
		render(marker, v);
		return v;
	}

	private void render(Marker marker, View v) {
		// setup our fields
		title = (TextView) v.findViewById(R.id.title);
		snippet = (TextView) v.findViewById(R.id.snippet);
		badge = (ImageView) v.findViewById(R.id.badge);

		title.setText(marker.getTitle());
		snippet.setText(marker.getSnippet());
		
		getPhoto("", badge, v);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View v = inflater.inflate(R.layout.infowindow, null);
		render(marker, v);
		return v;
	}

	/**
	 * Set the photo
	 * 
	 * @param fileName
	 * @param imageView
	 * @param v
	 */
	private void getPhoto(String fileName, ImageView imageView, View v) {
		if ((fileName != null) && (!TextUtils.isEmpty(fileName))) {
			ImageViewWorker imageWorker = new ImageViewWorker(v.getContext());
			imageWorker.setImageFadeIn(true);
			imageWorker.loadImage(fileName, imageView, true, 0);
		}
	}
}
