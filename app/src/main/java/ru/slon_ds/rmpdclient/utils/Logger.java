package ru.slon_ds.rmpdclient.utils;

import android.util.Log;

import ru.slon_ds.rmpdclient.AndroidApplication;

public class Logger {
    public static void info(Object source, String message) {
        Log.i(tag(source), message);
    }

    public static void warning(Object source, String message) {
        Log.w(tag(source), message);
    }

    public static void error(Object source, String message) {
        Log.e(tag(source), message);
    }

    public static void debug(Object source, String message) {
        Log.d(tag(source), message);
    }

    public static void exception(Object source, String message, Throwable exception) {
        Log.e(tag(source), message, exception);
    }

    private static String tag(Object source) {
        if (source instanceof String) {
            return (String) source;
        }
        return source.getClass().getName().replace(AndroidApplication.context().getPackageName(), "");
    }
}
