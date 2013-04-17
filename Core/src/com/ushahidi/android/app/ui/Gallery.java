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
package com.ushahidi.android.app.ui;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * A layout that displays a ViewPager with its children that are outside the
 * typical pager bounds.
 * 
 * Credit: https://gist.github.com/devunwired/8cbe094bb7a783e37ad1
 * 
 */
public class Gallery extends FrameLayout implements
		ViewPager.OnPageChangeListener {

	private ViewPager mPager;
	private boolean mNeedsRedraw = false;

	public Gallery(Context context) {
		super(context);
		init();
	}

	public Gallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Gallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// Disable clipping of children so non-selected pages are visible
		setClipChildren(false);

		// Child clipping doesn't work with hardware acceleration in Android
		// 3.x/4.x
		// You need to set this value here if using hardware acceleration in an
		// application targeted at these releases.
		// if (Build.VERSION.SDK_INT >= 11) {
		// setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// }
	}

	@Override
	protected void onFinishInflate() {
		try {
			mPager = (ViewPager) getChildAt(0);
			mPager.setOnPageChangeListener(this);
		} catch (Exception e) {
			throw new IllegalStateException(
					"The root child of PagerContainer must be a ViewPager");
		}
	}

	public ViewPager getViewPager() {
		return mPager;
	}

	private Point mCenter = new Point();
	private Point mInitialTouch = new Point();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCenter.x = w / 2;
		mCenter.y = h / 2;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// We capture any touches not already handled by the ViewPager
		// to implement scrolling from a touch outside the pager bounds.
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mInitialTouch.x = (int) ev.getX();
			mInitialTouch.y = (int) ev.getY();
		default:
			ev.offsetLocation(mCenter.x - mInitialTouch.x, mCenter.y
					- mInitialTouch.y);
			break;
		}

		return mPager.dispatchTouchEvent(ev);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// Force the container to redraw on scrolling.
		// Without this the outer pages render initially and then stay static
		if (mNeedsRedraw)
			invalidate();
	}

	@Override
	public void onPageSelected(int position) {
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
	}
}
