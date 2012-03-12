
package com.ushahidi.android.app.views;

import com.ushahidi.android.app.R;

import android.content.Context;
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
import android.view.ViewGroup;

public class AboutView extends com.ushahidi.android.app.views.View {

    private ImageButton searchButton;

    private TextView version;

    public AboutView(FragmentActivity activity) {
        super(activity);
        // VERSION
        version = (TextView)activity.findViewById(R.id.version);
        try {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("v");
            sBuilder.append(activity.getPackageManager().getPackageInfo(activity.getPackageName(),
                    0).versionName);
            version.setText(sBuilder.toString());
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

    public AboutView(ViewGroup viewGroup, Context context) {
        super(viewGroup);
        // TODO Auto-generated constructor stub
        searchButton = (ImageButton)viewGroup.findViewById(R.id.search_report_btn);

        if (!TextUtils.isEmpty(context.getString(R.string.deployment_url))) {
            searchButton.setVisibility(View.GONE);
        } else {
            searchButton.setVisibility(View.VISIBLE);
        }
        // VERSION
        version = (TextView)viewGroup.findViewById(R.id.version);
        try {
            version.setText(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            Log.e("About", "NameNotFoundException", e);
            version.setText("");
        }

        // BUTTONS
        setButtonVisibility((Button)viewGroup.findViewById(R.id.media_link),
                context.getString(R.string.media_url), context);
        setButtonVisibility((Button)viewGroup.findViewById(R.id.team_link),
                context.getString(R.string.team_url), context);
        setButtonVisibility((Button)viewGroup.findViewById(R.id.twitter_link),
                context.getString(R.string.twitter_url), context);
        setButtonVisibility((Button)viewGroup.findViewById(R.id.facebook_link),
                context.getString(R.string.facebook_url), context);
        setButtonVisibility((Button)viewGroup.findViewById(R.id.contact_link),
                context.getString(R.string.contact_url), context);
    }

    private void setButtonVisibility(final Button button, final String url, final Context context) {
        if (!TextUtils.isEmpty(url)) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri
                            .parse(url)));
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }
}
