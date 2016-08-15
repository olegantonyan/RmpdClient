package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class UpdateWallpaper extends BaseCommand {
    public UpdateWallpaper(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        KWargs options = new KWargs();
        options.put("message", "set wallpaper is not supported yet");
        return sender("ack_ok").call(options);
    }
}
