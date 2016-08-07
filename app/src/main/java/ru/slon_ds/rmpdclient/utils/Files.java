package ru.slon_ds.rmpdclient.utils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import ru.slon_ds.rmpdclient.AndroidApplication;

public class Files {
    public static String mediafiles_path() {
        String ext = external_sdcard_path();
        if (ext == null) {
            return null;
        }
        return ext + "/" + AndroidApplication.getAppContext().getPackageName() + "/mediafiles";
    }

    public static URL full_url_by_relative(String relative) throws MalformedURLException {
        if (relative.startsWith("http")) {
            return new URL(relative);
        }
        String server_base_url = "https://server.slon-ds.ru";
        return new URL(server_base_url + "/" + relative);
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
}
