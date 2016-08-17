package ru.slon_ds.rmpdclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.slon_ds.rmpdclient.utils.Config;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Config.instance().autostart()) {
            return;
        }
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
