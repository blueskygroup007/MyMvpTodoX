package com.bluesky.alarmclock;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * @author BlueSky
 * @date 2019/5/10
 * Description:
 */
public class AccelerationService extends Service {
    public static final String TAG = AccelerationService.class.getSimpleName();
    private SensorManager sensorManager;
    private static final int RADIO_ACC = 17;//传感器幅度值常量

    private Messenger mMessenger;
    private Sensor mSensor;
    private AccelerometerLisenter mLisenter = new AccelerometerLisenter();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        sensorManager.registerListener(mLisenter, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mMessenger == null) {
            mMessenger = (Messenger) intent.getExtras().get("messenger");
        }

        if (sensorManager == null) {
            sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        }
        if (mSensor == null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        }

        sensorManager.registerListener(mLisenter, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(mLisenter, mSensor);
        Log.e(TAG, "监听服务停止了");
        super.onDestroy();
    }

    class AccelerometerLisenter implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor1 = event.sensor;
            if (sensor1.getType() == Sensor.TYPE_ACCELEROMETER) {
                float valueX = Math.abs(event.values[0]);
                float valueY = Math.abs(event.values[1]);
                float valueZ = Math.abs(event.values[2]);
                if (valueX > RADIO_ACC || valueY > RADIO_ACC || valueZ > RADIO_ACC) {
                    Log.e(TAG, "传感器数据:" + "X=" + valueX + " Y=" + valueY + " Z=" + valueZ);
                    Message msg = new Message();
                    msg.what = 1;
                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
