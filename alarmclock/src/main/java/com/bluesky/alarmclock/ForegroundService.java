package com.bluesky.alarmclock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.bluesky.alarmclock.data.AlarmModelImpl;

/**
 * 前台服务测试
 * todo 明确该service的功能:
 * todo 1.保证存活.2.
 */
public class ForegroundService extends Service {

    public static final String TAG = ForegroundService.class.getSimpleName();
    public static final int ID = 12346;
    private AlarmClockPresenter mPresenter;

    /**
     * todo 修改方案:
     * 1.service需要一个无参构造方法.
     * 2.service的启动方式改为绑定.这样就有iBinder可以操控service
     * 2.1 绑定方式启动,更符合需求:(看音乐播放器的service的使用方法)
     * 2.2 可以先startService(),然后谁需要操作它,就bindService()
     * 3.service应该在P中被启动
     * <p>
     * todo 前台服务的必要性是什么?不容易被回收,有通知图标.
     * todo IntentService的使用(因为service是运行在主线程中,所以耗时任务容易产生ANR)
     *
     * @param view
     */
    public ForegroundService(AlarmMainContract.MainView view) {
        mPresenter = new AlarmClockPresenter(new AlarmModelImpl(), view);
        mPresenter.start();


    }

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "前台服务开启了.....");
//        return super.onStartCommand(intent, flags, startId);
        Intent notificationIntent = new Intent(this, MainActivity.class);

        //这里的PendingIntent就是为了标识Notification的点击事件(开启MainActivity)
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Title")
                .setContentText("Message")
                .setSmallIcon(getApplicationInfo().icon)
                .setContentIntent(pi)
                .build();
        startForeground(ID, noti);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "前台服务停止了.....");

        super.onDestroy();
        stopForeground(true);
    }
}
