package com.bluesky.alarmclock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.bluesky.alarmclock.utils.AlarmUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final long INTERVAL = 10 * 1000;
    TimePicker mTimePicker;
    Button mBtnTimeAlarm;
    Button mBtnOneMinute;

    long mStartMillis = System.currentTimeMillis();
    long mRemainMillis = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimePicker = findViewById(R.id.time_picker);
        mBtnTimeAlarm = findViewById(R.id.btn_setalarm);
        mBtnOneMinute = findViewById(R.id.btn_one_minute);
        //todo 不应该使用广播接收者来获取重力传感器的监听.
        //todo 应该使用SensorManager
        //应该是震动期间,才开始启动解除震动的监听
        //(迭代功能)但是在普通倒计时期间,也可以监听跳过本次倒计时?(已完成,进行下一轮计时).
        //(mvp设计)解除震动等等。先设计MVP.
//        startService();
    }

    public void onTimeAlarm(View view) {

    }

    public void onOneMinute(View view) {
        AlarmUtils.setAlarm(this, AlarmReceiver.class, INTERVAL);
        mStartMillis = System.currentTimeMillis();
    }

    public void onCancel(View view) {
        AlarmUtils.cancelAlarm(this, AlarmReceiver.class);
        mRemainMillis = INTERVAL - (System.currentTimeMillis() - mStartMillis);
        Log.e(TAG, "剩余时间是:" + mRemainMillis / 1000 + "秒");
    }

    public void onContinue(View view) {
        AlarmUtils.setAlarm(this, AlarmReceiver.class, mRemainMillis);
        mStartMillis = System.currentTimeMillis() - (INTERVAL - (mRemainMillis));

    }




}
