package ru.slon_ds.rmpdclient.remotecontrol;

import ru.slon_ds.rmpdclient.utils.Logger;

public class ControlWrapper {
    private CheckQueueLoop check_queue_loop = null;

    public ControlWrapper() {
        this.check_queue_loop = new CheckQueueLoop();
        this.check_queue_loop.start();
    }

    private class CheckQueueLoop extends Thread {
        private boolean stop_flag = false;

        @Override
        public void run() {
            while (!stop_flag) {
                try {
                    Logger.info(this, "i am alive ");
                } catch (Throwable e) {
                    Logger.exception(this, "error checking message queue", e);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.warning(this, "interrupted");
                    break;
                }
            }
            Logger.debug(this, "stopping check queue loop");
        }

        public void soft_stop() {
            this.stop_flag = true;
        }
    }


}
