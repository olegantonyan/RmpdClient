package ru.slon_ds.rmpdclient.common;

import android.os.StatFs;

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
            Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "pm", (disabled ? "disable" : "enable"), "com.android.systemui"});
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
