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
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.bluesky.alarmclock.data.Alarm;
import com.bluesky.alarmclock.data.AlarmModelImpl;
import com.bluesky.alarmclock.utils.AlarmUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements AlarmMainContract.MainView, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AlarmMainContract.MainPresenter mPresenter;
    private Dialog mDialog;
    public static final long INTERVAL = 10 * 1000;
    TimePicker mTimePicker;
    Button mBtnTimeAlarm;
    Button mBtnOneMinute;
    Alarm mAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimePicker = findViewById(R.id.time_picker);
        mBtnTimeAlarm = findViewById(R.id.btn_setalarm);
        mBtnOneMinute = findViewById(R.id.btn_one_minute);
        //应该是震动期间,才开始启动解除震动的监听
        //(迭代功能)但是在普通倒计时期间,也可以监听跳过本次倒计时?(已完成,进行下一轮计时).
        //(mvp设计)解除震动等等。先设计MVP.
//        startService();

        mPresenter = new AlarmClockPresenter(new AlarmModelImpl(), this);
        mPresenter.start();

    }

    public void onTimeAlarm(View view) {

    }

    public void onOneMinute(View view) {

    }

    public void onCancel(View view) {
        mPresenter.pauseAlarm(mAlarm);
    }

    public void onContinue(View view) {
//        AlarmUtils.setAlarm(this, AlarmReceiver.class, mRemainMillis);
//        mStartMillis = System.currentTimeMillis() - (INTERVAL - (mRemainMillis));

    }


    private void showDialogInBroadcastReceiver(String message, int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("闹钟");
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.stopAlarm(mAlarm);

            }
        });
        mDialog = builder.create();
        mDialog.show();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "收到了service发来的消息.....");
                    //让P去停止计时,关闭对话框,再重启计时
                    mPresenter.stopAlarm(mAlarm);
                    mPresenter.startAlarm(mAlarm);

                    break;
            }
        }
    };


    @Override
    public void showAlarm(Alarm alarm) {
/*        Button btn=new Button(this);
        btn.setText(alarm.getName());
        btn.setOnClickListener(this);*/
        mAlarm = alarm;
        mBtnOneMinute.setText(alarm.getName());
        mBtnOneMinute.setOnClickListener(this);

    }

    @Override
    public void showAlarmDialog() {
        String message = "点击确定关闭";
        int flag = 0;
        showDialogInBroadcastReceiver(message, flag);
    }

    @Override
    public void closeAlarmDialog() {
        mDialog.dismiss();
    }

    @Override
    public void setPresenter(AlarmMainContract.MainPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        mPresenter.startAlarm(mAlarm);
    }

    public Handler getHandler() {
        return mHandler;
    }


}
