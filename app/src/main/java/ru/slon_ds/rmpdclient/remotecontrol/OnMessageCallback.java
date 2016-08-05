package ru.slon_ds.rmpdclient.remotecontrol;

interface OnMessageCallback {
    void onmessage(IncomingMessage msg, Integer seq);
}
