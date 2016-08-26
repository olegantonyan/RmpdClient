package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.player.ImagePlayer;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Stop extends BaseCommand {
    public Stop(VideoView vv, ImagePlayer ip, KWargs options) {
        super(vv, ip, options);
    }

    @Override
    public KWargs call() {
        //video_view.seekTo(video_view.getDuration());
        video_view.stopPlayback();
        //video_view.setVisibility(View.GONE); // clear last frame
        //video_view.setVisibility(View.VISIBLE);
        Logger.info(this, "stopped");
        return new KWargs();
    }
}
