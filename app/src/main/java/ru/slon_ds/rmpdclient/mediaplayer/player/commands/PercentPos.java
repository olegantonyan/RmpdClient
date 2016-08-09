package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;

public class PercentPos extends BaseCommand {
    public PercentPos(VideoView vv, KWargs options) {
        super(vv, options);
    }

    @Override
    public KWargs call() {
        KWargs result = new KWargs();
        int percent = 0;
        if (video_view.isPlaying()) {
            int position_seconds = video_view.getCurrentPosition() / 1000;
            int duration = video_view.getDuration() / 1000;
            if (duration != 0 && position_seconds >= 0) {
                percent = position_seconds * 100 / duration;
            }
        }
        result.put("result", percent);
        return result;
    }
}