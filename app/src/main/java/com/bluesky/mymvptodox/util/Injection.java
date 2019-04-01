package com.bluesky.mymvptodox.util;

import android.content.Context;

import com.bluesky.mymvptodox.data.source.TasksRepository;
import com.bluesky.mymvptodox.data.source.local.TaskLocalDataSource;
import com.bluesky.mymvptodox.data.source.local.ToDoDatabase;
import com.bluesky.mymvptodox.data.source.remote.TaskRemoteDataSource;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * @author BlueSky
 * @date 2019/3/24
 * Description:
 */
public class Injection {

    public static TasksRepository provideTasksRepository(Context context) {
        checkNotNull(context);

        ToDoDatabase database = ToDoDatabase.getInstance(context);
        return TasksRepository.getInstance(TaskRemoteDataSource.getInstance(),
                TaskLocalDataSource.getInstance(database.taskDao(), new AppExecutors()));
    }
}
