package ru.slon_ds.rmpdclient.mediaplayer.playlist;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ru.slon_ds.rmpdclient.common.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.common.Logger;

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

    public JsonDict load() throws IOException, JSONException {
        JsonDict result = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath()));
            String line;
            String contents = "";
            while ((line = br.readLine()) != null) {
                contents += line;
            }
            result = new JsonDict(contents.replace("\n", "").replace("\r", ""));
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return result;
    }

    public boolean file_exists() {
        return new File(filepath()).exists();
    }

    public ArrayList<String> list_all_files() {
        ArrayList<String> files = new ArrayList<>();
        try {
            ArrayList<JsonDict> items = load().fetch_array_of_objects("items");
            for (JsonDict m : items) {
                files.add(m.fetch("filename", String.class));
            }
        } catch (Exception e) {
            Logger.exception(this, "error extracting items filenames", e);
        }
        return files;
    }
}
