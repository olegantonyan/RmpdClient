package ru.slon_ds.rmpdclient.mediaplayer.player;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.VideoView;

import java.util.Locale;

public class VideoAudioPlayer implements PlayerInterface, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private VideoView video_view = null;
    private PlayerInterface.Callback callback = null;

    public VideoAudioPlayer(VideoView vv, PlayerInterface.Callback cb) {
        video_view = vv;
        callback = cb;
        video_view.setOnCompletionListener(this);
        video_view.setOnErrorListener(this);
        video_view.setOnPreparedListener(this);
    }

    @Override
    public void play(String filepath, String mime_type, int duration_ms) {
        if (!mime_type.startsWith("audio/") && !mime_type.startsWith("video/")) {
            if (callback != null) {
                callback.on_player_error("unsupported file type " + mime_type);
            }
            return;
        }
        video_view.setVideoPath(filepath);
        video_view.start();
    }

    @Override
    public void stop() {
        video_view.stopPlayback();
    }

    @Override
    public void seek_to(int position_ms) {
        video_view.seekTo(position_ms);
    }

    @Override
    public boolean is_playing() {
        return video_view.isPlaying();
    }

    @Override
    public int percent_pos() {
        int percent = 0;
        if (is_playing()) {
            int position = video_view.getCurrentPosition();
            int duration = video_view.getDuration();
            if (duration != 0 && position >= 0) {
                percent = position * 100 / duration;
            }
        }
        return percent;
    }

    @Override
    public int duration_ms() {
        return video_view.getDuration();
    }

    @Override
    public void set_visible(boolean visible) {
        video_view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int time_ms_pos() {
        int ms = 0;
        if (video_view.isPlaying()) {
            ms = video_view.getCurrentPosition();
        }
        return ms;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (callback != null) {
            callback.on_player_finished();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        String message = error_explanation(what, extra);
        if (callback != null) {
            callback.on_player_error(message);
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
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
