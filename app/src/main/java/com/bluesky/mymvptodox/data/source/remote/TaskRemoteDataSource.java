package com.bluesky.mymvptodox.data.source.remote;

import com.bluesky.mymvptodox.data.Task;
import com.bluesky.mymvptodox.data.source.TasksDataSource;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description: 实现一个模拟网络的数据源
 */
public class TaskRemoteDataSource implements TasksDataSource {

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
