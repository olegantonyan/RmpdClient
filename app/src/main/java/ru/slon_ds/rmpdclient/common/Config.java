package ru.slon_ds.rmpdclient.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.FileInputStream;
import java.util.Properties;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.R;

public class Config {
    private static Config _instance = null;

    public static Config instance() {
        if (_instance == null) {
            _instance = new Config();
        }
        return _instance;
    }

    private Config() {
        load_defaults();
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

    public boolean load_preconfigured() {
        try {
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            final String filepath = path + "/rmpd_preconfig";
            Properties props = new Properties();
            props.load(new FileInputStream(filepath));

            SharedPreferences.Editor editor = preferences().edit();

            Resources res = getResources();
            final String keys[] = {res.getString(R.string.pref_key_server_uri),
                    res.getString(R.string.pref_key_login),
                    res.getString(R.string.pref_key_password)};
            for (String i : keys) {
                final String value = props.getProperty(i);
                if (value != null) {
                    editor.putString(i, value);
                }
            }
            editor.apply();
            return true;
        } catch (Exception e) {
            Logger.exception(this, "error loading pre-loaded config", e);
            return false;
        }
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

    public float brightness() {
        String result = preferences().getString(getResources().getString(R.string.pref_key_brightness), getResources().getString(R.string.pref_default_brightness));
        try {
            float f = Float.parseFloat(result) / 100F;
            if (f > 1F) {
                return 1F;
            }
            return f;
        } catch (Exception e) {
            return -1F;
        }
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
