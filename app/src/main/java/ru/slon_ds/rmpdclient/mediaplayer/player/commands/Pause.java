package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Pause extends BaseCommand {
    public Pause(VideoView vv, KWargs options) {
        super(vv, options);
    }

    @Override
    public KWargs call() {
        video_view.pause();
        Logger.info(this, "paused");
        return new KWargs();
    }
}
