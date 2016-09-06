package ru.slon_ds.rmpdclient.mediaplayer.player;

import java.util.HashMap;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Watchdog {
    private HashMap<Item, Integer> failures = null;
    private final Integer MAX_ERRORS = 5;

    public Watchdog() {
        failures = new HashMap<>();
    }

    public void error_with(Item track) {
        Integer current_errors = failures_count(track);
        Integer errors = current_errors + 1;
        if (errors  >= MAX_ERRORS) {
            notify_bark(track, errors);
        }
        failures.put(track, errors);
    }

    public boolean is_ok_to_start(Item track) {
        return failures_count(track) < MAX_ERRORS;
    }

    public boolean is_ok_to_resume(Item track) {
        return is_ok_to_start(track);
    }

    public void reset() {
        failures.clear();
    }

    private Integer failures_count(Item track) {
        Integer i = failures.get(track);
        if (i == null) {
            return 0;
        }
        return i;
    }

    private void notify_bark(Item track, Integer error_count) {
        Logger.error(this, "max number of errors " + error_count.toString() + " reached for track " + track.toString() + ", watchdog will prevent subsequent playbacks");
    }
}
