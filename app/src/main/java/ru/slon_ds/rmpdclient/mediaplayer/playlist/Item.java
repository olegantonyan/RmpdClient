package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import ru.slon_ds.rmpdclient.utils.DateOnly;
import ru.slon_ds.rmpdclient.utils.Files;
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

    public String filepath() {
        return Files.full_file_localpath(filename());
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

    public DateOnly begin_date() {
        return new DateOnly(d.fetch("begin_date", String.class));
    }

    public DateOnly end_date() {
        return new DateOnly(d.fetch("end_date", String.class));
    }

    public Integer playbacks_per_day() {
        return d.fetch("playbacks_per_day", Integer.class);
    }

    public boolean is_background() {
        return type().equals("background");
    }

    public boolean is_advertising() {
        return type().equals("advertising");
    }
}
