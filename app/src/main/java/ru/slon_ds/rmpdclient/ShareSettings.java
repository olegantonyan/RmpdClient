package ru.slon_ds.rmpdclient;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class ShareSettings {
    private Thread thread = null;

    public ShareSettings() {
        thread = new Thread(new ServerThread());
        thread.setDaemon(true);
        thread.setName("shared_settings_server");
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    private String settings() {
        JsonDict result = new JsonDict();
        try {
            result.put("base_storage_path", Files.base_storage_path());
            result.put("temp_path", Files.temp_path());
            result.put("logs_path", Files.logs_path());
        } catch (JSONException e) {
            Logger.exception(this, "error creating share settings", e);
        }
        return result.toString();
    }

    class ServerThread implements Runnable {
        @Override
        public void run() {
            ServerSocket server_socket = null;
            try {
                server_socket = new ServerSocket(12223);
            } catch (IOException e) {
                Logger.exception(this, "error creating server socket", e);
                return;
            }

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = null;
                BufferedWriter out = null;
                try {
                    socket = server_socket.accept();
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(settings());
                } catch (IOException e) {
                    Logger.exception(this, "error sending settings data to client", e);
                } finally {
                    try {
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        Logger.exception(this, "error closing client socket", e);
                    }
                }
            }
        }
    }
}
