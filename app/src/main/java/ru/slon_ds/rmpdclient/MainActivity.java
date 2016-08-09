package ru.slon_ds.rmpdclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_main);

        VideoView video_view = (VideoView) findViewById(R.id.videoView);
        /*String address = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri uri = Uri.parse(address);
        video_view.setVideoPath("/mnt/sdcard/external_sdcard/elixir_for_rubyist.mp4");
        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Logger.error(this, "video error");
                return false;
            }
        });
        video_view.setZOrderOnTop(true);
        video_view.requestFocus();
        video_view.start();*/

        new Main(new PlayerWrapper(video_view)).start();
        //Logger.error(this, Files.mediafiles_path());
    }
}
