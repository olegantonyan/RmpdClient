package ru.slon_ds.rmpdclient.remotecontrol;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    public OutgoingMessage merge_hashmap(HashMap<String, Object> other) throws JSONException {
        for (Map.Entry<String, Object> entry: other.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof HashMap) {
                put(key, new OutgoingMessage().merge_hashmap((HashMap<String, Object>)value));
            } else {
                put(key, value);
            }
        }
        return this;
    }
}
