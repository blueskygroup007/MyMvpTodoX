package com.bluesky.mymvptodox.data.source;

import com.bluesky.mymvptodox.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description: 三级缓存的task仓库实现
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    /**
     * 这个变量被赋予默认访问权限，即包访问权限，是为了测试时能够访问到。
     */
    Map<String, Task> mCachedTasks;

    /**
     * 将缓存标记为无效，以便在下次请求数据时强制更新，可见性同上
     */
    boolean mCacheIsDirty = false;

    private TasksRepository(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * 返回单例
     *
     * @param tasksRemoteDataSource
     * @param tasksLocalDataSource
     * @return
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource, TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //TODO 仅仅继承了方法,还未具体实现

    /**
     * 获取所有task
     *
     * @param callback presenter的回调，通知presenter获取成功或者失败
     */
    @Override
    public void getTasks(LoadTasksCallback callback) {
        checkNotNull(callback);

        //注意，此处使用三级缓存。一级“缓存”是指内存，二级是指本地数据库，三级是指net

        //如果有缓存，或者缓存有效(不需要更新)，直接从缓存取数据给回调
        if (mCachedTasks != null && !mCacheIsDirty) {
            //new一个list副本，应该是为了防止修改缓存
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            return;
        }

        //没缓存，或者缓存过期，都到这里

        //如果缓存需要更新，从网络取一次
        if (mCacheIsDirty) {
            getTasksFromRemoteDataSource(callback);
        } else {//否则，从本地取
            mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    /**
     * 更新缓存
     *
     * @param tasks
     */
    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    /**
     * 从网络缓存取数据
     *
     * @param callback
     */
    private void getTasksFromRemoteDataSource(LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * 更新本地缓存
     *
     * @param tasks
     */
    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for (Task task :
                tasks) {
            mTasksLocalDataSource.saveTask(task);
        }
    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);
        Task cachedTask = getTaskWithId(taskId);
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        //本地缓存取task
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                if (mCachedTasks == null) {
                    mCachedTasks = new LinkedHashMap<>();
                }
                mCachedTasks.put(task.getId(), task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                //网络缓存取task
                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        if (mCachedTasks == null) {
                            mCachedTasks = new LinkedHashMap<>();
                        }
                        mCachedTasks.put(task.getId(), task);
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        //都没有取到
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    private Task getTaskWithId(String taskId) {
        checkNotNull(taskId);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }

    }

    @Override
    public void saveTask(Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }


    @Override
    public void completeTask(Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);
        //根据原task生成一个新task，并设置completed为true，并用HashMap的put方法覆盖原task
        Task completedTask = new Task(task.getId(), task.getTitle(), task.getDescription(), true);

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activiteTask = new Task(task.getId(), task.getTitle(), task.getDescription());

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), activiteTask);
    }

    @Override
    public void activateTask(String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        //todo 只是设置了标志位，并没有主动刷新列表
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(String taskId) {
        mTasksRemoteDataSource.deleteTask(taskId);
        mTasksLocalDataSource.deleteTask(taskId);

        mCachedTasks.remove(taskId);
    }
}
