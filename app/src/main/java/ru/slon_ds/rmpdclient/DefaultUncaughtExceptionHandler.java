package ru.slon_ds.rmpdclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import java.util.Locale;

import ru.slon_ds.rmpdclient.utils.Logger;

public class DefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler, ANRWatchDog.ANRListener {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        String text = String.format(Locale.US, "oops! unhandled exception in thread %s/%d, restarting", thread.getName(), thread.getId());
        Logger.exception(this, text, throwable);
        restart();
    }

    public void restart() {
        set_after_crash_state(true);
        Intent me = AndroidApplication.context().getPackageManager().getLaunchIntentForPackage(AndroidApplication.context().getPackageName());
        PendingIntent pi = PendingIntent.getActivity(AndroidApplication.context(), 0, me, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) AndroidApplication.context().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pi);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(2);
    }

    @Override
    public void onAppNotResponding(ANRError error) {
        Logger.error(this, "oops! ANR caught");
        restart();
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
