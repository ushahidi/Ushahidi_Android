package com.ushahidi.android.app.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.database.Cursor;
import android.util.Log;

/**
 * Contains logic to load the details of a deployment and find a list of matching deployments
 * given a query.  Everything is held in SQLite database;
 */
public class DeploymentData {
    
    private static final DeploymentData sInstance = new DeploymentData();

    public static DeploymentData getInstance() {
        return sInstance;
    }

    private final Map<String, List<Deployment>> mDeployed = new ConcurrentHashMap<String, List<Deployment>>();

    private DeploymentData() {
    }

    private boolean mLoaded = false;

    /**
     * Loads the deployments and its details if they haven't been loaded already.
     *
     */
    public synchronized void ensureLoaded(final String name) {
        if (mLoaded) return;

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
        if (mLoaded) return;

        Log.d("dict", "loading words");
        
        try {
            fetchDeploymentByName(name);
        } finally {
            
        }
        mLoaded = true;
    }
    
 // get checkins from the db
    public void fetchDeploymentByName(String _name) {

        Cursor cursor;

        /*cursor = UshahidiApplication.mDb.fetchDeploymentByName(_name);
        String name;
        String url;
        String desc;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_ID);
            int urlIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_URL);
            int nameIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_NAME);
            int descIndex = cursor.getColumnIndexOrThrow(UshahidiDatabase.DEPLOYMENT_DESC);

            do {

                int id = Util.toInt(cursor.getString(idIndex));
                name = cursor.getString(nameIndex);
                desc = cursor.getString(descIndex);
                url =  cursor.getString(urlIndex);
                
                addDeployment(String.valueOf(id),name,desc,url);
                Log.d("Deployment","Name: "+name+" Desc: "+desc);
                
            } while (cursor.moveToNext());
        }

        cursor.close();*/
        
        
    }


    public List<Deployment> getMatches(String query) {
        //load query
        ensureLoaded(query);
        
        List<Deployment> list = mDeployed.get(query);
        return list == null ? Collections.EMPTY_LIST : list;
    }

    private void addDeployment(String id, String name, String description, String url) {
        final Deployment foundDeployment = new Deployment();
        foundDeployment.setId(id);
        foundDeployment.setName(name);
        foundDeployment.setUrl(url);
        
        final int len = name.length();
        for (int i = 0; i < len; i++) {
            final String prefix = name.substring(0, len - i);
            addMatch(prefix, foundDeployment);
        }
    }

    private void addMatch(String query, Deployment deployment) {
        List<Deployment> matches = mDeployed.get(query);
        if (matches == null) {
            matches = new ArrayList<Deployment>();
            mDeployed.put(query, matches);
        }
        matches.add(deployment);
    }
}
