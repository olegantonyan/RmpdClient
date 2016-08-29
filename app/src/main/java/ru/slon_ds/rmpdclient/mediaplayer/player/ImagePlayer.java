package ru.slon_ds.rmpdclient.mediaplayer.player;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class ImagePlayer implements Handler.Callback, PlayerInterface {
    private ImageView image_view = null;
    private Timer timer = null;
    private TimerTask timer_task = null;
    private int seek_position = 0;
    private int duration = 0;
    private boolean is_started = false;
    private Handler handler = null;
    private PlayerInterface.Callback callback = null;

    public ImagePlayer(ImageView iv, PlayerInterface.Callback cb) {
        image_view = iv;
        callback = cb;
        handler = new Handler(this);
    }

    @Override
    public void play(String filepath, String mime_type, int duration_ms) {
        stop();
        if (!mime_type.startsWith("image/")) {
            if (callback != null) {
                callback.on_player_error("unsupported mime type " + mime_type);
            }
            return;
        }
        image_view.setImageDrawable(Drawable.createFromPath(filepath));
        duration = duration_ms / 1000;
        start_seek_timer();
    }

    @Override
    public void stop() {
        stop_seek_timer();
        image_view.setImageResource(0);
        is_started = false;
    }

    @Override
    public boolean is_playing() {
        return is_started;
    }

    @Override
    public int percent_pos() {
        int percent = 0;
        if (is_playing()) {
            int position = time_ms_pos();
            int duration = duration_ms();
            if (duration != 0 && position >= 0) {
                percent = position * 100 / duration;
            }
        }
        return percent;
    }

    @Override
    public int time_ms_pos() {
        return seek_position * 1000;
    }

    @Override
    public synchronized void seek_to(int ms) {
        if (ms >= 0) {
            seek_position = ms / 1000;
        }
    }

    @Override
    public int duration_ms() {
        return duration * 1000;
    }

    private synchronized void start_seek_timer() {
        seek_position = 0;
        timer = new Timer();
        timer_task = new TimerTask() {
            @Override
            public void run() {
                int current_seek_position;
                synchronized (ImagePlayer.this) {
                    seek_position++;
                    current_seek_position = seek_position;
                }
                if (current_seek_position >= duration) {
                    execute("stop");
                    if (callback != null) {
                        callback.on_player_finished();
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(timer_task, 1000, 1000);
        is_started = true;
    }

    private synchronized void stop_seek_timer() {
        try {
            timer.cancel();
            timer.purge();
            timer_task.cancel();
        } catch (Exception e) {
            // don't care
        }
        timer = null;
        timer_task = null;
    }

    @Override
    public void set_visible(boolean visible) {
        image_view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
