package ru.slon_ds.rmpdclient.remotecontrol.protocol;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming.BaseCommand;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Support;

public class Receiver {
    private BaseCommand command_object = null;

    public Receiver(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) throws JSONException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String command_name = data.getString("command");
        String command_class_name = getClass().getPackage().getName() + ".incoming." + Support.underscore_to_camelcase(command_name);
        command_object = (BaseCommand)
                Class.forName(command_class_name).getConstructor(ControlWrapper.class, JsonDict.class, Integer.class)
                        .newInstance(control_wrapper, data, sequence_number);
    }

    public boolean call() {
        return command_object.call();
    }
}
