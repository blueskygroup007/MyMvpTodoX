package com.bluesky.mymvptodox.taskslist;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description:过滤类型
 */
public enum TasksFilterType {
    /**
     * 不过滤,显示所有任务
     */
    ALL_TASKS,

    /**
     * 只显示活动任务(过滤掉完成任务)
     */
    ACTIVE_TASKS,

    /**
     * 显示完成任务(过滤掉活动任务)
     */
    COMPLETED_TASKS
}
