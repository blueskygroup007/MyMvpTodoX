package com.bluesky.mymvptodox.data.source;

import com.bluesky.mymvptodox.data.Task;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description:
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

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

    @Override
    public void getTasks(LoadTasksCallback callback) {

    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {

    }

    @Override
    public void saveTask(Task task) {

    }

    @Override
    public void completeTask(Task task) {

    }

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
