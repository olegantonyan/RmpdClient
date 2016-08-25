package ru.slon_ds.rmpdclient.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;

public class ServiceUpload implements Runnable {
    private String reason = null;

    public ServiceUpload(String reason) {
        this.reason = reason;
    }

    @Override
    public void run() {
        try {
            run_sync();
        } catch (Exception e) {
            Logger.exception(this, "error running service upload", e);
        }
    }

    public void run_sync() throws Exception {
        Logger.info(this, "starting service upload");
        final String archive_path = temp_archive_filepath();
        try {
            ZipArchive archive = new ZipArchive();
            archive.compress_directory(Files.logs_path(), archive_path);
            HttpClient http = http_client();
            KWargs params = new KWargs();
            params.put("file", new File(archive_path));
            params.put("reason", reason);
            http.submit_multipart_form(http.service_upload_url(), params);
            Logger.info(this, "service upload complete");
        } finally {
            new File(archive_path).delete();
        }
    }

    private HttpClient http_client() throws MalformedURLException {
        return new HttpClient(Config.instance().server_url(), Config.instance().login(), Config.instance().password());
    }

    private String temp_archive_filepath() {
        return Files.temp_path() + "/service_upload_android " + thetime() + ".zip";
    }

    private String thetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        return sdf.format(new Date());
    }
}
