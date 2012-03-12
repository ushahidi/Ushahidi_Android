
package com.ushahidi.android.app.views;

import com.ushahidi.android.app.R;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

public class AboutView extends com.ushahidi.android.app.views.View {

    private ImageButton searchButton;

    private TextView version;

    public AboutView(FragmentActivity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        searchButton = (ImageButton)activity.findViewById(R.id.search_report_btn);

        if (!TextUtils.isEmpty(activity.getString(R.string.deployment_url))) {
            searchButton.setVisibility(View.GONE);
        } else {
            searchButton.setVisibility(View.VISIBLE);
        }
        // VERSION
        version = (TextView)activity.findViewById(R.id.version);
        try {
            version.setText(activity.getPackageManager().getPackageInfo(activity.getPackageName(),
                    0).versionName);
        } catch (NameNotFoundException e) {
            Log.e("About", "NameNotFoundException", e);
            version.setText("");
        }

        // BUTTONS
        setButtonVisibility((Button)activity.findViewById(R.id.media_link),
                activity.getString(R.string.media_url), activity);
        setButtonVisibility((Button)activity.findViewById(R.id.team_link),
                activity.getString(R.string.team_url), activity);
        setButtonVisibility((Button)activity.findViewById(R.id.twitter_link),
                activity.getString(R.string.twitter_url), activity);
        setButtonVisibility((Button)activity.findViewById(R.id.facebook_link),
                activity.getString(R.string.facebook_url), activity);
        setButtonVisibility((Button)activity.findViewById(R.id.contact_link),
                activity.getString(R.string.contact_url), activity);
    }

    private void setButtonVisibility(final Button button, final String url,
            final FragmentActivity activity) {
        if (!TextUtils.isEmpty(url)) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    activity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri
                            .parse(url)));
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }
}
