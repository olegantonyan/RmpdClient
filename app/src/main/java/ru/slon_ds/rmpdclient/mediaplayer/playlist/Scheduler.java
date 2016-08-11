package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.slon_ds.rmpdclient.mediaplayer.player.Watcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Scheduler extends Thread implements Watcher.Callback {
    private Watcher player = null;
    private BlockingQueue<KWargs> queue = null;
    private Playlist playlist = null;

    public Scheduler(Watcher w) {
        super();
        queue = new LinkedBlockingQueue<>();
        player = w;
        player.set_callback(this);
        start();
    }

    public void set_playlist(Playlist p) {
        this.playlist = p;
        Logger.info(this, "start playlist");
        schedule("start_playlist");
    }

    @Override
    public void onfinished(Item item) {
        if (item != null) {
            Logger.info(this, "track finished " + item.filename());
        }
    }

    private void schedule(String command) {
        KWargs msg = new KWargs();
        msg.put("command", command);
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            Logger.exception(this, "error putting data into queue", e);
        }
    }

    private void scheduler() {

    }

    private void start_playlist() {
        if (player.is_playing()) {
            play(null);
        }
        // set now playing
        // reset preempted
        Item start_item = playlist.first_background();
        if (start_item == null) {
            Logger.info(this, "no appropriate track to start playlist from");
        } else {
            play(start_item);
        }
    }


    private void track_finished() {

    }

    private void play(Item item) {
        if (item == null) {
            // set now playing
            player.stop_playback();
            return;
        }
        // check type and...
        player.play(item);
    }

    @Override
    public void run() {
        while (true) {
            try {
                KWargs msg = queue.poll(1, TimeUnit.SECONDS);
                if (msg != null && msg.fetch("command", String.class, null).equals("track_finished")) {
                    track_finished();
                } else if (msg != null && msg.fetch("command", String.class, null).equals("start_playlist")) {
                    start_playlist();
                }
            } catch (InterruptedException e) {
                Logger.exception(this, "scheduler loop interrupted", e);
                break;
            } catch (Exception e) {
                Logger.exception(this, "error processing scheduler command", e);
            } finally {
                try {
                    scheduler();
                } catch (Exception e) {
                    Logger.exception(this, "error running scheduler", e);
                }
            }
        }
    }
}
