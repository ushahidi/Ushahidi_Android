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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.ushahidi.android.app.models.Model;
import com.ushahidi.android.app.util.Util;

/**
 * BaseArrayAdapter
 *
 * Base class for all String ArrayAdapters
 *
 * @param <M> Model class
 */
public abstract class BaseArrayAdapter<M extends Model> extends ArrayAdapter<String> {

    protected final LayoutInflater inflater;
	protected final List<M> tags;
    protected int notSpecified = -1;
    protected static final int NOT_SPECIFIED = -1;
    protected Context context;
    public BaseArrayAdapter(Context context) {
        this(context, NOT_SPECIFIED);
        this.context = context;
    }

	public BaseArrayAdapter(Context context, Integer notSpecified) {
		super(context, notSpecified);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.notSpecified = notSpecified;
        this.context = context;
        inflater = LayoutInflater.from(context);
		tags = new ArrayList<M>();
	}

	public int getPosition(M m) {
		return this.getPosition(m.toString());
	}

    @Override
	public void clear() {
		super.clear();
		tags.clear();
	}

	@Override
	public void add(String label) {
		super.add(label);
		tags.add(null);
	}

	public void add(String label, M m) {
		super.add(label);
		tags.add(m);
	}

	@Override
	public void insert(String object, int index) {
		super.insert(object, index);
	}

	public M getTag(int position) {
		return tags.get(position);
	}

	public abstract void refresh();

	protected void log(String message) {
		new Util().log(message);
	}

	protected void log(String format, Object... args) {
		new Util().log( String.format(format, args));	
	}

	protected void log(String message, Exception ex) {
		new Util().log(message, ex);
	}
}
