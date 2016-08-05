package ru.slon_ds.rmpdclient.remotecontrol;

import org.json.JSONObject;

public class OutgoingMessage extends JSONObject {
    public OutgoingMessage(String json) throws org.json.JSONException {
        super(json);
    }

    public OutgoingMessage() {
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
