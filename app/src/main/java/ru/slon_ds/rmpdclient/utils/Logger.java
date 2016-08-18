package ru.slon_ds.rmpdclient.utils;

import android.util.Log;

import java.util.Locale;

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
        String result;
        if (source instanceof String) {
            result = (String) source;
        } else {
            result = source.getClass().getName().replace(AndroidApplication.context().getPackageName(), "");
        }
        return result + thread_info();
    }

    private static String thread_info() {
        return String.format(Locale.US, "[%s]", Thread.currentThread().getName());
    }
}
