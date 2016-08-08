package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class RequestSshTunnel extends BaseCommand {
    public RequestSshTunnel(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        return ack(true, "ssh tunnel is not supported by this platform");
    }

    private boolean ack(boolean ok, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", get_sequence());
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }
}
