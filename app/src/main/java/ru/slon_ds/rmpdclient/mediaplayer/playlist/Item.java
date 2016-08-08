package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.TimeOnly;

public class Item {
    private JsonDict d;

    public Item(JsonDict d) {
        this.d = d;
    }

    public String filename() {
        return d.fetch("filename", String.class);
    }

    public Integer id() {
        return d.fetch("id", Integer.class);
    }

    public String type() {
        return d.fetch("type", String.class);
    }

    public Integer position() {
        return d.fetch("position", Integer.class);
    }

    public TimeOnly begin_time() {
        return new TimeOnly(d.fetch("begin_time", String.class));
    }

    public TimeOnly end_time() {
        return new TimeOnly(d.fetch("end_time", String.class));
    }

    public boolean is_background() {
        return type().equals("background");
    }

    public boolean is_advertising() {
        return type().equals("advertising");
    }
}
