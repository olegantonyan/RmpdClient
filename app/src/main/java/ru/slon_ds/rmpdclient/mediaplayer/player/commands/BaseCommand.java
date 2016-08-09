package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;

public abstract class BaseCommand {
    protected VideoView video_view = null;
    protected KWargs options = null;

    public BaseCommand(VideoView vv, KWargs options) {
        this.video_view = vv;
        this.options = options;
    }

    public abstract KWargs call();
}
