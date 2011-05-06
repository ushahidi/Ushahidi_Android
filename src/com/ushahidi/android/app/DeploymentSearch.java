
package com.ushahidi.android.app;

import android.app.SearchManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.ushahidi.android.app.data.DeploymentProvider;
import com.ushahidi.android.app.data.UshahidiDatabase;

public class DeploymentSearch extends DashboardActivity {
    private TextView mTextView;

    private ListView mListView;

    private static final int MENU_SEARCH = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deployment_search);
        setTitleFromActivityLabel(R.id.title_text);

        mTextView = (TextView)findViewById(R.id.search_deploy);
        mListView = (ListView)findViewById(R.id.deployment_list);

        showResults();
        mTextView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Cursor cursor = showResults(mTextView.getText().toString());

                // Specify the columns we want to display in the result
                String[] from = new String[] {
                        UshahidiDatabase.DEPLOYMENT_NAME, UshahidiDatabase.DEPLOYMENT_DESC,
                        UshahidiDatabase.DEPLOYMENT_URL
                };

                // Specify the corresponding layout elements where we want the
                // columns to go
                int[] to = new int[] {
                        R.id.deploy_name, R.id.deploy_desc, R.id.deploy_url
                };

                // Create a simple cursor adapter for the details of the deployment and apply
                // them to the ListView
                if (cursor != null) {
                    SimpleCursorAdapter deployments = new SimpleCursorAdapter(
                            DeploymentSearch.this, R.layout.deployment_search_result, cursor, from,
                            to);
                    mListView.setAdapter(deployments);
                } else {
                    showResults();
                }

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SEARCH, 0, R.string.search_label)
                .setIcon(android.R.drawable.ic_search_category_default)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEARCH:
                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Searches the dictionary and displays results for the given query.
     * 
     * @param query The search query
     */
    private Cursor showResults(String query) {

        Cursor cursor = managedQuery(DeploymentProvider.CONTENT_URI, null, null, new String[] {
            query
        }, null);

        return cursor;
    }

    /**
     * Searches the dictionary and displays results for the given query.
     * 
     * @param query The search query
     */
    private void showResults() {

        Cursor cursor = UshahidiApplication.mDb.fetchAllDeployments();

        if (cursor != null) {
            // There are no results

            // Specify the columns we want to display in the result
            String[] from = new String[] {
                    UshahidiDatabase.DEPLOYMENT_NAME, UshahidiDatabase.DEPLOYMENT_DESC,
                    UshahidiDatabase.DEPLOYMENT_URL
            };

            // Specify the corresponding layout elements where we want the
            // columns to go
            int[] to = new int[] {
                    R.id.deployment_list_name, R.id.deployment_list_desc, R.id.deployment_list_url
            };

            // Create a simple cursor adapter for the definitions and apply them
            // to the ListView
            SimpleCursorAdapter deployments = new SimpleCursorAdapter(this,
                    R.layout.deployment_list, cursor, from, to);
            mListView.setAdapter(deployments);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO: do something when the list item is clicked.
                }
            });
        }
    }

}
