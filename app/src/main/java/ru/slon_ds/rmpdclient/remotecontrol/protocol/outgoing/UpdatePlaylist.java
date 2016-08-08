package ru.slon_ds.rmpdclient.remotecontrol.protocol.outgoing;

import java.util.ArrayList;
import java.util.List;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class UpdatePlaylist extends BaseCommand {
    public UpdatePlaylist(ControlWrapper control_wrapper) {
        super(control_wrapper);
    }

    @Override
    public boolean call(KWargs options) {
        List<String> files;
        if (options != null) {
            files = options.fetch("files", ArrayList.class, new ArrayList());
            if (files.size() > 20) {
                files = files.subList(0, 20);
            }
            String message = "";
            for (String s : files) {
                message += (s + ", ");
            }
            message = message.substring(0, message.length() - 2);
            set_message(message);
        }
        return super.call(options);
    }
}
