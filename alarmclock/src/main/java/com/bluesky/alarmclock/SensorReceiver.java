package com.bluesky.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author BlueSky
 * @date 2019/5/9
 * Description:
 */
public class SensorReceiver extends BroadcastReceiver {
    public static final String TAG = SensorReceiver.class.getSimpleName().toString();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "接收到了sensor广播");
    }
}
