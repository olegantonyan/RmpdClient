package ru.slon_ds.rmpdclient.remotecontrol.protocol;

import java.lang.reflect.InvocationTargetException;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing.BaseCommand;
import ru.slon_ds.rmpdclient.utils.Support;

public class Sender {
    private BaseCommand command_object = null;

    public Sender(ControlWrapper control_wrapper, String command_name) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String command_class_name = getClass().getPackage().getName() + ".outgoing." + Support.underscore_to_camelcase(command_name);
        this.command_object = (BaseCommand) Class.forName(command_class_name).getConstructor(ControlWrapper.class).newInstance(control_wrapper);
    }

    public boolean call(Object options) {
        return command_object.call(options);
    }
}
