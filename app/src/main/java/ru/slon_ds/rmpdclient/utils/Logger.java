package ru.slon_ds.rmpdclient.utils;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import ru.slon_ds.rmpdclient.AndroidApplication;

public class Logger {
    public static void info(Object source, String message) {
        String tag = tag(source);
        FileLogger.instance().write("INFO", tag, message);
        Log.i(tag, message);
    }

    public static void warning(Object source, String message) {
        String tag = tag(source);
        FileLogger.instance().write("WARNING", tag, message);
        Log.w(tag, message);
    }

    public static void error(Object source, String message) {
        String tag = tag(source);
        FileLogger.instance().write("ERROR", tag, message);
        Log.e(tag, message);
    }

    public static void debug(Object source, String message) {
        String tag = tag(source);
        FileLogger.instance().write("DEBUG", tag, message);
        Log.d(tag, message);
    }

    public static void exception(Object source, String message, Throwable exception) {
        String tag = tag(source);
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        FileLogger.instance().write("EXCEPTION", tag, message + "\n" + sw.toString());
        Log.e(tag, message, exception);
    }

    private static String tag(Object source) {
        String result;
        if (source instanceof String) {
            result = (String) source;
        } else {
            result = source.getClass().getName().replace(AndroidApplication.context().getPackageName(), "");
        }
        return result + thread_info();
    }

    private static String thread_info() {
        return String.format(Locale.US, "[%s]", Thread.currentThread().getName());
    }
}

class FileLogger {
    private static FileLogger _instance = null;
    private File file = null;
    private String path = null;
    private String filename = null;
    private boolean verbose = false;

    public static FileLogger instance() {
        if (_instance == null) {
            _instance = new FileLogger();
        }
        return _instance;
    }

    private FileLogger() {
        path = Files.logs_path();
        filename = "rmpd.log";
        file = new File(full_filepath());
        verbose = Config.instance().verbose_logging();
    }

    public synchronized boolean write(String level, String tag, String message) {
        level = level.toUpperCase();
        if (level.equals("DEBUG") && !verbose) {
            return true;
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(formatted_message(level, tag, message));
            out.close();
            rotate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String formatted_message(String level, String tag, String message) {
        return String.format(Locale.US, "[%s] %s |%s| %s", thetime(), tag, level, message);
    }

    private String thetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(new Date());
    }

    private String full_filepath() {
        return path + "/" + filename;
    }

    private void rotate() {
        final long threshold = 2 * 1024 * 1024;
        if (file.length() >= threshold) {
            try {
                Files.copy_file(file.getAbsolutePath(), file.getAbsolutePath() + "." + thetime());
            } catch (IOException e) {
            } finally {
                file.delete();
            }

            File dir = new File(path);
            File files[] = dir.listFiles();
            if (files.length >= 10) {
                Arrays.sort(files, new Comparator<File>(){
                    public int compare(File f1, File f2) {
                        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                    }
                });
                for (int i = 0; i < 2; i++) {
                    files[i].delete();
                }
            }
        }
    }
}
