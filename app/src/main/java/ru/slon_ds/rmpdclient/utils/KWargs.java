package ru.slon_ds.rmpdclient.utils;

import java.util.HashMap;

public class KWargs extends HashMap <String, Object> {
    public KWargs() {
        super();
    }

    public <T> T fetch(String key, Class<T> type, T default_value) {
        Object value = get(key);
        if (value != null && value.getClass().isInstance(type)) {
            return type.cast(value);
        } else {
            return default_value;
        }
    }
}
