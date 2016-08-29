package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class Playlist {
    private JsonDict data = null;
    private ArrayList<Item> background = new ArrayList<>();
    private ArrayList<Item> advertising = new ArrayList<>();
    private Integer current_background_position = 0;

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
            if (data.fetch("shuffle", Boolean.class, false)) {
                Collections.shuffle(background);
            } else {
                Collections.sort(background, new Comparator<Item>() {
                    @Override
                    public int compare(Item i1, Item i2) {
                        return i1.position().compareTo(i2.position());
                    }
                });
            }
        } catch (Exception e) {
            Logger.exception(this, "error loading playlist", e);
        }
    }

    public Item first_background() {
        if (background.isEmpty()) {
            return null;
        }
        return find_next_appropriate(background, 0, background.size());
    }

    public Item next_background() {
        Item item = find_next_appropriate(background, current_background_position + 1, background.size());
        if (item != null) {
            return item;
        }
        item = find_next_appropriate(background, 0, current_background_position + 1);
        if (item != null) {
            return item;
        }
        return null;
    }

    public Item next_advertizing() {
        Date thetime = new Date();
        ArrayList<Item> appropriate_now = new ArrayList<>();
        for (Item i : advertising) {
            if (i.is_appropriate_at(thetime)) {
                appropriate_now.add(i);
            }
        }
        if (appropriate_now.size() == 0) {
            return null;
        }
        for (Item i : appropriate_now) {
            if (i.is_required_at(thetime)) {
                return i;
            }
        }
        return null;
    }

    public void onfinished(Item item) {
        if (item == null) {
            return;
        }
        if (item.is_background()) {
            current_background_position = background_item_position(item);
            if (current_background_position == null) {
                current_background_position = 0;
            }
        }
    }

    private Item find_next_appropriate(ArrayList<Item> collection, Integer start_pos, Integer end_pos) {
        Date thetime = new Date();
        if (collection.size() > 0) {
            for (int i = start_pos; i < end_pos; i++) {
                Item item = collection.get(i);
                if (item.is_appropriate_at(thetime)) {
                    return item;
                }
            }
        }
        return null;
    }

    private ArrayList<Item> all_items() {
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<JsonDict> items = data.fetch_array_of_objects("items");
        for (JsonDict i : items) {
            result.add(new Item(i));
        }
        return result;
    }

    private Integer background_item_position(Item item) {
        for (int i = 0; i < background.size(); i++) {
            if (item.id().equals(background.get(i).id())) {
                return i;
            }
        }
        return null;
    }
}
