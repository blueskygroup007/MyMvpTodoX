package com.bluesky.alarmclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Messenger;
import android.os.Vibrator;
import android.util.Log;

import com.bluesky.alarmclock.data.Alarm;
import com.bluesky.alarmclock.data.AlarmModel;
import com.bluesky.alarmclock.utils.AlarmUtils;

/**
 * Presenter 如果持有 Activity 的强引用，在请求结束之前 Activity 被销毁了，那么由于网络请求还没有返回，
 * 导致 Presenter 一直持有 Activity 对象，使得 Activity 无法被回收，此时就容易发生内存泄漏，
 * 解决这个问题需要通过弱引用来解决
 *
 */
public class AlarmClockPresenter implements AlarmMainContract.MainPresenter {

    public static final String TAG = AlarmClockPresenter.class.getSimpleName();
    AlarmModel mModel;
    AlarmMainContract.MainView mView;
    private Intent mService;
    private Context mContext;
    private Handler mHandler;

    long mStartMillis = System.currentTimeMillis();
    long mRemainMillis = 0;

//    private Messenger mMessenger;

    //todo ---------当前进度----------
    //todo 如何保证前台被杀,后台AlarmManager会一直计时
    //todo 如何协调  加速度监听 , 闹钟到期的广播接收者 ,与 activity和presenter的关系.

    public AlarmClockPresenter(AlarmModel mModel, AlarmMainContract.MainView mView) {
        this.mModel = mModel;
        this.mView = mView;
        mView.setPresenter(this);
        mContext = ((MainActivity) mView).getBaseContext();
        mHandler = ((MainActivity) mView).getHandler();
//        mMessenger=((MainActivity)mView).getMessenger();

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
                AlarmUtils.setAlarm(mContext, AlarmClockReceiver.class, alarm.getInterval());
                mStartMillis = System.currentTimeMillis();
            }

            @Override
            public void onFail() {

            }
        });

        //不在这里启动加速度监听.闹钟到期时再启动
//        startAccService(mContext);
    }

    private void startAccService(Context context) {
        Log.e(TAG, "启动了sensor监听....");

        if (mService == null) {
            mService = new Intent(context, AccelerationService.class);
            mService.putExtra("messenger", new Messenger(mHandler));
        }
        context.startService(mService);

    }


    @Override
    public void stopAlarm(Alarm alarm) {
        Log.e(TAG, "停止Alarm...");
        stopAccService();
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

        AlarmUtils.cancelAlarm(mContext, AlarmClockReceiver.class);
        mRemainMillis = alarm.getInterval() - (System.currentTimeMillis() - mStartMillis);
        Log.e(TAG, "剩余时间是:" + mRemainMillis / 1000 + "秒");
    }

    @Override
    public void onAlarmTimeIsUp(Alarm alarm) {
        Log.e(TAG, "Alarm计时到期...");

        mView.showAlarmDialog();
        setVibrator(true);
        startAccService(mContext);
    }

    @Override
    public void stopAccService() {
        mContext.stopService(mService);
    }


    @Override
    public void start() {
        Log.e(TAG, "Presenter.start()...");

        /*
         *这里应该是放置程序启动初期,初始化界面,加载列表等操作
         */
    }

    private void setVibrator(boolean onOrOff) {
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
}
