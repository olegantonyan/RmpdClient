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

    public static String camelcase_to_underscore(String camel_case) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camel_case.replaceAll(regex, replacement).toLowerCase();
    }
}
