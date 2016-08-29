package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class SeekTo extends BaseCommand {
    public SeekTo(PlayerInterface p, KWargs options) {
        super(p, options);
    }

    @Override
    public KWargs call() {
        Integer ms = options.fetch("position_ms", Integer.class, 0);
        if (ms != null) {
            player.seek_to(ms);
        }
        return new KWargs();
    }
}
