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
        AlarmUtils.setAlarm(mContext, AlarmReceiver.class, alarm.getInterval());
        mStartMillis = System.currentTimeMillis();
        startAccService(mContext);
    }

    private void startAccService(Context context) {
        if (mService == null) {
            mService = new Intent(context, AccelerationService.class);
            mService.putExtra("messenger", new Messenger(mHandler));
        }
        context.startService(mService);
        Log.e(TAG, "启动了sensor监听....");

    }


    @Override
    public void stopAlarm(Alarm alarm) {
        mContext.stopService(mService);
        mView.closeAlarmDialog();
        setVibrator(false);
    }

    @Override
    public void pauseAlarm(Alarm alarm) {
        AlarmUtils.cancelAlarm(mContext, AlarmReceiver.class);
        mRemainMillis = alarm.getInterval() - (System.currentTimeMillis() - mStartMillis);
        Log.e(TAG, "剩余时间是:" + mRemainMillis / 1000 + "秒");
    }


    @Override
    public void start() {
        mModel.getAlarm(new AlarmModel.AlarmDataCallBack() {
            @Override
            public void onSuccess(Alarm alarm) {
                mView.showAlarm(alarm);
                setVibrator(true);
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void setVibrator(boolean onOrOff) {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()) {
            return;
        }

        if (onOrOff) {
            vibrator.vibrate(500);
        } else {
            vibrator.cancel();
        }

    }
}
