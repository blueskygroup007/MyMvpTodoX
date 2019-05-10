package com.bluesky.alarmclock;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ClockAlarmActivity extends AppCompatActivity {

    public static final String TAG = ClockAlarmActivity.class.getSimpleName();
    private Intent mService;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        String message = "点击确定关闭";
        int flag = 0;
        setVibrator(true);
        showDialogInBroadcastReceiver(message, flag);
        startAccService(this);
    }

    private void setVibrator(boolean onOrOff) {
        Vibrator vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            return;
        }

        if (onOrOff) {
            vibrator.vibrate(500);
        } else {
            vibrator.cancel();
        }

    }

    private void showDialogInBroadcastReceiver(String message, int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("闹钟");
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setVibrator(false);
                dialog.dismiss();
                stopService(mService);

            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

    private void startAccService(Context context) {
        mService = new Intent(context, AccelerationService.class);
        mService.putExtra("messenger", new Messenger(mHandler));
        startService(mService);
        Log.e(TAG, "启动了sensor监听....");

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "收到了service发来的消息.....");
                    stopService(mService);
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    break;
            }
        }
    };
}
