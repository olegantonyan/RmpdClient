package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class AckFail extends BaseCommand {
    public AckFail(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    @Override
    public boolean call(KWargs options) {
        if (options != null) {
            set_message(options.fetch("message", String.class, ""));
            set_sequence(options.fetch("sequence", Integer.class, 0));
        }
        return super.call(options);
    }
}
