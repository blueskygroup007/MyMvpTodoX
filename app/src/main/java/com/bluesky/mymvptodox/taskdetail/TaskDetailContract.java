package com.bluesky.mymvptodox.taskdetail;

import com.bluesky.mymvptodox.BasePresenter;
import com.bluesky.mymvptodox.BaseView;

/**
 * @author BlueSky
 * @date 2019/4/10
 * Description:
 */
public interface TaskDetailContract {
    interface View extends BaseView<Presenter>{

        /**
         * 是否显示loading指示器
         * @param active 显示与否
         */
        void setLoadingIndicator(boolean active);

        /**
         *
         */
        void showMissingTask();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showCompletionStatus(boolean complete);

        void showEditTask(String taskId);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();
    }

    interface Presenter extends BasePresenter{

    }
}
