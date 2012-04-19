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
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;

import com.commonsware.cwac.sacklist.SackOfViewsAdapter;
import com.ushahidi.android.app.models.Model;

/**
 * Credits:
 * http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-
 * android-09/
 * 
 * Adapted to fit this project.
 * 
 */
public abstract class BaseSectionListAdapter<M extends Model> extends
		BaseAdapter implements SectionIndexer {
	protected ArrayList<ListAdapter> pieces = new ArrayList<ListAdapter>();
	protected final Context context;
	protected final LayoutInflater inflater;

	/**
	 * Stock constructor, simply chaining to the superclass.
	 */
	public BaseSectionListAdapter(Context context) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	/**
	 * Adds a new adapter to the roster of things to appear in the aggregate
	 * list.
	 * 
	 * @param adapter
	 *            Source for row views for this section
	 */
	public void addAdapter(ListAdapter adapter) {
		pieces.add(adapter);
		adapter.registerDataSetObserver(new CascadeDataSetObserver());
	}

	/**
	 * Adds a new View to the roster of things to appear in the aggregate list.
	 * 
	 * @param view
	 *            Single view to add
	 */
	public void addView(View view) {
		addView(view, false);
	}

	/**
	 * Adds a new View to the roster of things to appear in the aggregate list.
	 * 
	 * @param view
	 *            Single view to add
	 * @param enabled
	 *            false if views are disabled, true if enabled
	 */
	public void addView(View view, boolean enabled) {
		ArrayList<View> list = new ArrayList<View>(1);

		list.add(view);

		addViews(list, enabled);
	}

	/**
	 * Adds a list of views to the roster of things to appear in the aggregate
	 * list.
	 * 
	 * @param views
	 *            List of views to add
	 */
	public void addViews(List<View> views) {
		addViews(views, false);
	}

	/**
	 * Adds a list of views to the roster of things to appear in the aggregate
	 * list.
	 * 
	 * @param views
	 *            List of views to add
	 * @param enabled
	 *            false if views are disabled, true if enabled
	 */
	public void addViews(List<View> views, boolean enabled) {
		if (enabled) {
			addAdapter(new EnabledSackAdapter(views));
		} else {
			addAdapter(new SackOfViewsAdapter(views));
		}
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 * 
	 * @param position
	 *            Position of the item whose data we want
	 */
	@Override
	public Object getItem(int position) {
		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				return (piece.getItem(position));
			}

			position -= size;
		}

		return (null);
	}

	/**
	 * Get the adapter associated with the specified position in the data set.
	 * 
	 * @param position
	 *            Position of the item whose adapter we want
	 */
	public ListAdapter getAdapter(int position) {
		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				return (piece);
			}

			position -= size;
		}

		return (null);
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 */
	@Override
	public int getCount() {
		int total = 0;

		for (ListAdapter piece : pieces) {
			total += piece.getCount();
		}

		return (total);
	}

	/**
	 * Returns the number of types of Views that will be created by getView().
	 */
	@Override
	public int getViewTypeCount() {
		int total = 0;

		for (ListAdapter piece : pieces) {
			total += piece.getViewTypeCount();
		}

		return (Math.max(total, 1)); // needed for setListAdapter() before
										// content add'
	}

	/**
	 * Get the type of View that will be created by getView() for the specified
	 * item.
	 * 
	 * @param position
	 *            Position of the item whose data we want
	 */
	@Override
	public int getItemViewType(int position) {
		int typeOffset = 0;
		int result = -1;

		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				result = typeOffset + piece.getItemViewType(position);
				break;
			}

			position -= size;
			typeOffset += piece.getViewTypeCount();
		}

		return (result);
	}

	/**
	 * Are all items in this ListAdapter enabled? If yes it means all items are
	 * selectable and clickable.
	 */
	@Override
	public boolean areAllItemsEnabled() {
		return (false);
	}

	/**
	 * Returns true if the item at the specified position is not a separator.
	 * 
	 * @param position
	 *            Position of the item whose data we want
	 */
	@Override
	public boolean isEnabled(int position) {
		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				return (piece.isEnabled(position));
			}

			position -= size;
		}

		return (false);
	}

	/**
	 * Get a View that displays the data at the specified position in the data
	 * set.
	 * 
	 * @param position
	 *            Position of the item whose data we want
	 * @param convertView
	 *            View to recycle, if not null
	 * @param parent
	 *            ViewGroup containing the returned View
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {

				return (piece.getView(position, convertView, parent));
			}

			position -= size;
		}

		return (null);
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 * 
	 * @param position
	 *            Position of the item whose data we want
	 */
	@Override
	public long getItemId(int position) {
		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				return (piece.getItemId(position));
			}

			position -= size;
		}

		return (-1);
	}

	@Override
	public int getPositionForSection(int section) {
		int position = 0;

		for (ListAdapter piece : pieces) {
			if (piece instanceof SectionIndexer) {
				Object[] sections = ((SectionIndexer) piece).getSections();
				int numSections = 0;

				if (sections != null) {
					numSections = sections.length;
				}

				if (section < numSections) {
					return (position + ((SectionIndexer) piece)
							.getPositionForSection(section));
				} else if (sections != null) {
					section -= numSections;
				}
			}

			position += piece.getCount();
		}

		return (0);
	}

	@Override
	public int getSectionForPosition(int position) {
		int section = 0;

		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				if (piece instanceof SectionIndexer) {
					return (section + ((SectionIndexer) piece)
							.getSectionForPosition(position));
				}

				return (0);
			} else {
				if (piece instanceof SectionIndexer) {
					Object[] sections = ((SectionIndexer) piece).getSections();

					if (sections != null) {
						section += sections.length;
					}
				}
			}

			position -= size;
		}

		return (0);
	}

	@Override
	public Object[] getSections() {
		ArrayList<Object> sections = new ArrayList<Object>();

		for (ListAdapter piece : pieces) {
			if (piece instanceof SectionIndexer) {
				Object[] curSections = ((SectionIndexer) piece).getSections();

				if (curSections != null) {
					for (Object section : curSections) {
						sections.add(section);
					}
				}
			}
		}

		if (sections.size() == 0) {
			return (new String[0]);
		}

		return (sections.toArray(new Object[0]));
	}

	private static class EnabledSackAdapter extends SackOfViewsAdapter {
		public EnabledSackAdapter(List<View> views) {
			super(views);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return (true);
		}

		@Override
		public boolean isEnabled(int position) {
			return (true);
		}
	}

	private class CascadeDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			notifyDataSetInvalidated();
		}
	}

	public abstract void refresh();

	protected void log(String message) {
		Log.i(getClass().getName(), message);
	}

	protected void log(String format, Object... args) {
		Log.i(getClass().getName(), String.format(format, args));
	}

	protected void log(String message, Exception ex) {
		Log.e(getClass().getName(), message, ex);
	}
}
