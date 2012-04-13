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

package com.ushahidi.android.app.ui.phone;

import java.util.List;

import android.os.Bundle;
import android.support.v4.view.MenuItem;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.activities.BaseViewActivity;
import com.ushahidi.android.app.models.ListReportVideoModel;
import com.ushahidi.android.app.views.ReportVideoView;

/**
 * @author eyedol
 */
public class ViewReportVideoActivity extends
		BaseViewActivity<ReportVideoView, ListReportVideoModel> {

	private ListReportVideoModel video;

	private List<ListReportVideoModel> listVideos;

	private int position;

	private int reportId;

	public ViewReportVideoActivity() {
		super(ReportVideoView.class, R.layout.video, R.menu.view_media);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		video = new ListReportVideoModel();
		view = new ReportVideoView(this);

		this.reportId = getIntent().getExtras().getInt("reportid", 0);
		this.position = getIntent().getExtras().getInt("position", 0);
		initReport(this.position);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.menu_forward) {

			goNext();
			return true;

		} else if (item.getItemId() == R.id.menu_backward) {

			goPrevious();
			return true;

		} else if (item.getItemId() == R.id.menu_share) {
			share();
		}

		return super.onOptionsItemSelected(item);
	}

	private void goNext() {
		if (listVideos != null) {
			position++;
			if (!(position > (listVideos.size() - 1))) {
				view.goNext(listVideos.get(position).getVideo());

				int page = position;
				this.setTitle(page + 1);

			} else {
				position = listVideos.size() - 1;
			}
		}
	}

	private void goPrevious() {
		if (listVideos != null) {
			position--;
			if ((position < (listVideos.size() - 1)) && (position != -1)) {
				view.goPrevious(listVideos.get(position).getVideo());

				int page = position;
				this.setTitle(page + 1);
			} else {
				position = 0;
			}
		}
	}

	private void initReport(int position) {
		listVideos = video.getVideosByReportId(reportId);
		if (view.webView != null) {
			if (listVideos != null) {
				view.url = listVideos.get(position).getVideo();
				view.setWebView();

				int page = position;
				this.setTitle(page + 1);
			}
		}
	}

	public void setTitle(int page) {
		final StringBuilder title = new StringBuilder(String.valueOf(page));
		title.append("/");
		if (listVideos != null)
			title.append(listVideos.size());
		setActionBarTitle(title.toString());
	}

	private void share() {
		final String shareString = getString(R.string.share_template, " ",
				" \n" + view.url);
		shareText(shareString);

	}

}