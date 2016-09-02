package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.io.File;
import java.io.IOException;

import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.HttpClient;
import ru.slon_ds.rmpdclient.utils.Control;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class UpdateSoftware extends BaseCommand {
    public UpdateSoftware(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        String url = get_data().fetch("distribution_url", String.class);
        try {
            String localpath = Files.temp_path() + "/update.apk";
            File f = new File(localpath);
            if (f.exists()) {
                if (!f.delete()) {
                    throw new IOException("error deleting update apk");
                }
            }
            Logger.info(this, "downloading update apk " + url + " to " + localpath);
            HttpClient.new_default().download_file(Files.full_url_by_relative(url), localpath, 3);
            if (Control.self_update(localpath)) {
                ack(true, "hope that software update will be successful");
                Control.reboot();
                return true;
            } else {
                ack(false, "error installing software update");
            }
            return false;
        } catch (Exception e) {
            Logger.exception(this, "software update error", e);
            return false;
        }
    }
}
