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

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.models.ListCommentModel;

/**
 * @author eyedol
 */
public class ListCommentAdapter extends BaseListAdapter<ListCommentModel> {

    /**
     * @param context
     */
    public ListCommentAdapter(Context context) {
        super(context);
    }

    private ListCommentModel mListCommentModel;

    private List<ListCommentModel> items;

    private int totalComments;

    class Widgets {
        TextView commentAuthor;
        TextView commentDate;
        TextView commentDescription;
        TextView total;

        public Widgets(View view) {

            this.commentAuthor = (TextView) view
                    .findViewById(R.id.comment_author);
            this.commentDate = (TextView) view.findViewById(R.id.comment_date);
            this.commentDescription = (TextView) view
                    .findViewById(R.id.comment_description);
            this.total = (TextView) view.findViewById(R.id.comment_total);

        }

    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_comment_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        // FIXME: only show the first item for now. In the future only get one
        // item
        widgets.commentAuthor.setText(getItem(position).getCommentAuthor());
        widgets.commentDate.setText(
                getItem(position).getCommentDate());
        widgets.commentDescription.setText(getItem(position)
                .getCommentDescription());
        widgets.total.setText(context.getResources().getQuantityString(
                R.plurals.no_of_comments, totalComments, totalComments));
        return view;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ushahidi.android.app.adapters.BaseListAdapter#refresh(android.content
     * .Context)
     */
    @Override
    public void refresh() {

    }

    public void refresh(int reportId) {
        mListCommentModel = new ListCommentModel();
        final boolean loaded = mListCommentModel.load(reportId);
        totalComments = mListCommentModel.totalComments();
        if (loaded) {
            items = mListCommentModel.getComments();
            this.setItems(items);
        }
    }

    public void refreshCheckinComment(int checkinId) {
        mListCommentModel = new ListCommentModel();
        final boolean loaded = mListCommentModel.loadCheckinComment(checkinId);
        totalComments = mListCommentModel.totalComments();
        if (loaded) {
            items = mListCommentModel.getComments();
            this.setItems(items);
        }
    }

}
