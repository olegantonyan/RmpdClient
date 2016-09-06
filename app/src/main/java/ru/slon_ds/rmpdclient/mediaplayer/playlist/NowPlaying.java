package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.common.Logger;

public class NowPlaying {
    private Item item = null;

    public synchronized void set(Item item) {
        this.item = item;
        if (item == null) {
            Logger.debug(this, "set now playing null");
        } else {
            Logger.debug(this, "set now playing " + item.toString());
        }
    }

    public synchronized Item get() {
        return item;
    }
}
