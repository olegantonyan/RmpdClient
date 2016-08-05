package ru.slon_ds.rmpdclient;

import android.app.Application;
import android.content.Context;

public class AndroidApplication extends Application {
    private static Context context = null;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
