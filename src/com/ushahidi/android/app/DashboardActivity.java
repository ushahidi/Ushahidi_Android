
package com.ushahidi.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public abstract class DashboardActivity extends Activity {
    
    private static final int VIEW_SEARCH = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void onDestroy() {
        
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onRestart() {
        
        super.onRestart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        //finish();
        super.onStop();
    }

    public void onClickHome(View v) {
        goHome(this);
    }

    /**
     * Handle the click on the search button.
     * 
     * @param v View
     * @return void
     */

    public void onClickSearch(View v) {
        onSearchRequested();
    }
    
    public void onSearchDeployments( View v ) {
        Intent intent = new Intent(DashboardActivity.this, DeploymentSearch.class);
        startActivityForResult(intent, VIEW_SEARCH);
        setResult(RESULT_OK);
    }

    /**
     * Handle the click on the About button.
     * 
     * @param v View
     * @return void
     */
    public void onClickAdd(View v) {
        startActivity(new Intent(getApplicationContext(),Object.class));
    }
    
    /**
     * Handle the click on the refresh button.
     * 
     * @param v View
     * @return void
     */
    public void onRefreshReports(View v) {
        
    }
    
    /**
     * Go back to the home activity.
     * 
     * @param context Context
     * @return void
     */

    public void goHome(Context context) 
    {
        final Intent intent = new Intent(context, Ushahidi.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity (intent);
    }

    /**
     * Use the activity label to set the text in the activity's title text view.
     * The argument gives the name of the view.
     *
     * <p> This method is needed because we have a custom title bar rather than the default Android title bar.
     * See the theme definitons in styles.xml.
     * 
     * @param textViewId int
     * @return void
     */

    public void setTitleFromActivityLabel (int textViewId)
    {
        TextView tv = (TextView) findViewById (textViewId);
        if (tv != null) tv.setText (getTitle ());
    } // end setTitleText
}
