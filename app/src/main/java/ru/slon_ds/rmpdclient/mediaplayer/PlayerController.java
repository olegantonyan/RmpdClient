package ru.slon_ds.rmpdclient.mediaplayer;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Loader;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Playlist;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Scheduler;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class PlayerController {
    private Scheduler scheduler = null;
    private static PlayerController _instance = null;

    public static PlayerController instance() {
        return _instance;
    }

    public PlayerController(PlayerWrapper player_wrapper) {
        scheduler = new Scheduler(player_wrapper);
        _instance = this;
    }

    public void start_playlist() {
        Loader loader = new Loader();
        if (loader.file_exists()) {
            Playlist playlist = new Playlist();
            KWargs k = new KWargs();
            k.put("files", loader.list_all_files());
            ProtocolDispatcher.instance().send("playlist_begin", k);
            scheduler.set_playlist(playlist);
        } else {
            Logger.error(this, "playlist file '" + loader.filepath() + "' does not exists");
        }
    }

    public String current_track_name() {
        return scheduler.current_track_filename();
    }

    public Integer current_track_position() {
        return scheduler.current_track_percent_pos();
    }
}
