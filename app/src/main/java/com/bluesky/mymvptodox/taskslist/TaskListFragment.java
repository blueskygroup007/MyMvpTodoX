package com.bluesky.mymvptodox.taskslist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bluesky.mymvptodox.R;
import com.bluesky.mymvptodox.addedittask.AddEditTaskActivity;
import com.bluesky.mymvptodox.data.Task;
import com.bluesky.mymvptodox.taskdetail.TaskDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description:
 */
public class TaskListFragment extends Fragment implements TaskListContract.View {

    private TaskListContract.Presenter mPresenter;

    private TaskListAdapter mListAdapter;


    // 没有任务的提示框---------

    private View mNoTasksView;


    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

    // ------------------


    private LinearLayout mTasksView;

    private TextView mFilteringLabelView;

    //测试用成员变量
    private static final String ARG_PARM1 = "param1";
    private static final String ARG_PARM2 = "param2";
    private int mParam1;
    private String mParam2;

    /**
     * 无参构造函数,必须有
     */
    public TaskListFragment() {
    }

    /**
     * fragment的创建方法,加了两个无用参数来说明用法
     *
     * @param param1
     * @param param2
     * @return
     */
    public static TaskListFragment newInstance(int param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARM1, param1);
        bundle.putString(ARG_PARM2, param2);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //真正的启动入口
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //测试代码:当fragment重新创建时,从bundle中取出参数
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARM1);
            mParam2 = getArguments().getString(ARG_PARM2);
        }
        //-----------------------------------------------
        View root = inflater.inflate(R.layout.tasklist_frag, container, false);
        //初始化任务列表视图
        ListView listView = root.findViewById(R.id.lv_task_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = root.findViewById(R.id.tv_filtering_lable);
        mTasksView = root.findViewById(R.id.ll_tasklist);
        //初始化no tasks窗体视图
        mNoTasksView = root.findViewById(R.id.ll_no_task);
        mNoTaskIcon = root.findViewById(R.id.iv_no_task_icon);
        mNoTaskMainView = root.findViewById(R.id.tv_no_tasks_main);
        mNoTaskAddView = root.findViewById(R.id.tv_no_tasks_add);
        //这个控件是NoTask视图中的add控件,visible状态在别处设置
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTask();
            }
        });

        //设置浮动按钮(加号)
        //在fragment中设置activity的浮动按钮,它不属于当前fragment的layout中
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTask();
            }
        });

        //设置进度指示器
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        //swipeRefreshLayout.setColorSchemeColors(getActivity().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        //设置滚动视图给自定义的SwipRefreshLayout
        swipeRefreshLayout.setScrollUpChild(listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTasks(false);
            }
        });
        //不调用这个方法,onCreateOptionsMenu就不会被执行(Fragment中)
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_clear:
                mPresenter.clearCompletedTasks();
                break;
            case R.id.menu_refresh:
                mPresenter.loadTasks(true);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task_list_frag, menu);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);
        //确保setRefreshing()在布局完成后再被调用
        //todo View.post()
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });

    }

    @Override
    public void showTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetailsUi(String taskId) {
        //源码注释说,在它自己的activity中,用以显示intent的灵活性
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete));
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active));
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared));
    }

    @Override
    public void showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(getString(R.string.no_tasks_all), R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getString(R.string.label_completed));

    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getString(R.string.label_all));

    }

    @Override
    public void showNoActiveTasks() {
        showNoTasksViews(getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews(getString(R.string.no_tasks_completed), R.drawable.ic_verified_user_24dp, false);

    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);
        mNoTaskMainView.setText(mainText);
        //todo 使用了contextcompat,源码使用getResources().getDrawable
        mNoTaskIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);

    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_task_list, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;
                    default:
                        mPresenter.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(TaskListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    private class TaskListAdapter extends BaseAdapter {

        private List<Task> mTasks;
        private TaskItemListener mItemListener;

        public TaskListAdapter(List<Task> tasks, TaskItemListener itemListener) {
            mTasks = tasks;
            mItemListener = itemListener;
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> tasks) {
            mTasks = checkNotNull(tasks);
        }


        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.task_item, parent, false);
            }

            final Task task = getItem(position);

            TextView tvTitle = rowView.findViewById(R.id.tv_title);
            tvTitle.setText(task.getTitleForList());
            CheckBox cbComplete = rowView.findViewById(R.id.completed);
            cbComplete.setChecked(task.isCompleted());

            if (task.isCompleted()) {
                rowView.setSelected(true);
            } else {
                rowView.setSelected(false);
            }

            cbComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!task.isCompleted()) {
                        mItemListener.onCompleteTaskClick(task);
                    } else {
                        mItemListener.onActivateTaskClick(task);
                    }
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onTaskClick(task);
                }
            });
            return rowView;
        }
    }

    public interface TaskItemListener {
        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }
}
