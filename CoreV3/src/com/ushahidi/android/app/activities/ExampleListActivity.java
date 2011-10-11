package com.ushahidi.android.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.adapters.ExampleListAdapter;
import com.ushahidi.android.app.models.ExampleModel;
import com.ushahidi.android.app.tasks.ProgressQueue;
import com.ushahidi.android.app.tasks.ProgressTask;
import com.ushahidi.android.app.views.ExampleListView;

/**
 * Example list activity for ExampleModels that inherits from BaseListActivity
 */
public class ExampleListActivity extends BaseListActivity<ExampleListView, ExampleModel, ExampleListAdapter> {

    protected ExampleListActivity() {
        super(ExampleListView.class,
              ExampleListAdapter.class,
              R.layout.example_list,
              R.menu.example,
              R.id.example_list_table);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Example of progress queue for executing asynctasks
        //which will execute TaskOne, then TaskTwo
        new ProgressQueue(
            new TaskOne(this),
            new TaskTwo(this)
        ).execute();
    }

    @Override
    protected void onLoaded() {
        log("onLoaded");
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        toastShort("onItemClick %d", position);
    }

    /**
     * Example of a ProgressTask
     */
    class TaskOne extends ProgressTask {

        public TaskOne(Activity activity) {
            super(activity, R.string.loading_);
            //pass custom loading message to super call
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * Another example of a ProgressTask
     */
    class TaskTwo extends ProgressTask {

        public TaskTwo(Activity activity) {
            super(activity, R.string.loading_);
            //pass custom loading message to super call
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
