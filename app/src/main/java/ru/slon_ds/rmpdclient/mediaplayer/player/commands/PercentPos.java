package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class PercentPos extends BaseCommand {
    public PercentPos(PlayerInterface p, KWargs options) {
        super(p, options);
    }

    @Override
    public KWargs call() {
        KWargs result = new KWargs();
        result.put("result", player.percent_pos());
        return result;
    }
}