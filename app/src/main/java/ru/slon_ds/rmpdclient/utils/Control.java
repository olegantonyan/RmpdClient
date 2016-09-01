package ru.slon_ds.rmpdclient.utils;

import android.os.StatFs;

import java.io.DataOutputStream;

public class Control {
    public static Long free_space(String path) {
        try {
            StatFs stat = new StatFs(path);
            long block_size = stat.getBlockSize();
            long available_blocks = stat.getAvailableBlocks();
            return available_blocks * block_size;
        } catch (Exception e) {
            return 0L;
        }
    }

    public static boolean set_systemui_disabled(boolean disabled) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("pm " + (disabled ? "disable" : "enable") + " com.android.systemui\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean set_usb_device_disabled(boolean enable_adb) {
        try {
            Process process = Runtime.getRuntime().exec("setprop persist.sys.usb.config " + (enable_adb ? "adb" : "none") + "\n");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.flush();
            process.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
