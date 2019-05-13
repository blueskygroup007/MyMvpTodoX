package com.bluesky.alarmclock;

import com.bluesky.alarmclock.data.Alarm;

public interface AlarmMainContract {

    interface MainPresenter extends BasePresenter{
        void startAlarm(Alarm alarm);
        void stopAlarm(Alarm alarm);
        void pauseAlarm(Alarm alarm);

    }

    interface MainView extends BaseView<MainPresenter>{
        void showAlarm(Alarm alarm);
        void showAlarmDialog();
        void closeAlarmDialog();

    }
}
