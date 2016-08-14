package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class PlaybackEvents {
    private ProtocolDispatcher proto = null;

    public PlaybackEvents(ProtocolDispatcher protocol_dispatcher) {
        proto = protocol_dispatcher;
    }

    public void onplay(Item item) {
        KWargs options = new KWargs();
        options.put("item", item);
        proto.send("track_begin", options);
    }

    public void onstop(Item item) {
        KWargs options = new KWargs();
        options.put("item", item);
        proto.send("track_end", options);
    }

    public void onsuspend(Item item, Integer position_ms) {
        KWargs options = new KWargs();
        options.put("item", item);
        options.put("position_ms", position_ms);
        options.put("position_seconds", position_ms / 1000);
        proto.send("track_suspend", options);
    }

    public void onresume(Item item, Integer position_ms) {
        KWargs options = new KWargs();
        options.put("item", item);
        options.put("position_ms", position_ms);
        options.put("position_seconds", position_ms / 1000);
        proto.send("track_resume", options);
    }

    public void onerror(Item item, String message) {
        KWargs options = new KWargs();
        options.put("item", item);
        options.put("message", message);
        proto.send("playback_error", options);
    }
}
