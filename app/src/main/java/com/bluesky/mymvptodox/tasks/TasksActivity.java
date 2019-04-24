package com.bluesky.mymvptodox.tasks;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bluesky.mymvptodox.R;
import com.bluesky.mymvptodox.taskdetail.TaskDetailFragment;
import com.bluesky.mymvptodox.util.Injection;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author BlueSky
 * @date 2019/3/18
 * Description: Task列表主Activity
 */
public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private static final String TAG = TasksActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private TasksPresenter mPresenter;
    private TasksFragment mTasksFragment;
    private TaskDetailFragment mTaskDetailFragment;
    private Fragment mCurrentFrangemt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ActionBar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        //FloatingActionButton,由fragement去处理了

        //DrawerLayout
        //源码中没有使用toggle.而是在onOptionsItemSelected中给R.id.home设定打开drawer.(滑动开启关闭应该是自动的)
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //手动创建fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mTasksFragment = TasksFragment.newInstance();
        mTaskDetailFragment = TaskDetailFragment.newInstance(TaskDetailFragment.class.getSimpleName());
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), taskListFragment, R.id.fl_content);
        transaction.add(R.id.fl_content, mTasksFragment, TasksFragment.class.getSimpleName());
        transaction.add(R.id.fl_content, mTaskDetailFragment, TaskDetailFragment.class.getSimpleName());
        transaction.hide(mTaskDetailFragment);
        transaction.show(mTasksFragment);
        transaction.commit();
        mCurrentFrangemt = mTasksFragment;

        //创建presenter
        //todo 这里的context使用了getApplicationContext
        mPresenter = new TasksPresenter(Injection.provideTasksRepository(getApplicationContext()), mTasksFragment);

        //恢复存resume状态
        if (savedInstanceState != null) {
            TasksFilterType currentFiltering = (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 侧边栏列表项选择处理函数
     * 1.负责fragment的切换
     * 2.每个fragment自己负责对activity的控件的可见状态进行修改
     *
     * @param item 菜单项
     * @return 是否消费
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_list:
                if (mCurrentFrangemt instanceof TasksFragment) {
                    break;
                } else {
                    mTasksFragment = (TasksFragment) manager.findFragmentByTag(TasksFragment.class.getSimpleName());
                    if (mTasksFragment == null) {
                        Log.d(TAG, "TasksFragment==null.findFragmentByTag 失败...");
                        //todo 这里是否应该添加创建fragment的代码,当fragment被强制回收时
                    }
                    transaction.hide(mCurrentFrangemt).show(mTasksFragment).commitNow();
                    mCurrentFrangemt = mTasksFragment;
                }
                break;
            case R.id.nav_stat:
                if (mCurrentFrangemt instanceof TaskDetailFragment) {
                    break;
                } else {
                    mTaskDetailFragment = (TaskDetailFragment) manager.findFragmentByTag(TaskDetailFragment.class.getSimpleName());
                    if (mTaskDetailFragment == null) {
                        Log.d(TAG, "TaskDetailFragment==null.findFragmentByTag 失败...");
                        //todo 这里是否应该添加创建fragment的代码,当fragment被强制回收时
                    }
                    transaction.hide(mCurrentFrangemt).show(mTaskDetailFragment).commitNow();
                    mCurrentFrangemt = mTaskDetailFragment;
                }
                break;
            default:
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
