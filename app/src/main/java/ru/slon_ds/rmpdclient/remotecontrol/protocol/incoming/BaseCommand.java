package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.protocol.Sender;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public abstract class BaseCommand {
    private ControlWrapper control_wrapper = null;
    private JsonDict data = null;
    private Integer sequence = 0;

    public BaseCommand(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        this.control_wrapper = control_wrapper;
        this.data = data;
        this.sequence = sequence_number;
    }

    public abstract boolean call();

    protected ControlWrapper get_control_wrapper() {
        return control_wrapper;
    }

    protected JsonDict get_data() {
        return data;
    }

    protected Integer get_sequence() {
        return sequence;
    }

    protected Sender sender(String msgtype) {
        try {
            return new Sender(control_wrapper, msgtype);
        } catch (Exception e) {
            Logger.exception(this, "error instantiating sender object", e);
            return null;
        }
    }
}
