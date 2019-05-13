package com.bluesky.alarmclock.data;

public class AlarmModelImpl implements AlarmModel {
    @Override
    public void getAlarm(AlarmDataCallBack listener) {
        Alarm alarm = new Alarm("Ten Sec Alarm", 10 * 1000);
        listener.onSuccess(alarm);

    }
}
