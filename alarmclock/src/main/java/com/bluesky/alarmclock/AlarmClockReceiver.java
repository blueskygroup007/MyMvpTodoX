package com.bluesky.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluesky.alarmclock.data.Alarm;


public class AlarmClockReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmClockReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        int id = intent.getIntExtra("flag", 0);
        Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");
        Log.e(TAG, "AlarmClockReceiver接收到了广播" + "  flag=" + id + "  alarm.name=" + alarm.getName() + "  alarm.id=" + alarm.getId());


        Intent actIntent = new Intent();
        actIntent.setClass(context, TimeUpActivity.class);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(actIntent);

    }

}
