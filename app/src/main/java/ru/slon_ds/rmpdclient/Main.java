package ru.slon_ds.rmpdclient;

import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Main extends Thread {
    public Main() {
        super();
    }

    @Override
    public void run() {
        ProtocolDispatcher proto = ProtocolDispatcher.getInstance();
        PlayerController player = new PlayerController();
        player.start_playlist();
        while (true) {
            KWargs kw = new KWargs();
            kw.put("percent_position", player.current_track_position());
            kw.put("track", player.current_track_name());
            proto.send("now_playing", kw);

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Logger.info(this, "interrupted Main");
                break;
            }
        }
    }
}
