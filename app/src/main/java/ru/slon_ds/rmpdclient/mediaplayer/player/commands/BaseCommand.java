package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.utils.KWargs;

public abstract class BaseCommand {
    protected PlayerInterface player = null;
    protected KWargs options = null;

    public BaseCommand(PlayerInterface p, KWargs options) {
        this.player = p;
        this.options = options;
    }

    public abstract KWargs call();
}
