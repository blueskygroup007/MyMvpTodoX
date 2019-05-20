package com.bluesky.alarmclock;

import com.bluesky.alarmclock.data.Alarm;

public interface AlarmMainContract {

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

        /**
         * 停止加速度监听服务
         */
        void stopAccService();


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
}
