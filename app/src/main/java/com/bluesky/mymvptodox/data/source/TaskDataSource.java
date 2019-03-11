package com.bluesky.mymvptodox.data.source;

import com.bluesky.mymvptodox.data.Task;

import java.util.List;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description: Task数据的接口,访问数据的主要入口
 */
public interface TaskDataSource {

    /**
     * 获取所有task的回调.注意"Tasks"
     */
    interface LoadTasksCallback {
        /**
         * 当tasks成功获取的回调
         *
         * @param tasks
         */
        void onTasksLoaded(List<Task> tasks);

        /**
         * 数据无效的回调
         */
        void onDataNotAvailable();
    }

    /**
     * 获取某个task
     */
    interface GetTaskCallback {

        /**
         * 当task成功获取的回调
         *
         * @param task
         */
        void onTaskLoaded(Task task);

        /**
         * 数据无效的回调
         */
        void onDataNotAvailable();
    }

    /**
     * 得到所有tasks
     *
     * @param callback
     */
    void getTasks(LoadTasksCallback callback);

    /**
     * 得到某个task
     *
     * @param taskId
     * @param callback
     */
    void getTask(String taskId, GetTaskCallback callback);

    /**
     * 保存一个task
     *
     * @param task
     */
    void saveTask(Task task);

    /**
     * 结束某个task,用task
     *
     * @param task
     */
    void completeTask(Task task);

    /**
     * 结束某个task,用id
     *
     * @param taskId
     */
    void completeTask(String taskId);

    /**
     * 激活某个task,用task
     *
     * @param task
     */
    void activateTask(Task task);

    /**
     * 激活某个task,用id
     *
     * @param taskId
     */
    void activateTask(String taskId);

    /**
     * 清除所有已经完成的task
     */
    void clearCompletedTasks();

    /**
     * 刷新
     */
    void refreshTasks();

    /**
     * 删除所有
     */
    void deleteAllTasks();

    /**
     * 删除某个task
     *
     * @param taskId
     */
    void deleteTask(String taskId);
}
