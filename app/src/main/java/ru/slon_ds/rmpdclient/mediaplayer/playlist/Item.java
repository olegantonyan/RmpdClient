package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.ArrayList;
import java.util.Date;

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
        String s = d.fetch("begin_time", String.class);
        if (s == null) {
            return null;
        }
        return new TimeOnly(s);
    }

    public TimeOnly end_time() {
        String s = d.fetch("end_time", String.class);
        if (s == null) {
            return null;
        }
        return new TimeOnly(s);
    }

    public DateOnly begin_date() {
        String s = d.fetch("begin_date", String.class);
        if (s == null) {
            return null;
        }
        return new DateOnly(s);
    }

    public DateOnly end_date() {
        String s = d.fetch("end_date", String.class);
        if (s == null) {
            return null;
        }
        return new DateOnly(s);
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

    public boolean is_wait_for_the_end() {
        return d.fetch("wait_for_the_end", Boolean.class, false);
    }

    public boolean is_appropriate_at(Date datetime) {
        return fit_time(datetime) && fit_date(datetime);
    }

    public boolean is_required_at(Date thetime) {
        if (!is_advertising()) {
            return false;
        }
        Integer seconds = new TimeOnly(thetime).to_seconds();
        Integer low = seconds - 1;
        Integer high = seconds + 1;
        for (TimeOnly i : schedule_at(thetime)) {
            if (low <= i.to_seconds() && i.to_seconds() <= high) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<ScheduleInterval> schedule_intervals() {
        ArrayList<ScheduleInterval> result = new ArrayList<>();
        for (JsonDict i : d.fetch_array_of_objects("schedule_intervals")) {
            result.add(new ScheduleInterval(i));
        }
        return result;
    }

    public ArrayList<TimeOnly> schedule_at(Date date) {
        DateOnly dt = new DateOnly(date);
        for (ScheduleInterval i : schedule_intervals()) {
            if (i.date_interval().begin.less_or_equal(dt) && dt.less_or_equal(i.date_interval().end)) {
                return i.schedule();
            }
        }
        return new ArrayList<>();
    }

    private boolean fit_time(Date time) {
        if (begin_time() == null || end_time() == null) {
            return true;
        }
        TimeOnly tm = new TimeOnly(time);
        return begin_time().less_or_equal(tm) && tm.less_or_equal(end_time());
    }

    private boolean fit_date(Date date) {
        if (begin_date() == null || end_date() == null) {
            return true;
        }
        DateOnly dt = new DateOnly(date);
        return begin_date().less_or_equal(dt) && dt.less_or_equal(end_date());
    }

    public String toString() {
        return filename();
    }
}

class ScheduleInterval {
    private JsonDict d = null;

    public ScheduleInterval(JsonDict raw) {
        d = raw;
    }

    public DateInterval date_interval() {
        DateInterval di = new DateInterval();
        di.begin = new DateOnly(d.fetch_object("date_interval").fetch("begin", String.class, null));
        di.end = new DateOnly(d.fetch_object("date_interval").fetch("end", String.class, null));
        return di;
    }

    public ArrayList<TimeOnly> schedule() {
        ArrayList<TimeOnly> result = new ArrayList<>();
        for (String i : d.fetch_array("schedule", String.class)) {
            result.add(new TimeOnly(i));
        }
        return result;
    }
}

class DateInterval {
    public DateOnly begin = null;
    public DateOnly end = null;
}