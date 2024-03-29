package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerGuard;
import ru.slon_ds.rmpdclient.mediaplayer.player.Watchdog;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.common.Logger;

public class Scheduler implements Runnable, PlayerGuard.Callback {
    private PlayerProxy player = null;
    private BlockingQueue<KWargs> queue = null;
    private Playlist playlist = null;
    private NowPlaying now_playing = null;
    private PreemptedTrack preempted_track = null;
    private PlaybackEvents events = null;
    private Thread thread = null;
    private Watchdog wdt = null;

    public Scheduler(PlayerGuard player_guard) {
        queue = new LinkedBlockingQueue<>();
        player_guard.set_callback(this);
        events = new PlaybackEvents(ProtocolDispatcher.instance());
        now_playing = new NowPlaying();
        player = new PlayerProxy(player_guard, events, now_playing);
        wdt = new Watchdog();
        thread = new Thread(this);
        thread.start();
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
    public void onerror(String message) {
        KWargs options = new KWargs();
        options.put("message", message);
        schedule("track_error", options);
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

    public void quit() {
        if (thread != null) {
            Logger.debug(this, "scheduler was told to quit...");
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void schedule(String command) {
        schedule(command, null);
    }

    private void schedule(String command, KWargs options) {
        KWargs msg = new KWargs();
        msg.put("command", command);
        msg.put("options", options);
        try {
            queue.put(msg);
        } catch (InterruptedException e) {
            Logger.exception(this, "putting data into queue interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private void scheduler() {
        Item current_track = now_playing.get();

        if (current_track != null && current_track.is_background()) {
            Date thetime = new Date();
            if (!current_track.is_appropriate_at(thetime) && !current_track.is_wait_for_the_end()) {
                Logger.info(this, "track stopped because it is out of time range");
                play(null);
            }
        }

        Item next_advertising = playlist.next_advertizing();
        if (next_advertising != null) {
            if (current_track == null || current_track.is_background()) {
                play(next_advertising);
            }
        } else {
            PreemptedTrack preempt = preempted();
            if (preempt != null) {
                if (current_track == null) {
                    resume(preempt.item, preempt.position_ms);
                    reset_preempted();
                }
            } else {
                Item next_background = playlist.next_background();
                if (next_background != null) {
                    if (current_track == null) {
                        play(next_background);
                    }
                }
            }
        }
    }

    private void start_playlist() {
        if (player.is_playing() || playlist == null) {
            play(null);
        }
        reset_preempted();
        wdt.reset();
        if (playlist != null) {
            Item start_item = playlist.first_background();
            if (start_item == null) {
                Logger.info(this, "no appropriate track to start playlist from");
            } else {
                play(start_item);
            }
        }
    }

    private void track_finished() {
        Item track = now_playing.get();
        if (track != null) {
            Logger.info(this, "track finished '" + track.filename() + "'");
            notify_playlist_on_track_finished(track);
            events.onstop(track);
        } else {
            Logger.warning(this, "track finished null");
        }
        now_playing.set(null);
    }

    private void track_error(String message) {
        Item track = now_playing.get();
        events.onerror(track, message);
        wdt.error_with(track);
        track_finished();
    }

    private void notify_playlist_on_track_finished(Item item) {
        if (playlist != null) {
            playlist.onfinished(item);
        }
    }

    private void stop() {
        player.stop();
        notify_playlist_on_track_finished(now_playing.get());
        now_playing.set(null);
    }

    private void play(Item item) {
        if (item == null) {
            stop();
            return;
        }
        if (!wdt.is_ok_to_start(item)) {
            Logger.debug(this, "playback prevented by watchdog: " + item.toString());
            return;
        }
        if (player.is_playing()) {
            if (item.is_advertising()) {
                suspend();
            } else {
                stop();
            }
        }
        now_playing.set(item);
        player.play(item);
    }

    private void resume(Item item, Integer position_ms) {
        if (!wdt.is_ok_to_resume(item)) {
            Logger.debug(this, "resume prevented by watchdog: " + item.toString());
            return;
        }
        player.resume(item, position_ms);
        Logger.info(this, "track resumed '" + item.filename() + "' at " + position_ms.toString() + " ms");
        now_playing.set(item);
    }

    private void suspend() {
        preempt(now_playing.get(), player.time_pos());
        player.suspend();
    }

    @Override
    public void run() {
        thread.setName("scheduler_loop");
        Logger.debug(this, "entering scheduler loop");
        while (!thread.isInterrupted()) {
            try {
                final KWargs msg = queue.poll(1, TimeUnit.SECONDS);
                if (msg != null) {
                    final String command = msg.fetch("command", String.class, null);
                    final KWargs options = msg.fetch("options", KWargs.class, null);
                    if (command.equals("track_finished")) {
                        track_finished();
                    } else if (command.equals("track_error")) {
                        if (options != null) {
                            track_error(options.fetch("message", String.class, ""));
                        } else {
                            track_error("");
                        }
                    } else if (command.equals("start_playlist")) {
                        start_playlist();
                        continue; // give it some time to start, do not run scheduler yet
                    }
                }
            } catch (InterruptedException e) {
                Logger.warning(this, "scheduler loop interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Logger.exception(this, "error processing scheduler command", e);
            }
            try {
                if (playlist != null) {
                    scheduler();
                }
            } catch (Exception e) {
                Logger.exception(this, "error running scheduler", e);
            }
        }
        Logger.debug(this, "stopping scheduler loop");
    }

    private synchronized void preempt(Item item, Integer position_ms) {
        Logger.info(this, "track suspended '" + item.filename() + "' at " + position_ms.toString() + " ms");
        preempted_track = new PreemptedTrack(item, position_ms);
    }

    private synchronized PreemptedTrack preempted() {
        return preempted_track;
    }

    private synchronized void reset_preempted() {
        Logger.debug(this, "reset preempted state");
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