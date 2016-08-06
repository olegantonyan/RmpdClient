package ru.slon_ds.rmpdclient.remotecontrol;

import ru.slon_ds.rmpdclient.remotecontrol.protocol.Sender;
import ru.slon_ds.rmpdclient.utils.Logger;

public class ProtocolDispatcher implements ControlWrapper.OnMessageCallback {
    private static ProtocolDispatcher instance = null;
    private ControlWrapper control_wrapper = null;

    public static ProtocolDispatcher getInstance() {
        if (instance == null) {
            instance = new ProtocolDispatcher();
        }
        return instance;
    }

    private ProtocolDispatcher() {
        this.control_wrapper = new ControlWrapper(this);
        send("power_on");
    }

    @Override
    public void onmessage(IncomingMessage msg, Integer seq) {
        Logger.debug(this, "onmessage");
    }

    public boolean send(String command_type, Object options) {
        try {
            return new Sender(control_wrapper, command_type).call(options);
        } catch (Exception e) {
            Logger.exception(this, "error sending message", e);
            return false;
        }
    }

    public boolean send(String command_type) {
        return send(command_type, null);
    }
}
