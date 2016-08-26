package ru.slon_ds.rmpdclient.mediaplayer.player;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class ImagePlayer extends TimerTask implements Handler.Callback {
    private ImageView image_view = null;
    private Timer timer = null;
    private int seek_position = 0;
    private int duration = 0;
    private boolean is_started = false;
    private Handler handler = null;
    private Callback callback = null;

    public interface Callback {
        void on_image_player_finished();
        void on_image_player_error(String error_message);
    }

    public ImagePlayer(ImageView image_view) {
        this.image_view = image_view;
        this.timer = new Timer();
        this.handler = new Handler(this);
    }

    public void set_callback(Callback cb) {
        this.callback = cb;
    }

    public void start(String image_filepath, Integer duration_ms) {
        if (is_playing()) {
            stop();
        }
        image_view.setImageDrawable(Drawable.createFromPath(image_filepath));
        duration = duration_ms / 1000;
        start_seek_timer();
    }

    public void stop() {
        timer.cancel();
        image_view.setImageResource(0);
        is_started = false;
    }

    public synchronized Integer position_sec() {
        return seek_position;
    }

    public boolean is_playing() {
        return is_started;
    }

    public synchronized void seek_to(Integer ms) {
        if (ms >= 0) {
            seek_position = ms;
        }
    }

    public Integer duration_sec() {
        return duration;
    }

    private synchronized void start_seek_timer() {
        seek_position = 0;
        timer.scheduleAtFixedRate(this, 1000, 1000);
        is_started = true;
    }

    @Override
    public void run() {
        int current_seek_position;
        synchronized (this) {
            ++seek_position;
            current_seek_position = seek_position;
        }
        if (current_seek_position >= duration) {
            execute("stop");
            if (callback != null) {
                callback.on_image_player_finished();
            }
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        Bundle bundle = message.getData();
        String command = bundle.getString("command");
        if (command != null && command.equals("stop")) {
            stop();
        }
        return true;
    }

    private void execute(String command) {
        Bundle bundle = new Bundle();
        bundle.putString("command", command);
        Message msg = new Message();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
