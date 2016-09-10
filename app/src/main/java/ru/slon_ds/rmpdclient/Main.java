package ru.slon_ds.rmpdclient;

import ru.slon_ds.rmpdclient.common.Logger;
import ru.slon_ds.rmpdclient.common.SelfUpdate;
import ru.slon_ds.rmpdclient.common.ServiceUpload;
import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerGuard;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class Main extends Thread {
    private PlayerGuard player_guard = null;

    public Main(PlayerGuard player_guard) {
        super();
        this.player_guard = player_guard;
        setDaemon(true);
    }

    @Override
    public void run() {
        setName("main_loop");
        setPriority(Thread.MIN_PRIORITY);

        Logger.info(this, "started");

        ProtocolDispatcher proto = ProtocolDispatcher.instance();

        PlayerController player = new PlayerController(player_guard);
        player.load_wallpaper();
        player.start_playlist();

        check_previous_crash();

        new SelfUpdate().verify();

        while (!isInterrupted()) {
            KWargs kw = new KWargs();
            kw.put("percent_position", player.current_track_position());
            kw.put("track", player.current_track_name());
            proto.send("now_playing", kw);

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Logger.info(this, "interrupted Main");
                interrupt();
                break;
            }
        }
        Logger.info(this, "finishing main");

        player.quit();

        proto.quit();
    }

    public void quit() {
        Logger.debug(this, "stopping main");
        if (!isAlive()) {
            Logger.debug(this, "main is already stopped");
            return;
        }
        interrupt();
        try {
            join();
        } catch (InterruptedException e) {
            Logger.exception(this, "waiting for main to finish interrupted", e);
            interrupt();
        }
    }

    private void check_previous_crash() {
        if (DefaultUncaughtExceptionHandler.is_after_crash()) {
            new Thread(new ServiceUpload("crash")).start();
            DefaultUncaughtExceptionHandler.set_after_crash_state(false);
        }
    }
}
