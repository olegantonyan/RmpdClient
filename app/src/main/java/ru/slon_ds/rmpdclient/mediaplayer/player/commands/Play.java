package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.widget.VideoView;

import java.io.File;

import ru.slon_ds.rmpdclient.mediaplayer.player.ImagePlayer;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Play extends BaseCommand {
    public Play(VideoView vv, ImagePlayer ip, KWargs options) {
        super(vv, ip, options);
    }

    @Override
    public KWargs call() {
        String path = options.fetch("item", Item.class, null).filepath();
        if (path == null) {
            Logger.error(this, "file path is null");
        } else if (new File(path).exists()) {
            Logger.info(this, "starting track " + path);
            video_view.setVideoPath(path);
            video_view.start();
            // video_view.setZOrderOnTop(true);
            // video_view.requestFocus();
        } else {
            Logger.error(this, "file " + path + " does not exists");
        }
        return new KWargs();
    }
}
