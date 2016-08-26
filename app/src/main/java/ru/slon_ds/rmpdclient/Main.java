package ru.slon_ds.rmpdclient;

import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;
import ru.slon_ds.rmpdclient.utils.ServiceUpload;

public class Main extends Thread {
    private PlayerWrapper player_wrapper = null;

    public Main(PlayerWrapper player_wrapper) {
        super();
        this.player_wrapper = player_wrapper;
        setDaemon(true);
    }

    @Override
    public void run() {
        setName("main_loop");
        setPriority(Thread.MIN_PRIORITY);
        Logger.info(this, "started");
        ProtocolDispatcher proto = ProtocolDispatcher.instance();
        PlayerController player = new PlayerController(player_wrapper);
        player.load_wallpaper();
        player.start_playlist();
        check_previous_crash();
        while (!isInterrupted()) {
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
        Logger.info(this, "finishing main");
        player.quit();
        proto.quit();
    }

    private void check_previous_crash() {
        if (DefaultUncaughtExceptionHandler.is_after_crash()) {
            new Thread(new ServiceUpload("crash")).start();
            DefaultUncaughtExceptionHandler.set_after_crash_state(false);
        }
    }
}
