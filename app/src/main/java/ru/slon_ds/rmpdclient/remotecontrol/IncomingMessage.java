package ru.slon_ds.rmpdclient.remotecontrol;

import org.json.JSONObject;

public class IncomingMessage extends JSONObject {
    public IncomingMessage(String json) throws org.json.JSONException {
        super(json);
    }

    public IncomingMessage() {
        super();
    }

    public Object fetch(String key, Object default_value) {
        try {
            return get(key);
        } catch (org.json.JSONException e) {
            return default_value;
        }
    }

    public Object fetch(String key) {
        return fetch(key, null);
    }
}
