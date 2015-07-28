package com.ushahidi.android.app.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ushahidi.android.app.R;
import com.ushahidi.android.app.util.Util;

public class AboutView extends com.ushahidi.android.app.views.View {

	private TextView version;

	public AboutView(Activity activity) {
		super(activity);
		
		// BUTTONS
		
		setButtonVisibility((Button) activity.findViewById(R.id.team_link),
				activity.getString(R.string.team_url), activity);
		
		setButtonVisibility((Button) activity.findViewById(R.id.contact_link),
				activity.getString(R.string.contact_url), activity);
	}

	public AboutView(ViewGroup viewGroup, Context context) {
		super(viewGroup);

		
		// BUTTONS
	
		setButtonVisibility((Button) viewGroup.findViewById(R.id.team_link),
				context.getString(R.string.team_url), context);
		
		setButtonVisibility((Button) viewGroup.findViewById(R.id.contact_link),
				context.getString(R.string.contact_url), context);
	}

	private void setButtonVisibility(final Button button, final String url,
			final Context context) {
		if (!TextUtils.isEmpty(url)) {
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					context.startActivity(new Intent(
							android.content.Intent.ACTION_VIEW, Uri.parse(url)));
				}
			});
		} else {
			button.setVisibility(View.GONE);
		}
	}

}
