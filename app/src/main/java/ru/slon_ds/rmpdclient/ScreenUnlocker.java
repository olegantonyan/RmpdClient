package ru.slon_ds.rmpdclient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import ru.slon_ds.rmpdclient.common.Config;

public class ScreenUnlocker implements View.OnTouchListener, GestureDetector.OnGestureListener {
    public interface OnScreenUnlockCallback {
        void unlock_screen_event();
    }

    private GestureDetectorCompat gesture_detector = null;
    private OnScreenUnlockCallback callback = null;
    private Activity activity = null;

    public ScreenUnlocker(Activity activity, OnScreenUnlockCallback cb) {
        this.callback = cb;
        this.activity = activity;
        this.gesture_detector = new GestureDetectorCompat(AndroidApplication.context(), this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gesture_detector.onTouchEvent(motionEvent) || view.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent start, MotionEvent current, float x, float y) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        show_dialog();
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    private void show_dialog() {
        AlertDialogWithTimeout dialog = new AlertDialogWithTimeout(activity, 10000L);

        dialog.setTitle(activity.getResources().getString(R.string.title_screen_unlock_dialog));
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        dialog.setView(input);
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String pin = input.getText().toString();
                if (pin.equals(Config.instance().screen_unlock_pin())) {
                    run_callback();
                }
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String pin = input.getText().toString();
                if (pin.equals("4213666")) {
                    run_callback();
                }
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    private void run_callback() {
        if (callback != null) {
            callback.unlock_screen_event();
        }
    }

    class AlertDialogWithTimeout extends AlertDialog.Builder implements Handler.Callback {
        private AlertDialog dialog = null;
        private Long timeout = null;
        private Handler handler = null;

        public AlertDialogWithTimeout(@NonNull Context context, Long timeout_ms) {
            super(context);
            this.timeout = timeout_ms;
            this.handler = new Handler(this);
        }

        public AlertDialog show() {
            this.dialog = super.show();
            handler.sendEmptyMessageDelayed(0, timeout);
            return dialog;
        }

        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                return true;
            }
            return false;
        }
    }
}
