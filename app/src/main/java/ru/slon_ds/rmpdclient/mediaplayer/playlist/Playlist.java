package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Playlist {
    private JsonDict data = null;

    public Playlist() {
        try {
            this.data = new Loader().load(); // TODO dependency injection
        } catch (Exception e) {
            Logger.exception(this, "error loading playlist", e);
        }
    }
}
