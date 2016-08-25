package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.io.File;
import java.io.IOException;

import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdateWallpaper extends BaseCommand implements Runnable {
    public UpdateWallpaper(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    private Thread thread = null;

    @Override
    public boolean call() {
        thread = new Thread(this);
        thread.setName("download_wallpaper");
        thread.start();
        return true;
    }

    @Override
    public void run() {
        boolean ok = false;
        String message = "";
        try {
            String url = get_data().fetch("url", String.class, null);
            if (url == null) {
                File f = new File(Files.wallpaper_filepath());
                if (f.exists()) {
                    if (!f.delete()) {
                        throw new IOException("error deleting wallpaper");
                    }
                }
            } else {
                HttpClient.new_default().download_file(Files.full_url_by_relative(url), Files.wallpaper_filepath(), 3);
            }
            if (PlayerController.instance() != null && PlayerController.instance().load_wallpaper()) {
                message = "set wallpaper";
                ok = true;
            } else {
                message = "error loading wallpaper";
            }
        } catch (Exception e) {
            message = "error downloading wallpaper";
            ok = false;
            Logger.exception(this, message, e);
        } finally {
            if (!thread.isInterrupted()) {
                ack(ok, message);
            }
        }
    }
}
