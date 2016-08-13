package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.slon_ds.rmpdclient.mediaplayer.player.Watcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Scheduler implements Runnable, Watcher.Callback {
    private Watcher player = null;
    private BlockingQueue<KWargs> queue = null;
    private Playlist playlist = null;
    private Item now_playing = null;
    private PreemptedTrack preempted_track = null;

    public Scheduler(Watcher w) {
        queue = new LinkedBlockingQueue<>();
        player = w;
        player.set_callback(this);
        new Thread(this).start();
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
        schedule("track_finished");
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
        set_now_playing(null);
        // reset preempted
        Item start_item = playlist.first_background();
        if (start_item == null) {
            Logger.info(this, "no appropriate track to start playlist from");
        } else {
            play(start_item);
        }
    }

    private void track_finished() {
        Item track = get_now_playing();
        notify_playlist_on_track_finished(track);
        set_now_playing(null);
    }

    private void notify_playlist_on_track_finished(Item item) {
        if (playlist != null) {
            playlist.onfinished(item);
        }
    }

    private void play(Item item) {
        if (item == null) {
            set_now_playing(null);
            player.stop();
            return;
        }
        if (player.is_playing()) {
            if (item.is_advertising()) {
                preempt(get_now_playing(), player.time_pos());
                player.suspend();
            } else {
                player.stop();
            }
        }
        player.play(item);
        set_now_playing(item);
    }

    private void resume(Item item, Integer position_ms) {
        player.resume(item, position_ms);
        set_now_playing(item);
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

    private synchronized void set_now_playing(Item item) {
        if (item == null) {
            Logger.debug(this, "set now playing null");
        } else {
            Logger.debug(this, "set now playing " + item.toString());
        }
        now_playing = item;
    }

    private synchronized Item get_now_playing() {
        return now_playing;
    }

    private synchronized void preempt(Item item, Integer position_ms) {
        preempted_track = new PreemptedTrack(item, position_ms);
    }

    private synchronized PreemptedTrack preempted() {
        return preempted_track;
    }

    private synchronized void reset_preempted() {
        preempted_track = null;
    }

    class PreemptedTrack {
        Item item = null;
        Integer position_ms = null;

        public PreemptedTrack(Item item, Integer position_ms) {
            this.item = item;
            this.position_ms = position_ms;
        }
    }
}