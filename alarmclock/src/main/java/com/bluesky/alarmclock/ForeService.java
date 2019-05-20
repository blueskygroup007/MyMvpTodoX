package com.bluesky.alarmclock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.bluesky.alarmclock.data.AlarmModelImpl;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @author BlueSky
 * @date 2019/5/20
 * Description:
 */
public class ForeService extends Service {
    private static final String TAG = ForeService.class.getSimpleName();
    public static final int ID = 12346;
    private static final String ACTION_DISMISS = "action_dismiss";
    private static boolean isRunning = false;
    private AlarmClockPresenter mPresenter;

    public class MyBinder extends Binder {
        public ForeService getInstance() {
            return ForeService.this;
        }

        public void doSomeThing() {
            Log.e(TAG, "ForeService do someThing...");
        }

        /**
         * todo 该方法执行时机是Bind(绑定)Service,
         * todo 分为第一次创建,或者Activity Resume.
         * todo 即--Activity和Service建立联系.同时也是V和P建立联系.
         * todo 此时的工作应该是:将V给P,将P也给V.使双方能互相操作
         *
         * @param view
         */
        public void start(AlarmMainContract.MainView view) {
            if (mPresenter == null) {
                mPresenter = new AlarmClockPresenter(new AlarmModelImpl(), view);
                mPresenter.start();
            } else {
                mPresenter.setView(view);
                mPresenter.start();
            }

        }
    }

    private MyBinder mBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            Log.e(TAG, "前台任务被重复启动...................");
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_DISMISS)) {
                //true代表顺便移除通知,但service应该还在
                stopForeground(true);
                Log.e(TAG, "service被停止前台了...................");

                return START_STICKY;
            }
        }
        Log.e(TAG, "前台服务首次启动了.onStartCommand 和startForeground.....");

        startForeground(ID, createNoti());
        isRunning = true;
        return START_STICKY;
    }


    public Notification createNoti() {
        //点击通知栏的PI
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //关闭通知栏的PI
        Intent dismissIntent = new Intent(this, ForeService.class);
        dismissIntent.setAction(ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(
                this, 0, dismissIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("新版Noti")
                .setContentText("消息内容....")
                .setSmallIcon(R.drawable.ic_star_border_black_24dp)
                .setDefaults(Notification.DEFAULT_ALL)
                // 该方法在Android 4.1之前会被忽略
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("大文本风格"))
                .addAction(R.drawable.ic_star_black_24dp, "btn", piDismiss);
        return builder.build();
    }

    public void createNotiOld() {
        //点击通知栏的PI
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //关闭通知栏的PI
        Intent dismissIntent = new Intent(this, ForeService.class);
        dismissIntent.setAction(ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(
                this, 0, dismissIntent, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Title")
                .setContentText("Message")
                .setSmallIcon(getApplicationInfo().icon)
                .setContentIntent(pi)
                //Notification.Builder的addAction()方法对版本有要求,所以改用NotificationCompat.Builder
//                .addAction(new Notification.Action(R.drawable.ic_star_black_24dp, "close", piDismiss))
                .build();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "前台服务onUnbind了.....");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "前台服务onDestroy了.....");
        super.onDestroy();
        stopForeground(true);
    }
}
