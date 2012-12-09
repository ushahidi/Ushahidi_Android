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

package com.ushahidi.android.app.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.drawable.Drawable;

import com.ushahidi.android.app.models.Model;
import com.ushahidi.java.sdk.api.Category;
import com.ushahidi.java.sdk.api.Comment;
import com.ushahidi.java.sdk.api.Incident;
import com.ushahidi.java.sdk.api.Media;

public class ReportEntity extends Model implements IDbEntity {

	private int id;

	private int pending = 0;

	private String thumbnail;

	private Drawable arrow;

	private Incident incident;

	private List<Media> media;

	private List<Comment> comments;

	private List<Category> categories;

	public void setIncident(Incident incident) {
		this.incident = incident;
	}

	public void setMedia(List<Media> media) {
		this.media = media;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setArrow(Drawable arrow) {
		this.arrow = arrow;
	}

	public Drawable getArrow() {
		return this.arrow;
	}

	public String getThumbnail() {
		return this.thumbnail;
	}

	public int getPending() {
		return this.pending;
	}

	public Incident getIncident() {
		return this.incident;
	}

	public List<Media> getMedia() {
		return media;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public List<Category> getCategories() {
		return this.categories;
	}

	@Override
	public int getDbId() {
		return id;
	}

	@Override
	public void setDbId(int id) {
		this.id = id;
	}

}
