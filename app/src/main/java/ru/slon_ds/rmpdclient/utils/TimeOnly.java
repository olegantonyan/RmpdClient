package ru.slon_ds.rmpdclient.utils;

public class TimeOnly {
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

    public boolean less(TimeOnly other) {
        return !equals(other) && !greater(other);
    }
}
