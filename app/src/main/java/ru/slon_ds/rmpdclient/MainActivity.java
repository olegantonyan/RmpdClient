package ru.slon_ds.rmpdclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerWrapper;
import ru.slon_ds.rmpdclient.utils.Config;
import ru.slon_ds.rmpdclient.utils.Logger;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private Main main = null;
    private VideoView video_view = null;
    private GestureDetectorCompat gesture_detector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        setContentView(R.layout.activity_main);

        gesture_detector = new GestureDetectorCompat(this, this);

        video_view = (VideoView) findViewById(R.id.videoView);
        video_view.setZOrderOnTop(true);
        video_view.requestFocus();
        video_view.setOnTouchListener(this);

        Config.instance().load_defaults();

        if (Config.instance().first_run()) {
            launch_settings_activity();
        }
    }

    private void launch_settings_activity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (main != null && main.isAlive()) {
            main.interrupt();
        }

        Logger.debug(this, "starting main");
        main = new Main(new PlayerWrapper(video_view));
        main.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (main != null) {
            Logger.debug(this, "stopping main");
            main.interrupt();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gesture_detector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //Logger.info(this, "onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //Logger.info(this, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        //Logger.info(this, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent start, MotionEvent current, float x, float y) {
        //Logger.info(this, String.format("onScroll (%f %f) -> (%f %f)", start.getX(), start.getY(), current.getX(), current.getY()));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //Logger.info(this, "onLongPress " + String.valueOf(motionEvent.getX()));
        launch_settings_activity();
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Logger.info(this, "onFling");
        return false;
    }
}
