package ru.slon_ds.rmpdclient.utils;

public class DateOnly {
    private Integer year = null;
    private Integer month = null;
    private Integer day = null;

    public DateOnly(String date) throws IllegalArgumentException {
        String parts[] = date.split(".");
        if (parts.length != 3) {
            throw new IllegalArgumentException("invalid date format, only %d.%m.%Y is supported");
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
}
