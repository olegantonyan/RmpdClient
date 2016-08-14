package ru.slon_ds.rmpdclient;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;
import ru.slon_ds.rmpdclient.utils.Logger;

public class MainActivity extends AppCompatActivity {
    private Main main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        setContentView(R.layout.activity_main);

        VideoView video_view = (VideoView) findViewById(R.id.videoView);
        video_view.setZOrderOnTop(true);
        video_view.requestFocus();

        video_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ActionBar a = getSupportActionBar();
                if (a != null) a.hide();
                android.app.ActionBar b = getActionBar();
                if (b != null) b.hide();
                return true;
            }
        });

        /*ActionBar a = getSupportActionBar();
        if (a != null) a.hide();
        android.app.ActionBar b = getActionBar();
        if (b != null) b.hide();*/


        main = new Main(new PlayerWrapper(video_view));
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (main != null) {
            Logger.debug(this, "starting main");
            main.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (main != null) {
            Logger.debug(this, "stopping main");
            main.interrupt();
        }
    }
}
