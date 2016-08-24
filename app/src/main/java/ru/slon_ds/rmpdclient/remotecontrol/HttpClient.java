package ru.slon_ds.rmpdclient.remotecontrol;

import android.util.Base64;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.utils.Files;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;

public class HttpClient {
    private URL server_url_base;
    private String login;
    private String password;

    public HttpClient(String server_url, String login, String password) throws MalformedURLException {
        this.server_url_base = new URL(server_url);
        this.login = login;
        this.password = password;
    }

    public HttpData send(JsonDict msg, Integer seq) throws IOException, JSONException, HttpError {
        HttpData result = null;
        HttpURLConnection connection = (HttpURLConnection) status_url().openConnection();
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
                    return;
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

    public void download_file(URL url, String localpath, int retries) throws IllegalArgumentException, IOException {
        if (retries <= 0) {
            throw new IllegalArgumentException("retries is <= 0");
        }
        IOException last_error = new IOException();
        for (int i = 0; i < retries; i++) {
            try {
                download_file(url, localpath);
                return;
            } catch (IOException e) {
                last_error = e;
                Logger.exception(this, "retrying download", e);
            }
        }
        throw last_error;
    }

    public void submit_multipart_form(URL url, KWargs data) throws IOException, HttpError {
        DataOutputStream os = null;
        final String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(60_000);
            connection.setReadTimeout(60_000);
            if (login != null && password != null) {
                connection.setRequestProperty("Authorization", basic_auth_header());
            }
            connection.setRequestProperty("User-Agent", AndroidApplication.user_agent());
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            os = new DataOutputStream(connection.getOutputStream());

            for(KWargs.Entry<String, Object> i : data.entrySet()) {
                String key = i.getKey();
                Object value = i.getValue();
                os.writeBytes("--" + boundary + "\r\n");
                if (value instanceof File) {
                    File file = (File) value;
                    os.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + file.getAbsolutePath() +"\"" + "\r\n");
                    os.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n");
                    os.writeBytes("Content-Transfer-Encoding: binary\r\n");
                    os.writeBytes("\r\n");
                    FileInputStream fi = new FileInputStream(file);
                    int bytes_available = fi.available();
                    final int MAX_BUFFER_SIZE = 1_048_576;
                    int buffer_size = Math.min(bytes_available, MAX_BUFFER_SIZE);
                    byte buffer[] = new byte[buffer_size];

                    int bytes_read = fi.read(buffer, 0, buffer_size);
                    while (bytes_read > 0) {
                        os.write(buffer, 0, buffer_size);
                        bytes_available = fi.available();
                        buffer_size = Math.min(bytes_available, MAX_BUFFER_SIZE);
                        bytes_read = fi.read(buffer, 0, buffer_size);
                    }
                    os.writeBytes("\r\n");
                    os.flush();
                    fi.close();
                } else if (value instanceof String) {
                    os.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
                    os.writeBytes("Content-Type: text/plain\r\n");
                    os.writeBytes("\r\n");
                    os.writeBytes((String) value);
                    os.writeBytes("\r\n");
                }
            }
            os.writeBytes("--" + boundary + "--\r\n");
            os.flush();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new HttpError("http request failed, status code: " + connection.getResponseCode());
            }
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public URL status_url() {
        try {
            return new URL(server_url_base, "/deviceapi/status");
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public URL service_upload_url() {
        try {
            return new URL(server_url_base, "/deviceapi/service_upload");
        } catch (MalformedURLException e) {
            return null;
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
