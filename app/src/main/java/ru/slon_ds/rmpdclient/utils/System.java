package ru.slon_ds.rmpdclient.utils;

import android.os.StatFs;

public class System {
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
}
