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
package com.ushahidi.android.app.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.PhotoUtils;

/**
 * An ImageView with rounded corner.
 * <p>
 * The corner radius is set by the attribute <code>cornerRadius</code> in the
 * layout XML.
 * 
 * @author umbalaconmeogia
 * 
 */
public class RoundedCornerImageView extends ImageView {

	/**
	 * The corner radius of the view (in pixel).
	 */
	private float cornerRadius;

	/**
	 * The corner radius of the bitmap.
	 */
	private float bitmapCornerRadius;

	public RoundedCornerImageView(Context context) {
		super(context);
	}

	public RoundedCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getXMLAttribute(context, attrs);
	}

	public RoundedCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		getXMLAttribute(context, attrs);
	}

	/**
	 * Get parameters in xml layout.
	 * 
	 * @param context
	 * @param attrs
	 */
	private void getXMLAttribute(Context context, AttributeSet attrs) {
		// Get proportion.
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RoundedCornerImageView);
		cornerRadius = a.getDimension(
				R.styleable.RoundedCornerImageView_cornerRadius, 0);
		a.recycle();
	}

	/**
	 * Set corner radius.
	 * 
	 * @param radius
	 *            Corder radius in pixel.
	 */
	public void setCornerRadius(int radius) {
		this.cornerRadius = radius;
	}

	/**
	 * Source:
	 * http://stackoverflow.com/questions/2459916/how-to-make-an-imageview
	 * -to-have-rounded-corners
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		// Remember if new rounded corner bitmap is set to the drawable.
		// If the rounded corner bitmap is already set to the drawable, then we
		// call
		// super.onDraw() to draw the view.
		// Else, we create the rounded corner bitmap and set it to the drawable,
		// and super.onDraw() will be called later.
		boolean applyCornerToBitmap = false;

		Drawable drawable = getDrawable();
		if (drawable != null) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			int viewWidth = getWidth();
			if (viewWidth != 0f) { // Only round the corner if view width is not
									// zero.
				// Calculate the corner radius on the real bitmap, based on the
				// corner radius of the view.
				int bitmapWidth = bitmap.getWidth();
				float newBitmapCornerRadius = cornerRadius * bitmapWidth
						/ viewWidth;

				// If newBitmapCornerRadius equals to bitmapCornerRadius,
				// then it is not needed to set the round the corner bitmap
				// to the drawable again.
				if (bitmapCornerRadius != newBitmapCornerRadius) {
					applyCornerToBitmap = true;
					// Create bitmap with rounded corner.
					int bitmapHeight = bitmap.getHeight();
					bitmapCornerRadius = newBitmapCornerRadius;
					bitmap = PhotoUtils.getRoundedCornerBitmap(bitmap,
							bitmapCornerRadius, bitmapWidth, bitmapHeight,
							false, false, false, false);
					// Set rounded corner bitmap to the view's drawable.
					setImageBitmap(bitmap); // This will call onDraw() again.
				}
			}
		}
		// Call super onDraw() if the drawable has already been rounded.
		if (!applyCornerToBitmap) {
			super.onDraw(canvas);
		}
	}

}
