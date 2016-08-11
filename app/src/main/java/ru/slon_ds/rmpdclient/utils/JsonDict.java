package ru.slon_ds.rmpdclient.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonDict extends JSONObject {
    public JsonDict(String json_string) throws JSONException {
        super(json_string);
    }

    public JsonDict() {
        super();
    }

    public JsonDict(HashMap<String, Object> kwargs) throws JSONException {
        super();
        merge_hashmap(kwargs);
    }

    public JsonDict merge_hashmap(HashMap<String, Object> other) throws JSONException {
        for (Map.Entry<String, Object> entry: other.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof HashMap) {
                put(key, new JsonDict().merge_hashmap((HashMap<String, Object>)value));
            } else {
                put(key, value);
            }
        }
        return this;
    }

    public <T> T fetch(String key, Class<T> type, T default_value) {
        try {
            Object value = get(key);
            if (value != null && type.isInstance(value)) {
                return type.cast(value);
            } else {
                return default_value;
            }
        } catch (JSONException e) {
            return default_value;
        }
    }

    public JsonDict fetch_object(String key) {
        JsonDict result = new JsonDict();
        try {
            JSONObject value = getJSONObject(key);
            if (value != null) {
                result = new JsonDict(value.toString());
            }
        } catch (JSONException e) {
        }
        return result;
    }

    public <T> T fetch(String key, Class<T> type) {
        return fetch(key, type, null);
    }

    public <T> ArrayList<T> fetch_array(String key, Class<T> type) {
        ArrayList<T> result = new ArrayList<>();
        try {
            JSONArray array = getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                Object object = array.get(i);
                if (object != null && type.isInstance(object)) {
                    result.add(type.cast(object));
                }
            }
        } catch (JSONException e) {
        }
        return result;
    }

    public ArrayList<JsonDict> fetch_array_of_objects(String key) {
        ArrayList<JsonDict> result = new ArrayList<>();
        try {
            JSONArray array = getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object != null) {
                    result.add(new JsonDict(object.toString()));
                }
            }
        } catch (JSONException e) {
        }
        return result;
    }
}
