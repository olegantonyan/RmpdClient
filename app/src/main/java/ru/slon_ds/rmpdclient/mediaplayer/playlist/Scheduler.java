package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Scheduler implements Runnable, PlayerWrapper.Callback {
    private PlayerProxy player = null;
    private BlockingQueue<KWargs> queue = null;
    private Playlist playlist = null;
    private NowPlaying now_playing = null;
    private PreemptedTrack preempted_track = null;
    private PlaybackEvents events = null;

    public Scheduler(PlayerWrapper player_wrapper) {
        queue = new LinkedBlockingQueue<>();
        player_wrapper.set_callback(this);
        events = new PlaybackEvents(ProtocolDispatcher.instance());
        now_playing = new NowPlaying();
        player = new PlayerProxy(player_wrapper, events, now_playing);
        new Thread(this).start();
    }

    public void set_playlist(Playlist p) {
        this.playlist = p;
        Logger.info(this, "start playlist");
        schedule("start_playlist");
    }

    @Override
    public void onfinished() {
        schedule("track_finished");
    }

    @Override
    public void onerror() {
        events.onerror(now_playing.get(), "todo: get error message");
        Logger.error(this, "track error");
        onfinished();
    }

    public String current_track_filename() {
        Item i = now_playing.get();
        if (i == null) {
            return null;
        }
        return i.filename();
    }

    public Integer current_track_percent_pos() {
        return player.percent_pos();
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
        now_playing.set(null);
        reset_preempted();
        Item start_item = playlist.first_background();
        if (start_item == null) {
            Logger.info(this, "no appropriate track to start playlist from");
        } else {
            play(start_item);
        }
    }

    private void track_finished() {
        Item track = now_playing.get();
        if (track != null) {
            Logger.info(this, "track finished '" + track.filename() + "'");
            notify_playlist_on_track_finished(track);
        } else {
            Logger.warning(this, "track finished null");
        }
        now_playing.set(null);
        events.onstop(track);
    }

    private void notify_playlist_on_track_finished(Item item) {
        if (playlist != null) {
            playlist.onfinished(item);
        }
    }

    private void play(Item item) {
        if (item == null) {
            now_playing.set(null);
            stop();
            return;
        }
        if (player.is_playing()) {
            if (item.is_advertising()) {
                preempt(now_playing.get(), player.time_pos());
            }
            stop();
        }
        player.play(item);
        now_playing.set(item);
    }

    private void stop() {
        player.stop();
        schedule("track_finished");
    }

    private void resume(Item item, Integer position_ms) {
        player.resume(item, position_ms);
        now_playing.set(item);
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