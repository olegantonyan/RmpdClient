package ru.slon_ds.rmpdclient.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchive {
    public void compress_directory(String source_directory, String destination_file) throws IOException {
        FileOutputStream fw = null;
        ZipOutputStream zip = null;
        try {
            fw = new FileOutputStream(destination_file);
            zip = new ZipOutputStream(fw);
            add_directory_to_zip("", source_directory, zip);
        } finally {
            if (zip != null) {
                zip.close();
            }
            if (fw != null) {
                fw.close();
            }
        }
    }

    private void add_directory_to_zip(String path, String source_directory, ZipOutputStream zip) throws IOException {
        File directory = new File(source_directory);
        if (directory.list().length == 0) {
            add_file_to_zip(path , source_directory, zip, true);
        } else {
            for (String filename : directory.list()) {
                if (path.equals("")) {
                    add_file_to_zip(directory.getName(), source_directory + "/" + filename, zip, false);
                } else {
                    add_file_to_zip(path + "/" + directory.getName(), source_directory + "/" + filename, zip, false);
                }
            }
        }
    }

    private void add_file_to_zip(String path, String source_file, ZipOutputStream zip, boolean flag) throws IOException {
        File file = new File(source_file);
        if (flag) {
            zip.putNextEntry(new ZipEntry(path + "/" +file.getName() + "/"));
        } else {
            if (file.isDirectory()) {
                add_directory_to_zip(path, source_file, zip);
            }
            else {
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(source_file);
                zip.putNextEntry(new ZipEntry(path + "/" + file.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        }
    }
}