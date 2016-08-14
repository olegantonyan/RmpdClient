package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class TrackResume extends BaseCommand {
    public TrackResume(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    @Override
    public boolean call(KWargs options) {
        Item item = options.fetch("item", Item.class, null);
        if (item == null) {
            Logger.error(this, "item is null");
            return false;
        }
        KWargs message = track_message(item);
        message.put("position_seconds", options.fetch("position_seconds", Integer.class, null));
        set_message(message);
        return super.call(options);
    }
}
