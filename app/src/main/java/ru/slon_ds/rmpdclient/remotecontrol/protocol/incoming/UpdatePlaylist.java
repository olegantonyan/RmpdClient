package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Loader;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.playlist.DownloadWorker;
import ru.slon_ds.rmpdclient.remotecontrol.playlist.DownloadWorkerManager;
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
        DownloadWorkerManager.instance().start(urls, this);
        return true;
    }

    @Override
    public void onfinished(boolean ok, String message) {
        Logger.info(this, message);
        if (ok) {
            save_playlist_file();
            if (PlayerController.instance() != null) {
                PlayerController.instance().start_playlist();
            }
        }
        ack(ok, message);
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