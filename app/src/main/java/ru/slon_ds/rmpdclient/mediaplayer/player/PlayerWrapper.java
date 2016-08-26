package ru.slon_ds.rmpdclient.mediaplayer.player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.VideoView;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ru.slon_ds.rmpdclient.mediaplayer.player.commands.BaseCommand;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;
import ru.slon_ds.rmpdclient.utils.Support;

public class PlayerWrapper extends Handler implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, ImagePlayer.Callback {
    private VideoView video_view = null;
    private ImagePlayer image_player = null;
    private Callback callback = null;
    private BlockingQueue<KWargs> queue = null;

    public interface Callback {
        void onfinished();
        void onerror(String message);
    }

    public PlayerWrapper(VideoView vv, ImageView iv) {
        super(Looper.getMainLooper());
        video_view = vv;
        video_view.setOnErrorListener(this);
        video_view.setOnCompletionListener(this);
        video_view.setOnPreparedListener(this);
        image_player = new ImagePlayer(iv);
        image_player.set_callback(this);
        queue = new LinkedBlockingQueue<>();
    }

    public KWargs execute(String command, KWargs options) throws InterruptedException {
        Bundle bundle = new Bundle();
        bundle.putString("command", command);
        bundle.putSerializable("options", options);
        Message msg = new Message();
        msg.setData(bundle);
        sendMessage(msg);
        return queue.take();
    }

    public KWargs execute(String command) throws InterruptedException {
        return execute(command, new KWargs());
    }

    public void set_callback(Callback cb) {
        callback = cb;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Logger.debug(this, "finished track");
        if (callback != null) {
            callback.onfinished();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        String message = error_explanation(what, extra);
        Logger.error(this, message);
        if (callback != null) {
            callback.onerror(message);
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Logger.debug(this, "media player is ready to go");
    }

    @Override
    public void on_image_player_finished() {
        Logger.debug(this, "finished track (image)");
        if (callback != null) {
            callback.onfinished();
        }
    }

    @Override
    public void on_image_player_error(String error_message) {
        Logger.error(this, error_message);
        if (callback != null) {
            callback.onerror(error_message);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle bundle = msg.getData();
        String command = bundle.getString("command");
        KWargs options = (KWargs) bundle.getSerializable("options");
        String command_class_name = getClass().getPackage().getName() + ".commands." + Support.underscore_to_camelcase(command);
        KWargs result = new KWargs();
        try {
            BaseCommand command_object = (BaseCommand) Class.forName(command_class_name).getConstructor(VideoView.class, ImagePlayer.class, KWargs.class)
                    .newInstance(video_view, image_player, options);
            result = command_object.call();
        } catch (Exception e) {
            // gotta catch em all!
            Logger.exception(this, "error executing command '" + command + "'" ,e);
        } finally {
            result.put("original_command", command);
            result.put("original_options", options);
            try {
                queue.put(result);
            } catch (InterruptedException e) {
                Logger.exception(this, "error putting result into queue", e);
            }
        }
    }

    private String error_explanation(int what, int extra) {
        String what_string;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                what_string = "MEDIA_ERROR_UNKNOWN";
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                what_string = "MEDIA_ERROR_SERVER_DIED";
                break;
            default:
                what_string = String.valueOf(what);
                break;

        }
        String extra_string;
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                extra_string = "MEDIA_ERROR_IO";
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                extra_string = "MEDIA_ERROR_MALFORMED";
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                extra_string = "MEDIA_ERROR_UNSUPPORTED";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                extra_string = "MEDIA_ERROR_TIMED_OUT";
                break;
            default:
                extra_string = String.valueOf(extra);
        }
        return String.format(Locale.US, "(%s, %s)", what_string, extra_string);
    }
}
