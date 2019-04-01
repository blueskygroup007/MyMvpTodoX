package com.bluesky.mymvptodox.data.source.remote;

import com.bluesky.mymvptodox.data.Task;
import com.bluesky.mymvptodox.data.source.TasksDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author BlueSky
 * @date 2019/3/12
 * Description: 实现一个模拟网络的数据源
 */
public class TaskRemoteDataSource implements TasksDataSource {

    private static TaskRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");

    }

    public static TaskRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TaskRemoteDataSource();
        }
        return INSTANCE;
    }

    private TaskRemoteDataSource() {
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
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
