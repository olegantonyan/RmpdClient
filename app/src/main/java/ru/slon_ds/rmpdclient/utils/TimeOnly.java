package ru.slon_ds.rmpdclient.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeOnly implements Comparable<TimeOnly> {
    public Integer hours;
    public Integer minutes;
    public Integer seconds;

    public TimeOnly(String time_string) throws IllegalArgumentException {
        String parts[] = time_string.split(":");
        if (parts.length != 3 ) {
            throw new IllegalArgumentException("invalid time format, only %H:%M:%S is supported");
        }
        hours = Integer.valueOf(parts[0]);
        minutes = Integer.valueOf(parts[1]);
        seconds = Integer.valueOf(parts[2]);
        if (hours > 23 || hours < 0) {
            throw new IllegalArgumentException("invalid hours value " + hours.toString());
        }
        if (minutes > 59 || minutes < 0) {
            throw new IllegalArgumentException("invalid minutes value " + minutes.toString());
        }
        if (seconds > 59 || seconds < 0) {
            throw new IllegalArgumentException("invalid seconds value " + seconds.toString());
        }
    }

    public TimeOnly(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        hours = cal.get(Calendar.HOUR_OF_DAY);
        minutes = cal.get(Calendar.MINUTE);
        seconds = cal.get(Calendar.SECOND);

    }

    public boolean equals(TimeOnly other) {
        return hours.equals(other.hours) && minutes.equals(other.minutes) && seconds.equals(other.seconds);
    }

    public boolean greater(TimeOnly other) {
        if (equals(other)) {
            return false;
        }
        if (hours > other.hours) {
            return true;
        } else if (hours < other.hours) {
            return false;
        }
        if (minutes > other.minutes) {
            return true;
        } else if (minutes < other.minutes) {
            return false;
        }
        return seconds > other.seconds;
    }

    public boolean greater_or_equal(TimeOnly other) {
        return equals(other) || greater(other);
    }

    public boolean less_or_equal(TimeOnly other) {
        return equals(other) || less(other);
    }

    public boolean less(TimeOnly other) {
        return !equals(other) && !greater(other);
    }

    @Override
    public int compareTo(TimeOnly other) {
        if (equals(other)) {
            return 0;
        } else if (greater(other)) {
            return 1;
        } else {
            return -1;
        }
    }

    public String toString() {
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public Integer to_seconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }
}
