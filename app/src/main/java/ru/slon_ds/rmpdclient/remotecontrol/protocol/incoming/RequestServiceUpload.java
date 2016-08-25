package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;
import ru.slon_ds.rmpdclient.utils.ServiceUpload;

public class RequestServiceUpload extends BaseCommand implements UploadWorker.OnUploadFinishedCallback {
    public RequestServiceUpload(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        new UploadWorker("manual_request", get_sequence(), this).start();
        return true;
    }

    private boolean ack(boolean ok, Integer seq, String msg) {
        KWargs options = new KWargs();
        options.put("sequence", seq);
        options.put("message", msg);
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }

    @Override
    public void onfinished(boolean ok, Integer seq, String message) {
        Logger.info(this, message);
        ack(ok, seq, message);
    }
}

class UploadWorker extends Thread {
    private String reason = null;
    private Integer seq = 0;
    private OnUploadFinishedCallback callback = null;

    public UploadWorker(String reason, Integer seq, OnUploadFinishedCallback cb) {
        super();
        this.reason = reason;
        this.seq = seq;
        this.callback = cb;
    }

    interface OnUploadFinishedCallback {
        void onfinished(boolean ok, Integer seq, String message);
    }

    @Override
    public void run() {
        setName("upload_worker");
        boolean ok = false;
        String message = "";
        try {
            new ServiceUpload(reason).run_sync();
            ok = true;
            message = "service upload complete";
        } catch (Exception e) {
            message = "error uploading file";
            ok = false;
            Logger.exception(this, message, e);
        } finally {
            if (callback != null && !isInterrupted()) {
                callback.onfinished(ok, seq, message);
            }
        }
    }
}