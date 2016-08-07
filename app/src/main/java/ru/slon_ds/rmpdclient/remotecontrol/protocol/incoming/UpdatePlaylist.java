package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.net.URL;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.IncomingMessage;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdatePlaylist extends BaseCommand implements DownloadWorker.OnDownloadFinishedCallback {
    public UpdatePlaylist(ControlWrapper control_wrapper, IncomingMessage data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        return false;
    }

    @Override
    public void onfinished(boolean ok, Integer seq, String message) {

        ack(ok, seq, message);
    }

    private boolean ack(boolean ok, Integer seq, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", seq);
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }
}

class DownloadWorkerManager {
    private static DownloadWorkerManager _instance = new DownloadWorkerManager();
    private DownloadWorker worker = null;

    public static DownloadWorkerManager instance() {
        return _instance;
    }

    public void start(ArrayList<URL> urls, Integer seq, DownloadWorker.OnDownloadFinishedCallback callback) throws InterruptedException {
        if (worker != null && worker.isAlive()) {
            Logger.warning(this, "trying to start update worker while another one is active, terminating");
            worker.soft_stop();
            worker.join();
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

        for (URL url : urls) {
            if (stop_flag) {
                message = "terminated";
                Logger.warning(this, message);
                break;
            }
        }

        if (callback != null && !stop_flag) {
            callback.onfinished(ok, seq, message);
        }
    }

    public void soft_stop() {
        this.stop_flag = true;
    }
}