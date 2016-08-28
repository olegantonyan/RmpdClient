package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerGuard;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class PlayerProxy {
    private PlayerGuard player = null;
    private PlaybackEvents events = null;
    private NowPlaying now_playing = null;

    public PlayerProxy(PlayerGuard w, PlaybackEvents e, NowPlaying np) {
        player = w;
        events = e;
        now_playing = np;
    }

    public boolean is_playing() {
        try {
            return player.execute("is_playing").fetch("result", Boolean.class, false);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public Integer time_pos() {
        try {
            return player.execute("time_pos").fetch("result", Integer.class, 0);
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public Integer percent_pos() {
        try {
            return player.execute("percent_pos").fetch("result", Integer.class, 0);
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public boolean play(Item item) {
        if (item == null) {
            Logger.error(this, "cannot play null item");
            return false;
        }
        try {
            KWargs options = new KWargs();
            options.put("item", item);
            player.execute("play", options);
            if (events != null) {
                events.onplay(item);
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean stop() {
        try {
            player.execute("stop");
            if (events != null) {
                events.onstop(now_playing.get());
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean resume(Item item, Integer position_ms) {
        if (item == null || position_ms == null) {
            Logger.error(this, "cannot resume null item or position");
            return false;
        }
        try {
            KWargs options = new KWargs();
            options.put("item", item);
            player.execute("play", options);
            options.put("position_ms", position_ms);
            player.execute("seek_to", options);
            if (events != null) {
                events.onresume(item, position_ms);
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public boolean suspend() {
        try {
            Item item = now_playing.get();
            Integer position_ms = time_pos();
            player.execute("stop");
            if (events != null) {
                events.onsuspend(item, position_ms);
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
