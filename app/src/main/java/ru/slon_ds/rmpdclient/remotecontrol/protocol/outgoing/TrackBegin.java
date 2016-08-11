package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class TrackBegin extends BaseCommand {
    public TrackBegin(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    public boolean call(KWargs options) {
        Item item = options.fetch("item", Item.class, null);
        if (item == null) {
            Logger.error(this, "item is null");
            return false;
        }
        set_message(track_message(item));
        return super.call(options);
    }
}
