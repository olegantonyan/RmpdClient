package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class TimePos extends BaseCommand {
    public TimePos(PlayerInterface p, KWargs options) {
        super(p, options);
    }

    @Override
    public KWargs call() {
        KWargs result = new KWargs();
        int ms = 0;
        if (player.is_playing()) {
            ms = player.time_ms_pos();
        }
        result.put("result", ms);
        result.put("units", "ms");
        return result;
    }
}
