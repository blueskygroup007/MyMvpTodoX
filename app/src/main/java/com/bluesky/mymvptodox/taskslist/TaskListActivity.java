package com.bluesky.mymvptodox.taskslist;

import android.content.Intent;
import android.os.Bundle;

import com.bluesky.mymvptodox.R;
import com.bluesky.mymvptodox.data.source.TasksRepository;
import com.bluesky.mymvptodox.data.source.local.TaskLocalDataSource;
import com.bluesky.mymvptodox.data.source.remote.TaskRemoteDataSource;
import com.bluesky.mymvptodox.statistics.StatisticsActivity;
import com.bluesky.mymvptodox.util.ActivityUtils;
import com.bluesky.mymvptodox.util.Injection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

/**
 * @author BlueSky
 * @date 2019/3/18
 * Description: Task列表主Activity
 */
public class TaskListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_FILTERING_KEY="CURRENT_FILTERING_KEY";
    private DrawerLayout mDrawerLayout;
    private TaskListPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasklist_act);

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

/*        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());*/
        //DrawerLayout
        //源码中没有使用toggle.而是在onOptionsItemSelected中给R.id.home设定打开drawer.(滑动开启关闭应该是自动的)
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        /*ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);*/
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //以findFragmentById的方式生成fragment
        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fl_content);
        //手动创建fragment
        if (taskListFragment == null) {
            taskListFragment = TaskListFragment.newInstance(0, "0");
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), taskListFragment, R.id.fl_content);
        }
        //创建presenter
        //todo 这里的context使用了getApplicationContext
        mPresenter=new TaskListPresenter(Injection.provideTasksRepository(getApplicationContext()), taskListFragment);

        //恢复存resume状态
        if (savedInstanceState!=null){
            TasksFilterType currentFiltering= (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {
            //Todo 源码并没有做什么，只是说，当前就是在这个屏幕下
        } else if (id == R.id.nav_stat) {
            Intent intent=new Intent(TaskListActivity.this, StatisticsActivity.class);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
