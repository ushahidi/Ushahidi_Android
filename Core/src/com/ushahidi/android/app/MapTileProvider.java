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

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.UrlTileProvider;

/**
 * Base class to provide base tile map
 * 
 */
public abstract class MapTileProvider extends UrlTileProvider {

	private String tileUrl = "";

	public MapTileProvider(int tileWidth, int tileHeight, String tileUrl) {
		super(tileWidth, tileHeight);
		this.tileUrl = tileUrl;
	}

	@Override
	public URL getTileUrl(int x, int y, int z) {
		try {
			return new URL(String.format(tileUrl, z, x, y));
		} catch (MalformedURLException e) {
			return null;
		}
	}

}
