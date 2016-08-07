package ru.slon_ds.rmpdclient;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

public class AndroidApplication extends Application {
    private static Context _context = null;

    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
    }

    public static Context context() {
        return _context;
    }

    public static String user_agent() {
        return "rmpd-android " + version() + " " + System.getProperty("http.agent");
    }

    public static String version() {
        Context ctx = context();
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e ){
            return "NULL";
        }
    }
}
