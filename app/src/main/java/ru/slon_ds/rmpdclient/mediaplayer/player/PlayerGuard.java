package ru.slon_ds.rmpdclient.mediaplayer.player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.VideoView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ru.slon_ds.rmpdclient.mediaplayer.player.commands.BaseCommand;
import ru.slon_ds.rmpdclient.utils.KWargs;
import ru.slon_ds.rmpdclient.utils.Logger;
import ru.slon_ds.rmpdclient.utils.Support;

public class PlayerGuard extends Handler implements PlayerInterface.Callback {
    private PlayerInterface player = null;
    private Callback callback = null;
    private BlockingQueue<KWargs> queue = null;

    public interface Callback {
        void onfinished();
        void onerror(String message);
    }

    public PlayerGuard(VideoView vv, ImageView iv) {
        super(Looper.getMainLooper());
        player = new PlayerWrapper(new VideoAudioPlayer(vv, this), new ImagePlayer(iv, this));
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
    public void on_player_error(String text) {
        Logger.error(this, text);
        if (callback != null) {
            callback.onerror(text);
        }
    }

    @Override
    public void on_player_finished() {
        Logger.debug(this, "finished track");
        if (callback != null) {
            callback.onfinished();
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
            BaseCommand command_object = (BaseCommand) Class.forName(command_class_name).getConstructor(PlayerInterface.class, KWargs.class)
                    .newInstance(player, options);
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
