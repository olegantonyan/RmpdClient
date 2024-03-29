package ru.slon_ds.rmpdclient.remotecontrol.playlist;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Loader;
import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;
import ru.slon_ds.rmpdclient.common.Files;
import ru.slon_ds.rmpdclient.common.Logger;

public class DownloadWorker extends Thread {
    private ArrayList<URL> urls = new ArrayList<>();
    private OnDownloadFinishedCallback callback = null;

    public interface OnDownloadFinishedCallback {
        void onfinished(boolean ok, String message);
    }

    public DownloadWorker(ArrayList<URL> urls, OnDownloadFinishedCallback callback) {
        super();
        this.urls = urls;
        this.callback = callback;
    }

    @Override
    public void run() {
        setName("download_worker");
        boolean ok = false;
        String message = "";
        try {
            HttpClient http = HttpClient.new_default();
            for (URL url : urls) {
                if (isInterrupted()) {
                    break;
                }
                String localpath = Files.full_file_localpath(url.toString());
                if (new File(localpath).exists()) {
                    continue;
                }
                Logger.info(this, "downloading file " + url.toString());
                http.download_file(url, localpath, 3);
            }
            if (!isInterrupted()) {
                utilize_not_playlist_files();
                message = "playlist updated successfully";
            } else {
                message = "playlist update interrupted";
                Logger.warning(this, message);
            }
            ok = true;
        } catch (Exception e) {
            message = "error downloading files";
            ok = false;
            Logger.exception(this, message, e);
        } finally {
            if (callback != null && !isInterrupted()) {
                callback.onfinished(ok, message);
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
}
