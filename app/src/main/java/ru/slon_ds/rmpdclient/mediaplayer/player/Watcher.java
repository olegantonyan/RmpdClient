package ru.slon_ds.rmpdclient.mediaplayer.player;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Watcher extends Thread implements PlayerWrapper.Callback {
    private PlayerWrapper player_wrapper = null;
    private PriorityBlockingQueue<QueuedMessage> tx = null;
    private LinkedBlockingQueue<KWargs> rx = null;
    private Item now_playing = null;
    private Callback callback = null;

    public interface Callback {
        void onfinished(Item item);
    }

    public void set_callback(Callback cb) {
        this.callback = cb;
    }

    public Watcher(PlayerWrapper pw) {
        player_wrapper = pw;
        player_wrapper.set_callback(this);
        tx = new PriorityBlockingQueue<>();
        rx = new LinkedBlockingQueue<>();
        start();
    }

    public void play(Item item) {
        if (item == null) {
            Logger.warning(this, "item is null, cannot play");
            return;
        }
        if (is_playing()) {
            stop_playback();
        }
        KWargs options = new KWargs();
        options.put("item", item);
        execute("play", options);
        set_now_playing(item);
        onplay(item);
    }

    public void stop_playback() {
        Item current = get_now_playing();
        execute("stop");
        track_finish(current);
    }

    public boolean is_playing() {
        return execute("is_playing").fetch("result", Boolean.class, false);
    }

    public Integer percent_pos() {
        return execute("percent_pos").fetch("result", Integer.class, 0);
    }

    public String filename() {
        Item current = get_now_playing();
        if (is_playing() && current != null) {
            return current.filename();
        }
        return null;
    }

    @Override
    public void onfinished() {
        tx.offer(new QueuedMessage("onfinished", -1));
    }

    @Override
    public void onerror() {
        tx.offer(new QueuedMessage("onerror", -2));
    }

    @Override
    public void run() {
        while (true) {
            try {
                QueuedMessage msg = tx.take();
                if (msg.command.equals("onerror")) {
                    Item item = get_now_playing();
                    track_error(item);
                } else if (msg.command.equals("onfinished")) {
                    Item item = get_now_playing();
                    track_finish(item);
                    if (callback != null) {
                        callback.onfinished(item);
                    }
                } else if (msg.command.equals("quit")) {
                    break;
                } else {
                    KWargs result = new KWargs();
                    try {
                        result = player_wrapper.execute(msg.command, msg.options);
                    } catch (Exception e) {
                        Logger.exception(this, "error executing player command '" + msg.command + "'", e);
                    } finally {
                        rx.put(result);
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void quit() {
        tx.offer(new QueuedMessage("quit"));
    }

    private void track_finish(Item item) {
        Logger.debug(this, "track finished '" + item.filename() + "'");
        onstop(item);
        set_now_playing(null);
    }

    private void track_error(Item item) {
        Logger.error(this, "playback error " + item.filename());
        onerror(item, "error");
        track_finish(item);
    }

    private synchronized Item get_now_playing() {
        return now_playing;
    }

    private synchronized void set_now_playing(Item now_playing) {
        this.now_playing = now_playing;
    }

    private synchronized KWargs execute(String command, KWargs options) {
        try {
            tx.put(new QueuedMessage(command, 10, options));
            return rx.take();
        } catch (Exception e) {
            Logger.exception(this, "error executing watcher command '" + command + "'", e);
            return null;
        }
    }

    private KWargs execute(String command) {
        return execute(command, new KWargs());
    }

    private void onplay(Item item) {
        KWargs options = new KWargs();
        options.put("item", item);
        ProtocolDispatcher.instance().send("track_begin", options);
    }

    private void onstop(Item item) {
        KWargs options = new KWargs();
        options.put("item", item);
        ProtocolDispatcher.instance().send("track_end", options);
    }

    private void onerror(Item item, String message) {
        KWargs options = new KWargs();
        options.put("item", item);
        options.put("message", message);
        ProtocolDispatcher.instance().send("playback_error", options);
    }

    class QueuedMessage implements Comparable<QueuedMessage> {
        public Integer priority = 0;
        public String command = "";
        public KWargs options = new KWargs();

        public QueuedMessage(String type, Integer priority, KWargs options) {
            this.command = type;
            this.priority = priority;
            this.options = options;
        }

        public QueuedMessage(String type, Integer priority) {
            this(type, priority, new KWargs());
        }

        public QueuedMessage(String type) {
            this(type, 0, new KWargs());
        }

        @Override
        public int compareTo(QueuedMessage other) {
            return priority.compareTo(other.priority);
        }
    }
}
