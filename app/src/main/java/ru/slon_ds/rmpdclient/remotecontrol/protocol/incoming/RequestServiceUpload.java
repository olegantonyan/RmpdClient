package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.common.Logger;
import ru.slon_ds.rmpdclient.common.ServiceUpload;

public class RequestServiceUpload extends BaseCommand implements Runnable {
    public RequestServiceUpload(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    private Thread thread = null;

    @Override
    public boolean call() {
        thread = new Thread(this);
        thread.setName("upload_worker");
        thread.start();
        return true;
    }

    @Override
    public void run() {
        boolean ok = false;
        String message = "";
        try {
            new ServiceUpload("manual_request").run_sync();
            ok = true;
            message = "service upload complete";
        } catch (Exception e) {
            message = "error uploading file";
            ok = false;
            Logger.exception(this, message, e);
        } finally {
            if (!thread.isInterrupted()) {
                ack(ok, message);
            }
        }
    }
}