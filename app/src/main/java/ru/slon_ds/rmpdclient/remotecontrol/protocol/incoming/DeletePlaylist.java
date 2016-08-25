package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import java.io.File;

import ru.slon_ds.rmpdclient.mediaplayer.PlayerController;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.remotecontrol.playlist.DownloadWorkerManager;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class DeletePlaylist extends BaseCommand {
    public DeletePlaylist(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        DownloadWorkerManager.instance().stop();
        boolean ok = delete_files();
        if (PlayerController.instance() != null) {
            PlayerController.instance().start_playlist();
        }
        return ack(ok);
    }

    private boolean ack(boolean ok) {
        KWargs options = new KWargs();
        options.put("message", ok ? "playlist deleted successfully" : "error deleting playlist");
        options.put("sequence", get_sequence());
        return sender("ack_" + (ok ? "ok" : "fail")).call(options);
    }

    private boolean delete_files() {
        boolean result = true;
        File dir = new File(Files.mediafiles_path());
        File files_in_dir[] = dir.listFiles();
        for (File current : files_in_dir) {
            if (!current.delete()) {
                result = false;
            }
        }
        return result;
    }
}
