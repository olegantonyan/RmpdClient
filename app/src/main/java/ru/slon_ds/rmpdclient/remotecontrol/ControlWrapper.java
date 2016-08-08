package ru.slon_ds.rmpdclient.remotecontrol;

import java.net.MalformedURLException;
import java.util.Locale;

import ru.slon_ds.rmpdclient.utils.Config;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class ControlWrapper extends Thread {
    private OnMessageCallback callback = null;

    interface OnMessageCallback {
        void onmessage(JsonDict msg, Integer seq);
    }

    public ControlWrapper(OnMessageCallback callback) {
        this.callback = callback;
        start();
    }

    public boolean send(JsonDict message) {
        return send(message, false, 0);
    }

    public boolean send(JsonDict message, Integer sequence_number) {
        return send(message, false, sequence_number);
    }

    public boolean send(JsonDict message, boolean queued) {
        return send(message, queued, 0);
    }

    public boolean send(JsonDict message, boolean queued, Integer sequence_number) {
        if (message == null) {
            return false;
        }
        Logger.debug(this,
                String.format(Locale.US, "sending message (%s|%d): %s", queued ? "queued" : "immed", sequence_number, message.toString()));
        boolean result = false;
        MessageQueue mq = mq();
        try {
            if (queued) {
                MessageQueue.EnqueuedData data = mq.new EnqueuedData(message, sequence_number);
                result = mq.enqueue(data);
            } else {
                HttpClient client = http_client();
                if (client != null) {
                    HttpClient.HttpData received = client.send(message, sequence_number);
                    onreceive(received.data, received.sequence_number);
                    result = true;
                }
            }
        } catch (Exception e) {
            Logger.exception(this, "error sending message '" + message.toString() + "'", e);
        }
        return result;
    }

    @Override
    public void run() {
        MessageQueue mq = mq();
        while (true) {
            try {
                MessageQueue.DequeueResult dequeued = mq.dequeue();
                if (dequeued != null) {
                    MessageQueue.EnqueuedData enqueued = mq.new EnqueuedData(dequeued.data, dequeued.sequence_number);
                    if (send(enqueued.data, false, enqueued.sequence_number)) {
                        mq.remove(dequeued.id);
                    }
                }
            } catch (Exception e) {
                Logger.exception(this, "error checking message queue", e);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.warning(this, "interrupted");
                break;
            }
        }
        Logger.debug(this, "stopping check queue loop");
    }

    private MessageQueue mq() {
        return MessageQueue.instance();
    }

    private HttpClient http_client() {
        try {
            return new HttpClient(Config.instance().server_url(), Config.instance().login(), Config.instance().password());
        } catch (MalformedURLException e) {
            Logger.exception(this, "error creating http client", e);
            return null;
        }
    }

    private void onreceive(JsonDict msg, Integer sequence_number) {
        Logger.debug(this, "received message: " + msg.toString());
        if (callback != null) {
            callback.onmessage(msg, sequence_number);
        }
    }
}
