package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;

public class SeekTo extends BaseCommand {
    public SeekTo(VideoView vv, KWargs options) {
        super(vv, options);
    }

    @Override
    public KWargs call() {
        Integer ms = options.fetch("position_ms", Integer.class, 0);
        if (ms != null) {
            video_view.seekTo(ms);
        }
        return new KWargs();
    }
}
