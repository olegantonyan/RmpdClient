package ru.slon_ds.rmpdclient.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateOnly implements Comparable<DateOnly> {
    private Integer year = null;
    private Integer month = null;
    private Integer day = null;

    public DateOnly(String date) throws IllegalArgumentException {
        String parts[] = date.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("invalid date format '" + date + "', only %d.%m.%Y is supported");
        }
        year = Integer.valueOf(parts[2]);
        month = Integer.valueOf(parts[1]);
        day = Integer.valueOf(parts[0]);
        if (year < 0) {
            throw new IllegalArgumentException("invalid year value " + year.toString());
        }
        if (month > 12 || month < 0) {
            throw new IllegalArgumentException("invalid month value " + month.toString());
        }
        if (day > 31 || day < 0) {
            throw new IllegalArgumentException("invalid day value " + day.toString());
        }
    }

    public DateOnly(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    public boolean equals(DateOnly other) {
        return year.equals(other.year) && month.equals(other.month) && day.equals(other.day);
    }

    public  boolean greater(DateOnly other) {
        if (equals(other)) {
            return false;
        }
        if (year > other.year) {
            return true;
        } else if (year < other.year) {
            return false;
        }
        if (month > other.month) {
            return true;
        } else if (month < other.month) {
            return false;
        }
        return day > other.day;
    }

    public boolean greater_or_equal(DateOnly other) {
        return equals(other) || greater(other);
    }

    public boolean less_or_equal(DateOnly other) {
        return equals(other) || less(other);
    }

    public boolean less(DateOnly other) {
        return !equals(other) && !greater(other);
    }

    @Override
    public int compareTo(DateOnly other) {
        if (equals(other)) {
            return 0;
        } else if (greater(other)) {
            return 1;
        } else {
            return -1;
        }
    }

    public String toString() {
        return String.format(Locale.US, "%02d.%02d.%04d", day, month, year);
    }
}
