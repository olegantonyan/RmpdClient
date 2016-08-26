package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.player.ImagePlayer;
import ru.slon_ds.rmpdclient.utils.KWargs;

public abstract class BaseCommand {
    protected VideoView video_view = null;
    protected ImagePlayer image_player = null;
    protected KWargs options = null;

    public BaseCommand(VideoView vv, ImagePlayer ip, KWargs options) {
        this.video_view = vv;
        this.image_player = ip;
        this.options = options;
    }

    public abstract KWargs call();
}
