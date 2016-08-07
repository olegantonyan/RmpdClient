package ru.slon_ds.rmpdclient.remotecontrol;

import org.json.JSONObject;

public class IncomingMessage extends JSONObject {
    public IncomingMessage(String json) throws org.json.JSONException {
        super(json);
    }

    public IncomingMessage() {
        super();
    }

    public <T> T fetch(String key, Class<T> type, T default_value) {
        try {
            Object value = get(key);
            if (value != null && value.getClass().isInstance(type)) {
                return type.cast(value);
            } else {
                return default_value;
            }
        } catch (org.json.JSONException e) {
            return default_value;
        }
    }

    public <T> T fetch(String key, Class<T> type) {
        return fetch(key, type, null);
    }
}
