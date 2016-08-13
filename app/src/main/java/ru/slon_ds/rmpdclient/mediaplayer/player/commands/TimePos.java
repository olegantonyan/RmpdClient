package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;

public class TimePos extends BaseCommand {
    public TimePos(VideoView vv, KWargs options) {
        super(vv, options);
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
