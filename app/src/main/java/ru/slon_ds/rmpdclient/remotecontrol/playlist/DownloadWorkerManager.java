package ru.slon_ds.rmpdclient.remotecontrol.playlist;

import java.net.URL;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.common.Logger;

public class DownloadWorkerManager {
    private static DownloadWorkerManager _instance = new DownloadWorkerManager();
    private DownloadWorker worker = null;

    public static DownloadWorkerManager instance() {
        return _instance;
    }

    public synchronized void start(ArrayList<URL> urls, DownloadWorker.OnDownloadFinishedCallback callback) {
        if (worker != null && worker.isAlive()) {
            Logger.warning(this, "trying to start update worker while another one is active, terminating");
            stop();
            try {
                worker.join();
            } catch (InterruptedException e) {
                Logger.exception(this, "waiting for thread finished interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        worker = new DownloadWorker(urls, callback);
        worker.start();
    }

    public synchronized void stop() {
        if (worker != null && worker.isAlive()) {
            worker.interrupt();
            try {
                worker.join();
            } catch (InterruptedException e) {
                Logger.exception(this, "waiting for download worker to finish interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private DownloadWorkerManager() {
    }
}

