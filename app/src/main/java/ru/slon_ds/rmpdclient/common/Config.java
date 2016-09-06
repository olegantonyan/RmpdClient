package ru.slon_ds.rmpdclient.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.R;

public class Config {
    private static Config _instance = new Config();

    public static Config instance() {
        return _instance;
    }

    private Config() {
    }

    public void load_defaults() {
        final Context ctx = AndroidApplication.context();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        final Resources res = ctx.getResources();
        final String pref_key_storage_path = res.getString(R.string.pref_key_storage_path);
        if (preferences.getString(pref_key_storage_path, null) == null) {
            editor.putString(pref_key_storage_path, Files.base_storage_path());
            editor.apply();
        }
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_network, false);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_logging, false);
    }

    public boolean first_run() {
        boolean result = preferences().getBoolean("firstrun", true);
        if (result) {
            SharedPreferences.Editor editor = preferences().edit();
            editor.putBoolean("firstrun", false);
            editor.apply();
        }
        return result;
    }

    public boolean autostart() {
        return preferences().getBoolean(getResources().getString(R.string.pref_key_autostart), true);
    }

    public String server_url() {
        return preferences().getString(getResources().getString(R.string.pref_key_server_uri), getResources().getString(R.string.pref_default_server_uri));
    }

    public String login() {
        return preferences().getString(getResources().getString(R.string.pref_key_login), getResources().getString(R.string.pref_default_login));
    }

    public String password() {
        return preferences().getString(getResources().getString(R.string.pref_key_password), getResources().getString(R.string.pref_default_password));
    }

    public String storage_path() {
        String result = preferences().getString(getResources().getString(R.string.pref_key_storage_path), null);
        if (result == null) {
            result = Files.base_storage_path();
        }
        return result;
    }

    public boolean verbose_logging() {
        return preferences().getBoolean(getResources().getString(R.string.pref_key_verbose_logging), false);
    }

    private SharedPreferences preferences() {
        final Context ctx = AndroidApplication.context();
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private Resources getResources() {
        final Context ctx = AndroidApplication.context();
        return ctx.getResources();
    }
}