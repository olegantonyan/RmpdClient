package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;
import ru.slon_ds.rmpdclient.remotecontrol.IncomingMessage;
import ru.slon_ds.rmpdclient.utils.Config;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdatePlaylist extends BaseCommand implements DownloadWorker.OnDownloadFinishedCallback {
    public UpdatePlaylist(ControlWrapper control_wrapper, IncomingMessage data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        DownloadWorkerManager.instance().start(items_urls(), get_sequence(), this);
        return true;
    }

    @Override
    public void onfinished(boolean ok, Integer seq, String message) {
        Logger.info(this, "playlist update finished " + (ok ? "successfully" : "failure") + ":" + message + " (" + seq.toString() + ")");
        ack(ok, seq, message);
    }

    private boolean ack(boolean ok, Integer seq, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", seq);
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }

    private ArrayList<URL> items_urls() {
        ArrayList<URL> urls = new ArrayList();
        try {
            JSONArray items = get_data().getJSONObject("playlist").getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject o = items.getJSONObject(i);
                String relative_url = o.getString("url");
                urls.add(Files.full_url_by_relative(relative_url));
            }
        } catch (Exception e) {
            Logger.exception(this, "error extracting urls", e);
        }
        return urls;
    }
}

class DownloadWorkerManager {
    private static DownloadWorkerManager _instance = new DownloadWorkerManager();
    private DownloadWorker worker = null;

    public static DownloadWorkerManager instance() {
        return _instance;
    }

    public synchronized void start(ArrayList<URL> urls, Integer seq, DownloadWorker.OnDownloadFinishedCallback callback) {
        if (worker != null && worker.isAlive()) {
            Logger.warning(this, "trying to start update worker while another one is active, terminating");
            worker.soft_stop();
            try {
                worker.join();
            } catch (InterruptedException e) {
                Logger.exception(this, "error waiting for thread finished", e);
            }
        }
        worker = new DownloadWorker(urls, seq, callback);
        worker.start();
    }

    private DownloadWorkerManager() {}
}

class DownloadWorker extends Thread {
    private ArrayList<URL> urls = new ArrayList<>();
    private Integer seq = 0;
    private OnDownloadFinishedCallback callback = null;
    private boolean stop_flag = false;

    interface OnDownloadFinishedCallback {
        void onfinished(boolean ok, Integer seq, String message);
    }

    public DownloadWorker(ArrayList<URL> urls, Integer seq, OnDownloadFinishedCallback callback) {
        super();
        this.urls = urls;
        this.seq = seq;
        this.callback = callback;
    }

    @Override
    public void run() {
        boolean ok = false;
        String message = "";
        try {
            HttpClient http = http_client();
            for (URL url : urls) {
                if (stop_flag) {
                    message = "terminated";
                    Logger.warning(this, message);
                    break;
                }
                Logger.info(this, "downloading file " + url.toString());
                http.download_file(url, Files.full_file_localpath(url.toString()));
            }
            ok = true;
            message = "playlist updated successfully";
        } catch (Exception e) {
            message = "error downloading files";
            ok = false;
            Logger.exception(this, message, e);
        } finally {
            if (callback != null && !stop_flag) {
                callback.onfinished(ok, seq, message);
            }
        }
    }

    public void soft_stop() {
        this.stop_flag = true;
    }

    private HttpClient http_client() throws MalformedURLException {
        return new HttpClient(Config.instance().server_url(), Config.instance().login(), Config.instance().password());
    }
}