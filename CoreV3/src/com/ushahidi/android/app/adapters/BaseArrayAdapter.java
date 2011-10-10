package com.ushahidi.android.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import com.ushahidi.android.app.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseArrayAdapter
 *
 * Base class for all String ArrayAdapters
 *
 * @param <M> Model class
 */
public abstract class BaseArrayAdapter<M extends BaseModel> extends ArrayAdapter<String> {

    protected final LayoutInflater inflater;
	protected final List<M> tags;
    protected int notSpecified = -1;
    protected static final int NOT_SPECIFIED = -1;

    public BaseArrayAdapter(Context context) {
        this(context, NOT_SPECIFIED);
    }

	public BaseArrayAdapter(Context context, Integer notSpecified) {
		super(context, android.R.layout.simple_spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.notSpecified = notSpecified;
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

	public abstract void refresh(Context context);

    protected void log(String message) {
        Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object...args) {
        Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        Log.e(getClass().getName(), message, ex);
    }
}
