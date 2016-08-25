package ru.slon_ds.rmpdclient.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import ru.slon_ds.rmpdclient.AndroidApplication;

public class Files {
    public static String mediafiles_path() {
        String result = Config.instance().storage_path() + "/mediafiles";
        create_path_if_not_exists(result);
        return result;
    }

    public static String temp_path() {
        String result = Config.instance().storage_path() + "/temp"; //AndroidApplication.context().getCacheDir().getAbsolutePath();
        create_path_if_not_exists(result);
        return result;
    }

    public static String logs_path() {
        String result = Config.instance().storage_path() + "/logs";
        create_path_if_not_exists(result);
        return result;
    }

    public static String base_storage_path() {
        String ext = external_sdcard_path();
        if (ext == null) {
            ext = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        String result = ext + "/" + AndroidApplication.context().getPackageName();
        create_path_if_not_exists(result);
        return result;
    }

    public static String wallpaper_filepath() {
        return Config.instance().storage_path() + "/wallpaper";
    }



    public static URL full_url_by_relative(String relative) throws MalformedURLException {
        if (relative.startsWith("http")) {
            return new URL(relative);
        }
        String server_base_url = Config.instance().server_url();
        return new URL(server_base_url + "" + relative);
    }

    public static String file_basename(String full_filepath) {
        return full_filepath.substring(full_filepath.lastIndexOf('/') + 1);
    }

    public static String full_file_localpath(String relative_url) {
        return mediafiles_path() + "/" + file_basename(relative_url);
    }

    public static void copy_file(String src, String dst) throws IOException {
        File dstf = new File(dst);
        if (dstf.exists()) {
            if (!dstf.delete()) {
                throw new IOException("cannot delete file " + dst);
            }
        }
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } finally {
            in.close();
            out.close();
        }
    }

    public static ArrayList<String> external_mounts() {
        final ArrayList<String> result = new ArrayList();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (Exception e) {
            Logger.exception(new Files(), "error getting info about mounted fs", e);
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/")) {
                            if (!part.toLowerCase(Locale.US).contains("vold")) {
                                result.add(part);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String external_sdcard_path() {
        final ArrayList<String> mounts = external_mounts();
        if (mounts.size() == 1) {
            return mounts.get(0);
        } else if (!mounts.isEmpty()) {
            for (String s : mounts) {
                if (s.toLowerCase(Locale.US).contains("ext")) {
                    return s;
                }
            }
            return mounts.get(0);
        }
        return null;
    }

    private static boolean create_path_if_not_exists(String path) {
        File f = new File(path);
        return f.exists() || f.mkdirs();
    }
}
