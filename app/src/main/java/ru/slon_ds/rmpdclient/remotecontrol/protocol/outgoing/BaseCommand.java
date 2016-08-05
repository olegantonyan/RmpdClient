package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.OutgoingMessage;

public class BaseCommand {
    private ControlWrapper control_wrapper = null;
    private boolean queued = false;
    private Integer sequence = 0;
    private OutgoingMessage message = null;

    public BaseCommand(ControlWrapper control_wrapper) {
        this.control_wrapper = control_wrapper;
    }

    public boolean call(Object options) {
        return send();
    }

    private boolean send() {
        return control_wrapper.send(message, queued, sequence);
    }

    protected void setQueued(boolean queued) {
        this.queued = queued;
    }

    protected void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    protected void setMessage(OutgoingMessage message) {
        this.message = message;
    }
}
