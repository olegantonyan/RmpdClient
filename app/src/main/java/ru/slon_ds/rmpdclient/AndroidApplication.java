package ru.slon_ds.rmpdclient;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.github.anrwatchdog.ANRWatchDog;

public class AndroidApplication extends Application {
    private static Context _context = null;

    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
        setup_watchdogs();
    }

    public static Context context() {
        return _context;
    }

    public static String user_agent() {
        /*String hw = "board:" + Build.BOARD + "," +
                "bootloader:" + Build.BOOTLOADER + "," +
                "brand:" + Build.BRAND + "," +
                "device:" + Build.DEVICE + "," +
                "display:" + Build.DISPLAY + "," +
                "fingerprint:" + Build.FINGERPRINT + "," +
                "hardware:" + Build.HARDWARE + "," +
                "host:" + Build.HOST + "," +
                "id:" + Build.ID + "," +
                "manufacturer:" + Build.MANUFACTURER + "," +
                "model:" + Build.MODEL + "," +
                "product:" + Build.PRODUCT + "," +
                "radio:" + Build.getRadioVersion() +
                "serial:" + Build.SERIAL + "," +
                "tags:" + Build.TAGS + "," +
                "command:" + Build.TYPE + "," +
                "user:" + Build.USER;*/
        return "rmpd-android " + version() + " " + System.getProperty("http.agent") + " [" + System.getProperty("os.version") + "] " + Build.BRAND + " " + Build.MODEL;
    }

    public static String version() {
        Context ctx = context();
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e ){
            return "NULL";
        }
    }

    private void setup_watchdogs() {
        DefaultUncaughtExceptionHandler exception_handler = new DefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(exception_handler);

        ANRWatchDog anr_wdt = new ANRWatchDog(15000);
        anr_wdt.setANRListener(exception_handler);
        anr_wdt.start();
    }
}
