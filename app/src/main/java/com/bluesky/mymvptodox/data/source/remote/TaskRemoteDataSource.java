package com.bluesky.mymvptodox.data.source.remote;

import android.os.Handler;

import com.bluesky.mymvptodox.data.Task;
import com.bluesky.mymvptodox.data.source.TasksDataSource;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Iterator;
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
        addTask("Reading the EnglishBook---Desert Mountain Sea.", "Himalaya Kathmandu");

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


    /**
     * 模拟网络取数据，先延时5秒，再返回自行创建的两个task
     *
     * @param callback
     */
    @Override
    public void getTasks(LoadTasksCallback callback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getTask(String taskId, GetTaskCallback callback) {
        final Task task = TASKS_SERVICE_DATA.get(taskId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTaskLoaded(task);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveTask(Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(Task task) {
        Task completedTask = new Task(task.getId(), task.getTitle(), task.getDescription(), true);
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(String taskId) {
        //源码注明不需要实现
    }

    @Override
    public void activateTask(Task task) {
        Task activeTask = new Task(task.getId(), task.getTitle(), task.getDescription());
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void activateTask(String taskId) {
        //同上，不需要实现
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        //同上
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
