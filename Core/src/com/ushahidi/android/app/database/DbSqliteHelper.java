
package com.ushahidi.android.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.util.DbUtils;

public abstract class DbSqliteHelper extends SQLiteOpenHelper {

    public DbSqliteHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    protected abstract String getTableName();

    @Override
    public void onCreate(SQLiteDatabase db) {

        log("Creating database table for " + getTableName());
        String createStatement = DbUtils.getCreateTable(getTableName());
        try {
            db.execSQL(createStatement);
        } catch (SQLiteException ex) {
            if (ex.getMessage().startsWith("table " + getTableName() + " already exists: ")) {
                log("Table already exists: " + getTableName());
            } else {
                log("Error executing SQL: " + createStatement, ex);
                throw ex;
            }
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        log("Database version changed from " + oldVersion + " to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + getTableName());

        onCreate(db);
    }

    protected void log(String message) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        if (MainApplication.LOGGING_MODE)
            Log.e(getClass().getName(), message, ex);
    }

}
