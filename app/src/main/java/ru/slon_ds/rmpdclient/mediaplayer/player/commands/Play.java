package ru.slon_ds.rmpdclient.mediaplayer.player.commands;

import java.io.File;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerInterface;
import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Play extends BaseCommand {
    public Play(PlayerInterface p, KWargs options) {
        super(p, options);
    }

    @Override
    public KWargs call() {
        final Item item = options.fetch("item", Item.class, null);
        if (item == null) {
            Logger.error(this, "item is null");
        } else {
            final String path = item.filepath();
            if (new File(path).exists()) {
                final String type = item.content_type();
                Logger.info(this, "starting track " + path + " (" + type + ")");
                player.play(path, type, item.show_duration() * 1000);
            } else {
                Logger.error(this, "file " + path + " does not exists");
            }
        }
        return new KWargs();
    }
}
