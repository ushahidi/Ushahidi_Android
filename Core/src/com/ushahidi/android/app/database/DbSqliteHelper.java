package com.ushahidi.android.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.ushahidi.android.app.util.DbUtils;
import com.ushahidi.android.app.util.Logger;

public abstract class DbSqliteHelper extends SQLiteOpenHelper {

    protected Logger log = Logger.getLogger(this);
    
    public DbSqliteHelper(Context context, String name,  int version) {
        super(context, name, null, version);
    }
    
    protected abstract String getTableName();
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        log.info("Creating database table for " + getTableName());
        String createStatement = DbUtils.getCreateTable(getTableName());
        try {
            db.execSQL(createStatement);
        }catch(SQLiteException ex){
            if(ex.getMessage().startsWith("table " + getTableName() + " already exists: ")) {
                log.info("Table already exists: " + getTableName());
            } else {
                log.error("Error executing SQL: " + createStatement, ex);
                throw ex;
            }
        }
      
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        log.info("Database version changed from " + oldVersion + " to " + newVersion);
       
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        
        onCreate(db);
    }

}
