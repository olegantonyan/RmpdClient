package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;

public class RequestSshTunnel extends BaseCommand {
    public RequestSshTunnel(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        return ack(true, "ssh tunnel is not supported by this platform");
    }
}
