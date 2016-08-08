package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Loader;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;
import ru.slon_ds.rmpdclient.utils.Config;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdatePlaylist extends BaseCommand implements DownloadWorker.OnDownloadFinishedCallback {
    public UpdatePlaylist(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        ArrayList<URL> urls = items_urls();
        send_begin_command(urls);
        DownloadWorkerManager.instance().start(urls, get_sequence(), this);
        return true;
    }

    @Override
    public void onfinished(boolean ok, Integer seq, String message) {
        Logger.info(this, "playlist update finished " + (ok ? "successfully" : "failure") + ":" + message + " (" + seq.toString() + ")");
        save_playlist_file();
        ack(ok, seq, message);
    }

    private boolean ack(boolean ok, Integer seq, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", seq);
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }

    private void send_begin_command(ArrayList<URL> urls) {
        KWargs opts = new KWargs();
        ArrayList<String> filenames = new ArrayList<>();
        for (URL u : urls) {
            filenames.add(Files.file_basename(u.toString()));
        }
        opts.put("files", filenames);
        sender("update_playlist").call(opts);
    }

    private ArrayList<URL> items_urls() {
        ArrayList<JsonDict> items = get_data().fetch_object("playlist").fetch_array_of_objects("items");
        ArrayList<URL> urls = new ArrayList<>();
        try {
            for (JsonDict m : items) {
                urls.add(Files.full_url_by_relative(m.fetch("url", String.class)));
            }
        } catch (MalformedURLException e) {
            Logger.exception(this, "error extracting items urls", e);
        }
        return urls;
    }

    private void save_playlist_file() {
        try {
            JsonDict playlist = get_data().fetch_object("playlist");
            new Loader().save_to_file(playlist);
        } catch (IOException e) {
            Logger.exception(this, "error saving playlist file", e);
        }
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
                String localpath = Files.full_file_localpath(url.toString());
                if (new File(localpath).exists()) {
                    continue;
                }
                Logger.info(this, "downloading file " + url.toString());
                http.download_file(url, localpath);
            }
            utilize_not_playlist_files();
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

    private void utilize_not_playlist_files() {
        ArrayList<String> filenames = new ArrayList<>();
        for (URL u : urls) {
            filenames.add(Files.file_basename(u.toString()));
        }
        File dir = new File(Files.mediafiles_path());
        File files_in_dir[] = dir.listFiles();
        for (File current : files_in_dir) {
            if (!filenames.contains(current.getName()) && !(new Loader().filename().equals(current.getName()))) {
                Logger.info(this, "removing file not in current playlist '" + current.getName() + "'");
                current.delete();
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