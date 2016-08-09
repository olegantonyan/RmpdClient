package ru.slon_ds.rmpdclient.mediaplayer.player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.VideoView;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ru.slon_ds.rmpdclient.mediaplayer.player.commands.BaseCommand;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;
import ru.slon_ds.rmpdclient.utils.Support;

public class PlayerWrapper extends Handler implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{
    private VideoView video_view = null;
    private Callback callback = null;
    private BlockingQueue<KWargs> queue = null;

    interface Callback {
        void onfinished();
        void onerror();
    }

    public PlayerWrapper(VideoView vv) {
        super(Looper.getMainLooper());
        video_view = vv;
        video_view.setOnErrorListener(this);
        video_view.setOnCompletionListener(this);
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
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Logger.error(this, String.format(Locale.US, "playback error (%d, %d)", i, i1));
        if (callback != null) {
            callback.onerror();
        }
        return false;
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
            BaseCommand command_object = (BaseCommand) Class.forName(command_class_name).getConstructor(VideoView.class, KWargs.class)
                    .newInstance(video_view, options);
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
}
