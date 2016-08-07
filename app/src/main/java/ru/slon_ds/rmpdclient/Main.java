package ru.slon_ds.rmpdclient;

import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Main extends Thread {
    public Main() {
        super();
    }

    @Override
    public void run() {
        ProtocolDispatcher proto = ProtocolDispatcher.getInstance();
        while (true) {
            proto.send("now_playing", new KWargs());

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Logger.info(this, "interrupted Main");
                break;
            }
        }
    }
}
