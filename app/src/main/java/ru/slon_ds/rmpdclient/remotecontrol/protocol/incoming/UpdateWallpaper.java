package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;
import ru.slon_ds.rmpdclient.utils.Config;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdateWallpaper extends BaseCommand implements Runnable {
    public UpdateWallpaper(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        new Thread(this).start();
        return true;
    }

    private boolean ack(boolean ok, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", get_sequence());
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }

    @Override
    public void run() {
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
                http_client().download_file(Files.full_url_by_relative(url), Files.wallpaper_filepath(), 3);
            }
            if (PlayerController.instance().load_wallpaper()) {
                ack(true, "set wallpaper");
            } else {
                ack(false, "error loading wallpaper");
            }
        } catch (Exception e) {
            final String msg = "error downloading wallpaper";
            Logger.exception(this, msg, e);
            ack(false, msg);
        }
    }

    private HttpClient http_client() throws MalformedURLException {
        return new HttpClient(Config.instance().server_url(), Config.instance().login(), Config.instance().password());
    }
}
