package ru.slon_ds.rmpdclient.mediaplayer;

import android.graphics.drawable.Drawable;

import java.io.File;

import ru.slon_ds.rmpdclient.R;
import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Loader;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Playlist;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Scheduler;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class PlayerController {
    private Scheduler scheduler = null;
    private PlayerWrapper player = null;
    private static PlayerController _instance = null;

    public static PlayerController instance() {
        return _instance;
    }

    public PlayerController(PlayerWrapper player_wrapper) {
        player = player_wrapper;
        scheduler = new Scheduler(player);
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
            scheduler.set_playlist(null);
        }
    }

    public String current_track_name() {
        return scheduler.current_track_filename();
    }

    public Integer current_track_position() {
        return scheduler.current_track_percent_pos();
    }

    public void quit() {
        scheduler.quit();
        _instance = null;
    }

    public boolean load_wallpaper() {
        final String path = Files.wallpaper_filepath();
        try {
            KWargs options = new KWargs();
            if (new File(path).exists()) {
                options.put("drawable", Drawable.createFromPath(path));
            } else {
                options.put("resource_id", R.drawable.slon_ds_image);
            }
            player.execute("set_background_image", options);
            return true;
        } catch (Exception e) {
            Logger.exception(this, "error setting wallpaper", e);
            return false;
        }

    }
}
