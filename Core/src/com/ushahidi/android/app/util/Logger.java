
package com.ushahidi.android.app.util;

import android.util.Log;

public class Logger {

    private String tag;

    public Logger(String tag) {
        this.tag = tag;
    }

    public void info(String message) {

        Log.i(this.tag, message);
    }

    public void warn(String message) {

        Log.w(this.tag, message);
    }

    public void trace(String message) {

        Log.d(this.tag, message);
    }

    public void error(String message, Throwable t) {

        Log.e(this.tag, message, t);
    }

    // > STATIC FACTORIES
    public static Logger getLogger(Object object) {
        return getLogger(object.getClass());
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }
}
