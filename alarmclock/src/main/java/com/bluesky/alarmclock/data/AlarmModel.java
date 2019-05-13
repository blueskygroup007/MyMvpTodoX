package com.bluesky.alarmclock.data;

public interface AlarmModel {
    void getAlarm(AlarmDataCallBack listener);

    interface AlarmDataCallBack {
        void onSuccess(Alarm alarm);

        void onFail();
    }
}
