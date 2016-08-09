package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class IsPlaying extends BaseCommand {
    public IsPlaying(VideoView vv, KWargs options) {
        super(vv, options);
    }

    @Override
    public KWargs call() {
        KWargs result = new KWargs();
        result.put("result", video_view.isPlaying());
        return result;
    }
}
