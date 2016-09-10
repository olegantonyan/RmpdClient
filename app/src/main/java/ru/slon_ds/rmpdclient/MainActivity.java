package ru.slon_ds.rmpdclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import ru.slon_ds.rmpdclient.common.Config;
import ru.slon_ds.rmpdclient.common.Logger;
import ru.slon_ds.rmpdclient.mediaplayer.player.PlayerGuard;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private Main main = null;
    private VideoView video_view = null;
    private ImageView image_view = null;
    private GestureDetectorCompat gesture_detector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup_ui();
        setup_controls();

        if (Config.instance().first_run()) {
            if (!Config.instance().load_preconfigured()) {
                launch_settings_activity();
            }
        }
    }

    private void setup_controls() {
        gesture_detector = new GestureDetectorCompat(this, this);

        video_view = (VideoView) findViewById(R.id.videoView);
        video_view.setZOrderOnTop(true);
        video_view.requestFocus();
        video_view.setOnTouchListener(this);

        image_view = (ImageView) findViewById(R.id.image_view);
        image_view.requestFocus();
        image_view.setOnTouchListener(this);
    }

    private void setup_ui() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        setContentView(R.layout.activity_main);
    }

    private void configure_ui() {
        Window window = getWindow();
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = Config.instance().brightness();
        window.setAttributes(layout);
    }

    private void launch_settings_activity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private synchronized void stop_main() {
        if (main != null) {
            main.quit();
        }
    }

    private synchronized void start_main() {
        if (main != null && main.isAlive()) {
            stop_main();
        }

        Logger.debug(this, "starting main");
        main = new Main(new PlayerGuard(video_view, image_view));
        main.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        start_main();
        configure_ui();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop_main();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Intent close_dialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(close_dialog);
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
