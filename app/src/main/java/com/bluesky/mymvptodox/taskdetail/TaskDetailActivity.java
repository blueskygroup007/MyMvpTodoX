package com.bluesky.mymvptodox.taskdetail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bluesky.mymvptodox.R;

/**
 * @author BlueSky
 * @date 2019/3/18
 * Description: Task详情页面
 */
public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_act);
    }
}
