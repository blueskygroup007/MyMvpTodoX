package com.bluesky.mymvptodox.data.source.local;

import com.bluesky.mymvptodox.data.Task;
import com.bluesky.mymvptodox.data.source.TasksDataSource;
import com.bluesky.mymvptodox.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/20
 * Description:
 */
public class TaskLocalDataSource implements TasksDataSource {

    private static volatile TaskLocalDataSource INSTANCE;
    private TasksDao mTasksDao;

    //todo 这里用到了Executors类,需要了解
    private AppExecutors mAppExecutors;

    private TaskLocalDataSource(TasksDao tasksDao, AppExecutors appExecutors) {
        mTasksDao = tasksDao;
        mAppExecutors = appExecutors;
    }

    public static TaskLocalDataSource getInstance(TasksDao tasksDao, AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (TaskLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskLocalDataSource(tasksDao, appExecutors);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public void getTasks(LoadTasksCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Task> tasks = mTasksDao.getTasks();
                //todo 如何保证getTasks()返回结果之后,才开始在mainThread检查结果(tasks)为空?
                //todo debug一下,看看是否都在主线程.

                //todo 理解:这里的getTasks()是个阻塞操作.只有结果返回了,才会执行下一行代码.
                //todo 接下来只是为了在主线程中压入一个事件,即回调
                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tasks.isEmpty()) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onTasksLoaded(tasks);
                        }
                    }
                });
            }
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Task task = mTasksDao.getTaskById(taskId);
                mAppExecutors.getMainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (task != null) {
                            callback.onTaskLoaded(task);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void saveTask(Task task) {
        checkNotNull(task);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.insertTask(task);
            }
        };
        mAppExecutors.getDiskIO().execute(saveRunnable);
    }

    @Override
    public void completeTask(Task task) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                mTasksDao.updateCompleted(task.getId(), true);
            }
        };
        mAppExecutors.getDiskIO().execute(completeRunnable);
    }

    /**
     * 这个方法没有实现,源码上说本地数据源不需要,因为TasksRepository负责转换一个taskId到task,使用它缓存的数据
     * 可能是说,由于TasksRepository可以用taskId转换成task,然后调用上面那个使用task的方法即可
     * @param taskId
     */
    @Override
    public void completeTask(String taskId) {

    }

    @Override
    public void activateTask(Task task) {

    }

    @Override
    public void activateTask(String taskId) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteTask(String taskId) {

    }
}
