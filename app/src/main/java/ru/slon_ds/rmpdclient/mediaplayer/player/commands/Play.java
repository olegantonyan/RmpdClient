package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Play extends BaseCommand {
    public Play(VideoView vv, KWargs options) {
        super(vv, options);
    }

    @Override
    public KWargs call() {
        String path = options.fetch("item", Item.class, null).filepath();
        Logger.info(this, "starting track " + path);
        video_view.setVideoPath(path);
        video_view.setZOrderOnTop(true);
        video_view.requestFocus();
        video_view.start();
        return new KWargs();
    }
}
