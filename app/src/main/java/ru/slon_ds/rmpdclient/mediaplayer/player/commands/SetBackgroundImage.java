package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import android.graphics.drawable.Drawable;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class SetBackgroundImage extends BaseCommand {
    public SetBackgroundImage(PlayerInterface p, KWargs options) {
        super(p, options);
    }

    @Override
    public KWargs call() {
        Integer resource_id = options.fetch("resource_id", Integer.class, null);
        if (resource_id != null) {
            Logger.info(this, "loading wallpaper from built in resource");
            //video_view.setBackgroundResource(resource_id);
        } else {
            Drawable image = options.fetch("drawable", Drawable.class, null);
            if (image != null) {
                Logger.info(this, "loading wallpaper file");
                //video_view.setBackground(image);
            }
        }
        return new KWargs();
    }
}
