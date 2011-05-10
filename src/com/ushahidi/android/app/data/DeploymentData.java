
package com.ushahidi.android.app.data;

import java.io.IOException;

import android.util.Log;

/**
 * Contains logic to load the details of a deployment and find a list of
 * matching deployments given a query. Everything is held in SQLite database;
 */
public class DeploymentData {

    private static final DeploymentData sInstance = new DeploymentData();

    public static DeploymentData getInstance() {
        return sInstance;
    }

    // private final Map<String, List<Deployment>> mDeployed = new
    // ConcurrentHashMap<String, List<Deployment>>();

    private DeploymentData() {

    }

    private boolean mLoaded = false;

    /**
     * Loads the deployments and its details if they haven't been loaded
     * already.
     */
    public synchronized void ensureLoaded(final String name) {
        if (mLoaded)
            return;

        new Thread(new Runnable() {
            public void run() {
                try {
                    loadDeployments(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private synchronized void loadDeployments(String name) throws IOException {
        if (mLoaded)
            return;

        Log.d("dict", "loading words");

        try {
            fetchDeploymentByName(name);
        } finally {

        }
        mLoaded = true;
    }

    // get checkins from the db
    public void fetchDeploymentByName(String _name) {

        // Cursor cursor;

        /*
         * cursor = UshahidiApplication.mDb.fetchDeploymentByName(_name); String
         * name; String url; String desc; if (cursor.moveToFirst()) { int
         * idIndex =
         * cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_ID); int
         * urlIndex =
         * cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_URL); int
         * nameIndex =
         * cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_NAME); int
         * descIndex =
         * cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_DESC); do {
         * int id = Util.toInt(cursor.getString(idIndex)); name =
         * cursor.getString(nameIndex); desc = cursor.getString(descIndex); url
         * = cursor.getString(urlIndex);
         * addDeployment(String.valueOf(id),name,desc,url);
         * Log.d("Deployment","Name: "+name+" Desc: "+desc); } while
         * (cursor.moveToNext()); } cursor.close();
         */

    }
}
