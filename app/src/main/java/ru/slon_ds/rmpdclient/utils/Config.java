package ru.slon_ds.rmpdclient.utils;

public class Config {
    private static Config _instance = new Config();

    public static Config instance() {
        return _instance;
    }

    private Config() {
    }

    public String server_url() {
        return "http://192.168.1.3:3000";
        //return "http://10.0.2.2:3000";
    }

    public String login() {
        return "android";
    }

    public String password() {
        return "12345678";
    }
}
