package ru.slon_ds.rmpdclient.utils;

public class Support {
    public static String underscore_to_camelcase(String snake_case_string) {
        String[] parts = snake_case_string.split("_");
        String result = "";
        for (String part : parts) {
            result += part.substring(0, 1).toUpperCase() + part.substring(1);
        }
        return result;
    }
}
