package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Stop extends BaseCommand {
    public Stop(PlayerInterface p, KWargs options) {
        super(p, options);
    }

    @Override
    public KWargs call() {
        player.stop();
        Logger.info(this, "stopped");
        return new KWargs();
    }
}
