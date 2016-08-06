package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.IncomingMessage;

public class UpdateSetting extends BaseCommand {
    public UpdateSetting(ControlWrapper control_wrapper, IncomingMessage data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        return false;
    }

    private boolean ack(boolean ok, String msg) {
        return false;
    }
}
