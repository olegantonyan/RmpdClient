package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.mediaplayer.player.Watcher;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Scheduler extends Thread {
    private Watcher player = null;

    public Scheduler(Watcher w) {
        super();
        player = w;
        start();
    }

    public void set_playlist(Playlist p) {

    }

    @Override
    public void run() {
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.exception(this, "scheduler loop interrupted", e);
                break;
            }
        }
    }
}
