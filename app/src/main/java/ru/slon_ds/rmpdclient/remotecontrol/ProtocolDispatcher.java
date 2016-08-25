package ru.slon_ds.rmpdclient.remotecontrol;

import ru.slon_ds.rmpdclient.remotecontrol.playlist.DownloadWorkerManager;
import ru.slon_ds.rmpdclient.remotecontrol.protocol.Receiver;
import ru.slon_ds.rmpdclient.remotecontrol.protocol.Sender;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class ProtocolDispatcher implements ControlWrapper.OnMessageCallback {
    private static ProtocolDispatcher _instance = null;
    private ControlWrapper control_wrapper = null;

    public static ProtocolDispatcher instance() {
        if (_instance == null) {
            _instance = new ProtocolDispatcher();
        }
        return _instance;
    }

    private ProtocolDispatcher() {
        this.control_wrapper = new ControlWrapper(this);
        send("power_on");
    }

    @Override
    public void onmessage(JsonDict msg, Integer seq) {
        if (msg == null || msg.length() == 0) {
            Logger.debug(this, "received message is null or empty");
            return;
        }
        try {
            new Receiver(control_wrapper, msg, seq).call();
        } catch (Exception e) {
            Logger.exception(this, "error processing message " + msg.toString(), e);
        }
    }

    public boolean send(String command_type, KWargs options) {
        try {
            return new Sender(control_wrapper, command_type).call(options);
        } catch (Exception e) {
            Logger.exception(this, "error sending message", e);
            return false;
        }
    }

    public boolean send(String command_type) {
        return send(command_type, new KWargs());
    }

    public void quit() {
        control_wrapper.quit();
        DownloadWorkerManager.instance().stop();
        _instance = null;
    }
}
