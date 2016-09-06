package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.slon_ds.rmpdclient.mediaplayer.playlist.Item;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.common.Control;
import ru.slon_ds.rmpdclient.common.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.common.Logger;
import ru.slon_ds.rmpdclient.utils.Support;

public class BaseCommand {
    private ControlWrapper control_wrapper = null;
    private boolean queued = true;
    private Integer sequence = 0;
    private Object message = null;
    private KWargs json = new KWargs();

    public BaseCommand(ControlWrapper control_wrapper) {
        this.control_wrapper = control_wrapper;
    }

    public boolean call(KWargs options) {
        try {
            return send();
        } catch (JSONException e) {
            Logger.exception(this, "error calling send on command object", e);
            return false;
        }
    }

    private boolean send() throws JSONException {
        return control_wrapper.send(default_data().merge_hashmap(json), queued, sequence);
    }

    protected String type() {
        return Support.camelcase_to_underscore(getClass().getSimpleName());
    }

    protected Long free_space() {
       // return 100000000L;
       return Control.free_space(Files.mediafiles_path());
    }

    protected String thetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ", Locale.US);
        return sdf.format(new Date());
    }

    protected JsonDict default_data() throws JSONException {
        JsonDict m = new JsonDict();
        m.put("localtime", thetime());
        m.put("command", type());
        m.put("message", message);
        m.put("free_space", free_space());
        return m;
    }

    protected void set_queued(boolean queued) {
        this.queued = queued;
    }

    protected void set_sequence(Integer sequence) {
        if (sequence == null) {
            this.sequence = 0;
        } else {
            this.sequence = sequence;
        }
    }

    protected void set_message(String message) {
        if (message == null) {
            this.message = "";
        } else {
            this.message = message;
        }
    }

    protected void set_message(KWargs message) {
        if (message == null) {
            this.message = "";
        } else {
            try {
                this.message = new JsonDict(message);
            } catch (JSONException e) {
                this.message = "";
            }
        }
    }

    protected void set_json(KWargs json) {
        this.json = json;
    }

    protected KWargs track_message(Item track) {
        KWargs a = new KWargs();
        a.put("id", track.id());
        a.put("filename", track.filename());
        return a;
    }
}
