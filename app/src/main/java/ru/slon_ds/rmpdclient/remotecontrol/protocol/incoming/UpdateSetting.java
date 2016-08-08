package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdateSetting extends BaseCommand {
    public UpdateSetting(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        String msg = "";
        boolean ok = true;
        String tz = get_data().fetch("time_zone", String.class);
        if (tz != null) {
            msg = "changing time zone not supported by this platform yet";
            Logger.warning(this, msg);
        }
        return ack(ok, msg);
    }

    private boolean ack(boolean ok, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", get_sequence());
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }
}
