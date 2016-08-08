package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;

public class Loader {
    private String _filename = null;

    private String _filepath = null;

    public Loader() {
        this("playlist.json");
    }

    public Loader(String filename) {
        this._filename = filename;
        this._filepath = Files.full_file_localpath(_filename);
    }

    public String filepath() {
        return this._filepath;
    }

    public String filename() {
        return this._filename;
    }

    public void save_to_file(JsonDict data) throws IOException {
        File file = new File(filepath());
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("error creating new file " + file.getAbsolutePath());
            }
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(data.toString());
        bw.close();
    }
}
