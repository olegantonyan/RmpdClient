package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.ArrayList;

import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Playlist {
    private JsonDict data = null;
    private ArrayList<Item> background = new ArrayList<>();
    private ArrayList<Item> advertising = new ArrayList<>();

    public Playlist() {
        try {
            this.data = new Loader().load(); // TODO dependency injection
            for (Item i : all_items()) {
                if (i.is_advertising()) {
                    this.advertising.add(i);
                } else if (i.is_background()) {
                    this.background.add(i);
                }
            }
        } catch (Exception e) {
            Logger.exception(this, "error loading playlist", e);
        }
    }

    public Item first_background() {
        if (background.isEmpty()) {
            return null;
        }
        return background.get(0);
    }

    private ArrayList<Item> all_items() {
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<JsonDict> items = data.fetch_array_of_objects("items");
        for (JsonDict i : items) {
            result.add(new Item(i));
        }
        return result;
    }
}
