package com.bluesky.alarmclock;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static com.bluesky.alarmclock.utils.ActivityManagerUtils.isServiceRunning;

public class TimeUpActivity extends AppCompatActivity implements AlarmMainContract.AlertView {

    public static final String TAG = TimeUpActivity.class.getSimpleName();
    private boolean isVisible;
    private AlarmMainContract.MainPresenter mPresenter;

    Button btnOk;

    ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected被回调了...");
            ForeService.MyBinder mBinder = (ForeService.MyBinder) service;
            mBinder.setTimeUpView(TimeUpActivity.this);
            //todo 只有运行到这里,mPresenter才有值,才能执行操作
            mPresenter.setVibrator(true);
            mPresenter.registAccListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected被回调了...");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setGravity(Gravity.CENTER);
        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "finish之前,presenter=" + mPresenter.toString());

                finish();
            }
        });

        try {
            bindForeService();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void bindForeService() throws Exception {
        boolean isRunning = isServiceRunning(this, ForeService.class.getSimpleName());
        Intent intentService = new Intent(this, ForeService.class);
        if (!isRunning) {
            throw new Exception("在AlertView中---------->后台服务ForeService不应该找不到!!!");
        }
        bindService(intentService, mConn, BIND_AUTO_CREATE);
    }

    @Override
    public boolean isActive() {
        return isVisible;
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void setPresenter(AlarmMainContract.MainPresenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 此时Activity已经有可能不可见.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Activity被onPause了...");
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Activity被onResume了...");
        isVisible = true;
    }

    /**
     * 此时Activity不可见
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "finish之前,presenter=" + mPresenter == null ? "null" : "非null");

        mPresenter.setAlertView(null);
        mPresenter.setVibrator(false);
        mPresenter.unregistAccListener();
        unbindService(mConn);
        super.onDestroy();
    }
}
