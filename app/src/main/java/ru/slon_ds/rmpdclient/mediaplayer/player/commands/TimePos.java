package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.player.ImagePlayer;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class TimePos extends BaseCommand {
    public TimePos(VideoView vv, ImagePlayer ip, KWargs options) {
        super(vv, ip, options);
    }

    @Override
    public KWargs call() {
        KWargs result = new KWargs();
        int ms = 0;
        if (video_view.isPlaying()) {
            ms = video_view.getCurrentPosition();
        }
        result.put("result", ms);
        result.put("units", "ms");
        return result;
    }
}
