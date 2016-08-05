package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.Logger;

public class PowerOn extends BaseCommand {
    public PowerOn(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    @Override
    public boolean call(Object options) {
        Logger.info(this, "yay!");
        return super.call(options);
    }
}
