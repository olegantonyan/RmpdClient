package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;

public class PowerOn extends BaseCommand {
    public PowerOn(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    @Override
    public boolean call(Object options) {
      /*  HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("hello", "world");
        HashMap<String, Object> m1 = new HashMap<String, Object>();
        m1.put("recursive", "love");
        HashMap<String, Object> m2 = new HashMap<String, Object>();
        m2.put("deeper", "eeee");
        m1.put("way more", m2);
        m.put("deep", m1);
        set_json(m);*/
        return super.call(options);
    }
}
