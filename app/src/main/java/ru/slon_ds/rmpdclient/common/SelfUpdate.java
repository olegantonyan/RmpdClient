package ru.slon_ds.rmpdclient.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.remotecontrol.ProtocolDispatcher;
import ru.slon_ds.rmpdclient.utils.KWargs;

public class SelfUpdate {
    private String _apk_filepath = null;
    private String sequnce_filepath = null;

    public SelfUpdate() {
        _apk_filepath = Files.temp_path() + "/update.apk";
        sequnce_filepath = Files.temp_path() + "/last_update_sequence_number.txt";
    }

    public String apk_filepath() {
        return _apk_filepath;
    }

    public boolean run(Integer sequence_number) {
        if (!apk_update_exists()) {
            Logger.error(this, "update akp file does not exists " + apk_filepath());
            return false;
        }
        Logger.info(this, "about to run self-update, there is no way back");
        try {
            Thread.sleep(1000); // last breath for other threads
        } catch (InterruptedException e) {
            // don't care
        }
        write_sequence_file(sequence_number);
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "su", "-c", "pm", "install", "-r", "-d", apk_filepath(), "&&", "su", "-c", "reboot"});
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void verify() {
        try {
            Integer seq = read_sequence_file();
            if (seq < 0) {
                Logger.info(this, "there is no self-update in progress, nothing to do");
                return;
            }
            Logger.info(this, "self-update probably succeeded, sequence: " +seq.toString());
            ack(seq);
        } finally {
            if (apk_update_exists()) {
                if (!new File(apk_filepath()).delete()) {
                    Logger.error(this, "error deleting update apk file " + apk_filepath());
                }
            }
            File seq = new File(sequnce_filepath);
            if (seq.exists()) {
                if (!seq.delete()) {
                    Logger.error(this, "error deleting sequence file " + sequnce_filepath);
                }
            }
        }
    }

    private void write_sequence_file(Integer sequence) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(sequnce_filepath, "UTF-8");
            writer.println(sequence);
        } catch (IOException e) {
            Logger.exception(this, "error writing sequence number to file " + sequnce_filepath, e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private Integer read_sequence_file() {
        if (!apk_update_exists()) {
            return -1;
        }

        try {
            FileReader reader = new FileReader(sequnce_filepath);
            BufferedReader br = new BufferedReader(reader);
            String s = br.readLine().trim();
            reader.close();
            br.close();
            return Integer.valueOf(s);
        } catch (Exception e) {
            Logger.exception(this, "error reading sequence number file " + sequnce_filepath, e);
            return -1;
        }
    }

    private boolean apk_update_exists() {
        return new File(apk_filepath()).exists();
    }

    private void ack(Integer seq) {
        KWargs options = new KWargs();
        options.put("sequence", seq);
        options.put("message", "still alive after update, hope it was successful, version: " + AndroidApplication.version());
        ProtocolDispatcher.instance().send("ack_ok", options);
    }
}
