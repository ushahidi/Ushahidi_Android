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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.ushahidi.android.app.R;

/**
 * @author eyedol
 * 
 */
public class PopupAdapter implements InfoWindowAdapter {

	protected LayoutInflater inflater = null;

	protected TextView title;

	protected TextView snippet;

	protected static TextView readMore;

	protected ImageView image;

	PopupAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View v = inflater.inflate(R.layout.map_balloon_overlay, null);
		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		readMore = (TextView) v.findViewById(R.id.balloon_item_readmore);
		image = (ImageView) v.findViewById(R.id.balloon_item_image);
		return v;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

}
