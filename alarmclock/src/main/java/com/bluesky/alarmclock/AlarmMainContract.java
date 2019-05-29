package com.bluesky.alarmclock;

import com.bluesky.alarmclock.data.Alarm;

public interface AlarmMainContract {

    /**
     * todo isActive()只用来检测Activity是否在前台
     * todo 应该让Activity销毁时,把Presenter中的View置null  (通过setAlertView()方法)
     */
    interface MainPresenter extends BasePresenter<MainView> {

        /**
         * 启动闹钟
         *
         * @param alarm
         */
        void startAlarm(Alarm alarm);

        /**
         * 停止闹钟
         *
         * @param alarm
         */
        void stopAlarm(Alarm alarm);

        /**
         * 暂停闹钟
         *
         * @param alarm
         */
        void pauseAlarm(Alarm alarm);

        /**
         * 闹钟计时到了
         *
         * @param alarm
         */
        void onAlarmTimeIsUp(Alarm alarm);

        void registBroadcastReceiver();

        void unregistBroadcastReceiver();

        void registAccListener();

        /**
         * 停止加速度监听服务
         */
        void unregistAccListener();

        /**
         * 结束时,回收清理反注册工作
         */
        void stop();

        /**
         * 给P设置主界面view,或者置null
         * todo 传null时,必须加判断,因为此时无需将P回传给View,否则空指针
         *
         * @param view
         */
        void setMainView(MainView view);

        /**
         * 给P设置闹钟弹出对话框View,或者置null
         * todo 传null时,必须加判断,因为此时无需将P回传给View,否则空指针
         *
         * @param view
         */
        void setAlertView(AlertView view);

        void setVibrator(boolean onOrOff);
    }

    interface MainView extends BaseView<MainPresenter> {
        void showAlarm(Alarm alarm);

        void showAlarmDialog();

        void closeAlarmDialog();

        /*
        判断activity是否还存活
         */
        boolean isActive();


    }

    interface AlertView extends BaseView<MainPresenter> {
        boolean isActive();
        void close();
    }
}
