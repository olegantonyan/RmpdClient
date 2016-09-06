package ru.slon_ds.rmpdclient.common;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import ru.slon_ds.rmpdclient.utils.JsonDict;

public class ShareSettings {
    private Thread thread = null;
    ServerSocket server_socket = null;

    public ShareSettings() {
        thread = new Thread(new ServerThread());
        thread.setDaemon(true);
        thread.setName("shared_settings_server");
    }

    public boolean start() {
        boolean result;
        try {
            server_socket = new ServerSocket(12223);
            result = true;
        } catch (IOException e) {
            Logger.exception(this, "error creating server socket", e);
            result = false;
        }
        thread.start();
        return result;
    }

    public void stop() {
        if (server_socket != null) {
            try {
                server_socket.close();
            } catch (IOException e) {
                Logger.exception(this, "error closing server socket", e);
            }
        }
        thread.interrupt();
    }

    private String settings() {
        JsonDict result = new JsonDict();
        try {
            result.put("base_storage_path", Files.base_storage_path());
            result.put("temp_path", Files.temp_path());
            result.put("logs_path", Files.logs_path());
            result.put("software_update_filepath", new SelfUpdate().apk_filepath());
        } catch (JSONException e) {
            Logger.exception(this, "error creating share settings", e);
        }
        return result.toString();
    }

    class ServerThread implements Runnable {
        @Override
        public void run() {
            while (server_socket != null && !Thread.currentThread().isInterrupted()) {
                Socket socket = null;
                BufferedWriter out = null;
                try {
                    socket = server_socket.accept();
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(settings());
                } catch (SocketException e) {
                    break;
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
            Logger.warning(this, "finishing share settings thread");
        }
    }
}
