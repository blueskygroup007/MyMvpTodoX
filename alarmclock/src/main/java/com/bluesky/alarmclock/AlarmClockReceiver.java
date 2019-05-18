package com.bluesky.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmClockReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmClockReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.e(TAG, "AlarmClockReceiver接收到了广播");
/*        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("提示");
        dialogBuilder.setMessage("这是在BroadcastReceiver弹出的对话框。");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlarmUtils.setAlarm(context, AlarmClockReceiver.class, 10 * 1000);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();*/

        Intent actIntent = new Intent();
        actIntent.setClass(context, AlertDialogActivity.class);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(actIntent);

    }

    public void temp(Context context) {
        //这里收到闹钟到时广播.

        //todo 设想步骤:
        //todo 1.将广播接收者独立出去.
        //      2.当接收到广播时,接收者自己弹窗.
        //      3.MainActivity做好被回收时的 数据保存,数据恢复
        //      4.考虑使用后台service来保证app运行

        Intent clockIntent = new Intent(context, MainActivity.class);
        clockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
//            mPresenter.onAlarmTimeIsUp(mAlarm);
    }
}
