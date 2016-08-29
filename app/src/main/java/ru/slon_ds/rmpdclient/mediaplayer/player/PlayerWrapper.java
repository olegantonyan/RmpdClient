package ru.slon_ds.rmpdclient.mediaplayer.player;

public class PlayerWrapper implements PlayerInterface {
    private VideoAudioPlayer videoaudio_player = null;
    private ImagePlayer image_player = null;
    private PlayerInterface active_player = new NullPlayer();

    public PlayerWrapper(VideoAudioPlayer vap, ImagePlayer ip) {
        image_player = ip;
        videoaudio_player = vap;
    }

    public PlayerInterface active_player() {
        return active_player;
    }

    @Override
    public void play(String filepath, String mime_type, int duration_ms) {
        if (mime_type.startsWith("image/")) {
            image_player.play(filepath, mime_type, duration_ms);
            set_active_player(image_player);
            image_player.set_visible(true);
            videoaudio_player.set_visible(false);
        } else {
            videoaudio_player.play(filepath, mime_type, duration_ms);
            set_active_player(videoaudio_player);
            videoaudio_player.set_visible(true);
            image_player.set_visible(false);
        }
    }

    @Override
    public void stop() {
        active_player().stop();
    }

    @Override
    public void seek_to(int position_ms) {
        active_player().seek_to(position_ms);
    }

    @Override
    public boolean is_playing() {
        return active_player().is_playing();
    }

    @Override
    public int percent_pos() {
        return active_player().percent_pos();
    }

    @Override
    public int time_ms_pos() {
        return active_player().time_ms_pos();
    }

    @Override
    public int duration_ms() {
        return active_player().duration_ms();
    }

    @Override
    public void set_visible(boolean visible) {
        active_player().set_visible(visible);
    }

    private void set_active_player(PlayerInterface p) {
        active_player = p;
    }

    class NullPlayer implements PlayerInterface {
        @Override
        public void play(String filepath, String mime_type, int duration_ms) {
        }

        @Override
        public void stop() {
        }

        @Override
        public void seek_to(int position_ms) {
        }

        @Override
        public boolean is_playing() {
            return false;
        }

        @Override
        public int percent_pos() {
            return 0;
        }

        @Override
        public int time_ms_pos() {
            return 0;
        }

        @Override
        public int duration_ms() {
            return 0;
        }

        @Override
        public void set_visible(boolean visible) {
        }
    }
}
