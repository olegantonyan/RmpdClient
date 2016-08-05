package ru.slon_ds.rmpdclient.remotecontrol;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {
    private URL server_url;
    private String login;
    private String password;

    public HttpClient(String server_url, String login, String password) throws MalformedURLException {
        this.server_url = new URL(server_url);
        this.login = login;
        this.password = password;
    }

    public boolean send(CharSequence msg, Integer seq) {
        try {
            request(new HttpData());
        } catch (Exception e) {
            Log.e("bb", "assdf");
        }
        return true;
    }

    private HttpData request(HttpData data) throws IOException, JSONException {
        HttpData result = new HttpData();
        HttpURLConnection connection = (HttpURLConnection) server_url.openConnection();
        try {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String json_string = input_stream_to_string(in);
            JSONObject json = new JSONObject(json_string);
            Log.i("BLABLABLA", json_string);
            result.data = json;
            result.sequence_number = 0;
        } finally {
            connection.disconnect();
        }
        return result;
    }

    class HttpData {
        public JSONObject data;
        public Integer sequence_number;
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
}
