package ru.slon_ds.rmpdclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public class UsbAttachedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        throw new RuntimeException("USB OTG attached: " + (device == null ? "" : device.toString())); // causing restart
    }
}