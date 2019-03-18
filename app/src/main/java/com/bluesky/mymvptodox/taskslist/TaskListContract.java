package com.bluesky.mymvptodox.taskslist;

import com.bluesky.mymvptodox.BasePresenter;
import com.bluesky.mymvptodox.BaseView;
import com.bluesky.mymvptodox.data.Task;

import java.util.List;

/**
 * @author BlueSky
 * @date 2019/3/5
 * Description: Taskist功能的契约类,包含V和P的接口
 */
public interface TaskListContract {

    /**
     * P的接口,先定义,因为V要用到
     */
    interface Presenter extends BasePresenter {
        /**
         * onActivityResult传递用
         *
         * @param requestCode
         * @param resultCode
         */
        void result(int requestCode, int resultCode);

        /**
         * 从网络加载task列表
         *
         * @param forceUpdate 是否强制更新
         */
        void loadTasks(boolean forceUpdate);

        /**
         * 添加新task
         */
        void addNewTask();

        /**
         * 打开某个task详情页
         *
         * @param requestedTask
         */
        void openTaskDetails(Task requestedTask);

        /**
         * 结束某个Task
         *
         * @param completedTask
         */
        void completeTask(Task completedTask);

        /**
         * 激活某个task
         *
         * @param activeTask
         */
        void activateTask(Task activeTask);

        /**
         * 清除所有已经完成的Task
         */
        void clearCompletedTasks();

        /**
         * 设置过滤类型
         *
         * @param requestType
         */
        void setFiltering(TasksFilterType requestType);

        /**
         * 得到当前过滤类型
         *
         * @return
         */
        TasksFilterType getFiltering();
    }

    /**
     * V的接口
     */
    interface View extends BaseView<Presenter> {

        /**
         * 设置loading指示器.
         *
         * @param active 是否显示刷新进度
         */
        void setLoadingIndicator(boolean active);

        /**
         * 显示所有tasks列表
         *
         * @param tasks
         */
        void showTasks(List<Task> tasks);

        /**
         * 显示添加任务界面
         */
        void showAddTask();

        /**
         * 显示任务详情界面
         *
         * @param taskId 任务的id
         */
        void showTaskDetailsUi(String taskId);

        /**
         * 显示"任务标注完成"
         */
        void showTaskMarkedComplete();

        /**
         * 显示"任务标注激活"
         */
        void showTaskMarkedActive();

        /**
         * 显示"已完成任务清除"
         */
        void showCompletedTasksCleared();

        /**
         * 显示"获取任务失败"
         */
        void showLoadingTasksError();

        /**
         * 显示"没有任务"
         */
        void showNoTasks();

        /**
         * 显示"激活"过滤标识
         */
        void showActiveFilterLabel();

        /**
         * 显示"已完成"过滤标识
         */
        void showCompletedFilterLabel();

        /**
         * 显示所有过滤标识
         */
        void showAllFilterLabel();

        /**
         * 显示"没有已激活任务"
         */
        void showNoActiveTasks();

        /**
         * 显示"没有已完成任务"
         */
        void showNoCompletedTasks();

        /**
         * 显示成功保存信息
         */
        void showSuccessfullySavedMessage();

        /**
         * 显示过滤气泡
         */
        void showFilteringPopUpMenu();

        /**
         * 是否激活
         *
         * @return
         */
        //Todo 具体用途不明
        boolean isActive();
    }
}
