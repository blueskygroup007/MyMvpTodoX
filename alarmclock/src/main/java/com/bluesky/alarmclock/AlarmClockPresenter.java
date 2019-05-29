package com.bluesky.alarmclock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.bluesky.alarmclock.data.Alarm;
import com.bluesky.alarmclock.data.AlarmModel;
import com.bluesky.alarmclock.utils.AlarmUtils;

import static android.content.Context.SENSOR_SERVICE;
import static com.bluesky.alarmclock.utils.AlarmUtils.ALARM_ACTION;

/**
 * Presenter 如果持有 Activity 的强引用，在请求结束之前 Activity 被销毁了，那么由于网络请求还没有返回，
 * 导致 Presenter 一直持有 Activity 对象，使得 Activity 无法被回收，此时就容易发生内存泄漏，
 * 解决这个问题需要通过弱引用来解决
 */
public class AlarmClockPresenter implements AlarmMainContract.MainPresenter {

    public static final String TAG = AlarmClockPresenter.class.getSimpleName();

    AlarmModel mModel;
    AlarmMainContract.MainView mView;
    private AlarmMainContract.AlertView mAlertView;
    private Context mContext;
    private Handler mHandler;

    long mStartMillis = System.currentTimeMillis();
    long mRemainMillis = 0;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private static final int RADIO_ACC = 17;//传感器幅度值常量
    private AccelerometerLisenter mLisenter = new AccelerometerLisenter();
    private AlarmClockReceiver mReceiver;

    //todo ---------当前进度----------
    //todo 如何保证前台被杀,后台AlarmManager会一直计时
    //todo 如何协调  加速度监听 , 闹钟到期的广播接收者 ,与 activity和presenter的关系.


    public static final class AlarmReceiver extends BroadcastReceiver {
        public final String TAG = AlarmReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "AlarmReceiver接收到了广播");
            Intent actIntent = new Intent();
            actIntent.setClass(context, TimeUpActivity.class);
            //todo  被代替 android:launchMode="singleInstance"
            //actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(actIntent);
        }
    }

    public AlarmClockPresenter(Context context, AlarmModel mModel, AlarmMainContract.MainView mView) {
        this.mModel = mModel;
        this.mView = mView;
        mView.setPresenter(this);
        mContext = context;
    }

    @Override
    public void startAlarm(Alarm alarm) {
        Log.e(TAG, "启动Alarm....");

        mModel.getAlarm(new AlarmModel.AlarmDataCallBack() {
            @Override
            public void onSuccess(Alarm alarm) {
                Log.e(TAG, "alarm数据=" + alarm.toString());

                /*
                 * 当activity被回收时,MainActivity.AlarmReceiver这个广播接收者无法启动
                 */

                //这里传入广播接收者的类名
                AlarmUtils.setAlarm(mContext, AlarmClockReceiver.class, alarm.getInterval(), alarm.getId(), alarm);
                mStartMillis = System.currentTimeMillis();
            }

            @Override
            public void onFail() {

            }
        });

        //不在这里启动加速度监听.闹钟到期时再启动
//        startAccService(mContext);
    }


    /**
     * 开启加速度监听
     */
    @Override
    public void registAccListener() {
        Log.e(TAG, "监听服务启动了");
        if (sensorManager == null) {
            sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        }

        if (mSensor == null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(mLisenter, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    public void stopAlarm(Alarm alarm) {
        Log.e(TAG, "停止Alarm...");
        unregistAccListener();
        mView.closeAlarmDialog();
        setVibrator(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAlarm(null);
            }
        }, 5000);
    }

    @Override
    public void pauseAlarm(Alarm alarm) {
        Log.e(TAG, "暂停Alarm...");

        AlarmUtils.cancelAlarm(mContext, AlarmReceiver.class);
        mRemainMillis = alarm.getInterval() - (System.currentTimeMillis() - mStartMillis);
        Log.e(TAG, "剩余时间是:" + mRemainMillis / 1000 + "秒");
    }

    @Override
    public void onAlarmTimeIsUp(Alarm alarm) {
        Log.e(TAG, "Alarm计时到期...");

        mView.showAlarmDialog();
        setVibrator(true);
    }

    @Override
    public void unregistAccListener() {
        sensorManager.unregisterListener(mLisenter, mSensor);
    }

    @Override
    public void stop() {
        unregistBroadcastReceiver();
        unregistAccListener();
    }

    @Override
    public void setMainView(AlarmMainContract.MainView view) {
        mView = view;
    }

    @Override
    public void setAlertView(AlarmMainContract.AlertView view) {
        mAlertView = view;
        //todo 如果传进来的不是空,那么,把P传回去
        if (view != null) {
            mAlertView.setPresenter(this);
        }
    }

    class AccelerometerLisenter implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor1 = event.sensor;
            if (sensor1.getType() == Sensor.TYPE_ACCELEROMETER) {
                float valueX = Math.abs(event.values[0]);
                float valueY = Math.abs(event.values[1]);
                float valueZ = Math.abs(event.values[2]);
                if (valueX > RADIO_ACC || valueY > RADIO_ACC || valueZ > RADIO_ACC) {
                    Log.e(TAG, "传感器数据:" + "X=" + valueX + " Y=" + valueY + " Z=" + valueZ);
                    //完成了一次消息发送.该service已经不再有用
                    //该方法让AlertView自己调用onDestory(),在该析构方法中,有反注册加速度监听器,取消震动,等操作

                    mAlertView.close();

//                  sensorManager.unregisterListener(mLisenter, mSensor);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    public void start() {
        Log.e(TAG, "Presenter.start()...");

        /*
         *这里应该是放置程序启动初期,初始化界面,加载列表等操作
         */
//        registBroadcastReceiver();
    }

    @Override
    public void setView(AlarmMainContract.MainView view) {
        mView = view;
        mView.setPresenter(this);
    }


    @Override
    public void setVibrator(boolean onOrOff) {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            return;
        }

        if (onOrOff) {
            vibrator.vibrate(new long[]{500, 500}, 0);
        } else {
            vibrator.cancel();
        }

    }

    @Override
    public void registBroadcastReceiver() {
        String press1 = "android.intent.action.BOOT_COMPLETED";
        String press2 = "android.intent.action.USER_PRESENT";

        Log.e(TAG, "注册了广播接收者.....");
        mReceiver = new AlarmClockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ALARM_ACTION);
//        filter.addAction(press1);
//        filter.addAction(press2);
        Intent temp = mContext.registerReceiver(mReceiver, filter);
        if (temp != null) {
            Log.e(TAG, "注册结果=" + temp.toString());
        } else {
            Log.e(TAG, "注册广播接收者失败");
        }
    }

    @Override
    public void unregistBroadcastReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }
}
