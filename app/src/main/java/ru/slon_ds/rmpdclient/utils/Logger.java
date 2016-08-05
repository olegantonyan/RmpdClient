package ru.slon_ds.rmpdclient.utils;

import android.util.Log;

public class Logger {
    private static final String ANDROID_LOG_TAG = "rmpd_client";

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
        return source.getClass().getName();
    }
}
