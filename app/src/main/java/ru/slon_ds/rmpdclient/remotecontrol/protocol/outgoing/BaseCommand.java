package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import android.text.format.Time;

import org.json.JSONException;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.Control;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;
import ru.slon_ds.rmpdclient.utils.Support;

public class BaseCommand {
    private ControlWrapper control_wrapper = null;
    private boolean queued = true;
    private Integer sequence = 0;
    private String message = null;
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
        Time tm = new Time();
        tm.setToNow();
        return tm.format("%Y-%m-%dT%H:%M:%S%z");
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

    protected void set_json(KWargs json) {
        this.json = json;
    }
}
