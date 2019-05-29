package com.bluesky.alarmclock;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.bluesky.alarmclock.data.Alarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.bluesky.alarmclock.utils.ActivityManagerUtils.isServiceRunning;
import static com.bluesky.alarmclock.utils.AlarmUtils.ALARM_ACTION;


public class MainActivity extends AppCompatActivity implements AlarmMainContract.MainView, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static AlarmMainContract.MainPresenter mPresenter;
    private Dialog mDialog;
    public static final long INTERVAL = 10 * 1000;
    TimePicker mTimePicker;
    Button mBtnTimeAlarm;
    Button mBtnOneMinute;
    static Alarm mAlarm;

    /**
     * Activity是否可见的标志
     */
    private boolean isVisible;

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected被回调了...");

            ForeService.MyBinder mBinder = (ForeService.MyBinder) service;
            mBinder.doSomeThing();
            mBinder.start(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected被回调了...");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "Activity被onCreate了...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimePicker = findViewById(R.id.time_picker);
        mBtnTimeAlarm = findViewById(R.id.btn_setalarm);
        mBtnOneMinute = findViewById(R.id.btn_one_minute);
        //应该是震动期间,才开始启动解除震动的监听
        //(迭代功能)但是在普通倒计时期间,也可以监听跳过本次倒计时?(已完成,进行下一轮计时).
        //(mvp设计)解除震动等等。先设计MVP.
        boolean isRunning = isServiceRunning(this, ForeService.class.getSimpleName());
        Intent intentService = new Intent(this, ForeService.class);
        if (!isRunning) {
            Log.e(TAG, "---------->后台服务ForeService没找到!!!");

            startService(intentService);
        }
        bindService(intentService, mConn, BIND_AUTO_CREATE);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e(TAG, "Activity被onSaveInstanceState了...");

        super.onSaveInstanceState(outState);
    }

    public void onTimeAlarm(View view) {

    }

    public void onOneMinute(View view) {
        mPresenter.startAlarm(null);

    }

    public void onCancel(View view) {
        mPresenter.pauseAlarm(mAlarm);
    }

    public void onContinue(View view) {
//        AlarmUtils.setAlarm(this, AlarmReceiver.class, mRemainMillis);
//        mStartMillis = System.currentTimeMillis() - (INTERVAL - (mRemainMillis));

    }


    private void showAlarmDialog(String message, int flag) {
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
//                    mPresenter.unregistAccListener();
                    mPresenter.stopAlarm(mAlarm);
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
        showAlarmDialog(message, flag);
    }

    @Override
    public void closeAlarmDialog() {
        mDialog.dismiss();
    }

    @Override
    public boolean isActive() {
        return isVisible;
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


    public void onStartForeService(View view) {
        Intent serviceIntent = new Intent(this, ForeService.class);
        startService(serviceIntent);
    }

    public void onStopForeService(View view) {
        Intent serviceIntent = new Intent(this, ForeService.class);
        stopService(serviceIntent);
    }


    /**
     * 此时Activity已经有可能不可见.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Activity被onPause了...");
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Activity被onResume了...");
        isVisible = true;
    }

    /**
     * 此时Activity不可见
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.setAlertView(null);
        unbindService(mConn);
        Log.e(TAG, "Activity被onDestroy了...");
    }

    public void sendBroadcast(View view) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "activity主动发送广播了.....");
                sendBroadcast(new Intent(ALARM_ACTION));
            }
        }, 2000);
    }
}
