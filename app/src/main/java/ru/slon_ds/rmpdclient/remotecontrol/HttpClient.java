package ru.slon_ds.rmpdclient.remotecontrol;

import android.util.Base64;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.Logger;

public class HttpClient {
    private URL server_url;
    private String login;
    private String password;

    public HttpClient(String server_url, String login, String password) throws MalformedURLException {
        this.server_url = new URL(new URL(server_url), "/deviceapi/status");
        this.login = login;
        this.password = password;
    }

    public HttpData send(JsonDict msg, Integer seq) throws IOException, JSONException, HttpError {
        HttpData result = null;
        HttpURLConnection connection = (HttpURLConnection) server_url.openConnection();
        connection.setConnectTimeout(30_000);
        connection.setReadTimeout(60_000);
        connection.setRequestProperty("X-Sequence-Number", seq.toString());
        connection.setRequestProperty("Authorization", basic_auth_header());
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", AndroidApplication.user_agent());
        OutputStream os = connection.getOutputStream();
        os.write(msg.toString().getBytes());
        os.flush();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new HttpError("http request failed, status code: " + connection.getResponseCode());
        }
        try {
            result = new HttpData();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String json_string = input_stream_to_string(in);
            result.data = new JsonDict(json_string);
            result.sequence_number = connection.getHeaderFieldInt("X-Sequence-Number", 0);
        } finally {
            connection.disconnect();
        }
        return result;
    }

    public void download_file(URL url, String localpath) throws IOException {
        FileOutputStream os = null;
        File tempfile = File.createTempFile(Files.file_basename(localpath), null, new File(Files.temp_path()));
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(60_000);
            connection.setReadTimeout(200_000);
            connection.setRequestProperty("User-Agent", AndroidApplication.user_agent());
            connection.setRequestProperty("Accept","*/*");
            os = new FileOutputStream(tempfile);
            InputStream is = connection.getInputStream();
            byte[] buffer = new byte[2048];
            int bufferLength;
            while ((bufferLength = is.read(buffer)) > 0) {
                if (Thread.currentThread().isInterrupted()) {
                    Logger.warning(this, "download interrupted");
                    break;
                }
                os.write(buffer, 0, bufferLength);
            }
            os.flush();
            Files.copy_file(tempfile.getAbsolutePath(), localpath);
        } finally {
            if (os != null) {
                os.close();
            }
            if (tempfile.exists()) {
                tempfile.delete();
            }
        }
    }

    private static String input_stream_to_string(InputStream is) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return sb.toString();
    }

    private String basic_auth_header() {
        String credentials = login + ":" + password;
        return "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
    }

    public class HttpData {
        public JsonDict data;
        public Integer sequence_number;
    }

    public class HttpError extends RuntimeException {
        public HttpError(String msg) {
            super(msg);
        }
    }
}
