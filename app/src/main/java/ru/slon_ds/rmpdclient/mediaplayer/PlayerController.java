package ru.slon_ds.rmpdclient.mediaplayer;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Loader;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Playlist;
import ru.slon_ds.rmpdclient.utils.Logger;

public class PlayerController {
    public PlayerController() {
    }

    public void start_playlist() {
        Loader loader = new Loader();
        if (loader.file_exists()) {
            new Playlist();
            // todo
        } else {
            Logger.error(this, "playlist file '" + loader.filepath() + "' does not exists");
        }
    }

    public void stop() {
    }

    public void quit() {
    }

    public String current_track_name() {
        return "nothing";
    }

    public Integer current_track_position() {
        return 0;
    }
}
