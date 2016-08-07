package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import java.util.Locale;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class NowPlaying extends BaseCommand {
    public NowPlaying(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    @Override
    public boolean call(KWargs options) {
        String track = options.fetch("track", String.class, "nothing");
        Integer percent_position = options.fetch("percent_position", Integer.class, 0);
        set_queued(false);
        set_message(String.format(Locale.US, "%s (%d%%)", track, percent_position));
        return super.call(options);
    }
}
