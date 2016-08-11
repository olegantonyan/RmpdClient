package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Stop extends BaseCommand {
    public Stop(VideoView vv, KWargs options) {
        super(vv, options);
    }

    @Override
    public KWargs call() {
        //video_view.seekTo(video_view.getDuration());
        video_view.stopPlayback();
        Logger.info(this, "stopped");
        return new KWargs();
    }
}