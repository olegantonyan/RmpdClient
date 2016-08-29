package ru.slon_ds.rmpdclient.mediaplayer.player;

public interface PlayerInterface {
    void play(String filepath, String mime_type, int duration_ms);
    void stop();
    void seek_to(int position_ms);
    boolean is_playing();
    int percent_pos();
    int time_ms_pos();
    int duration_ms();
    void set_visible(boolean visible);

    interface Callback {
        void on_player_error(String text);
        void on_player_finished();
    }
}
