package ru.slon_ds.rmpdclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

import ru.slon_ds.rmpdclient.utils.Logger;

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler, Runnable {
    private Activity activity = null;

    public DefaultUncaughtExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String text = String.format(Locale.US, "oops! unhandled exception in thread %s/%d, restarting", thread.getName(), thread.getId());
        Logger.exception(this, text, throwable);
        set_after_crash_state(true);
        activity.runOnUiThread(this);
    }

    @Override
    public void run() {
        Intent me = AndroidApplication.context().getPackageManager().getLaunchIntentForPackage(AndroidApplication.context().getPackageName());
        PendingIntent pi = PendingIntent.getActivity(AndroidApplication.context(), 0, me, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) AndroidApplication.context().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pi);

        activity.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(2);
    }

    public static void set_after_crash_state(boolean crash) {
        final Context ctx = AndroidApplication.context();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("after_crash", crash);
        editor.commit();
    }

    public static boolean is_after_crash() {
        final Context ctx = AndroidApplication.context();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("after_crash", false);
    }
}
