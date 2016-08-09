package ru.slon_ds.rmpdclient.mediaplayer.player;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Watcher extends Thread implements PlayerWrapper.Callback {
    private PlayerWrapper player_wrapper = null;
    private PriorityBlockingQueue<QueuedMessage> tx = null;
    private LinkedBlockingQueue<KWargs> rx = null;
    private Item now_playing = null;

    public Watcher(PlayerWrapper pw) {
        player_wrapper = pw;
        player_wrapper.set_callback(this);
        tx = new PriorityBlockingQueue<>();
        rx = new LinkedBlockingQueue<>();
        start();
    }

    public void play(Item item) {
        KWargs options = new KWargs();
        options.put("item", item);
        execute("play", options);
        set_now_playing(item);
    }

    public void stop_playback() {
        execute("stop");
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
                    track_finished(item);
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

    private void track_finished(Item item) {
        set_now_playing(null);
        Logger.info(this, "track finished '" + item.filename() + "'");
    }

    private void track_error(Item item) {
        set_now_playing(null);
        Logger.error(this, "playback error " + item.filename());
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
