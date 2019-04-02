package com.bluesky.mymvptodox.data.source.local;

import android.content.Context;

import com.bluesky.mymvptodox.data.Task;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author BlueSky
 * @date 2019/3/20
 * Description:
 */
@Database(entities = {Task.class}, version = 1,exportSchema = false)
public abstract class ToDoDatabase extends RoomDatabase {
    //todo exportSchema = false 否则报错
    private static ToDoDatabase INSTANCE;

    public abstract TasksDao taskDao();

    private static final Object sLock = new Object();

    public static ToDoDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ToDoDatabase.class, "Tasks.db").build();
            }
            return INSTANCE;
        }
    }
}
