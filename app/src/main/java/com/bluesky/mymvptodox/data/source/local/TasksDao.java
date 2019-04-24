package com.bluesky.mymvptodox.data.source.local;

import com.bluesky.mymvptodox.data.Task;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * @author BlueSky
 * @date 2019/3/20
 * Description:
 */
@Dao
public interface TasksDao {

    /**
     * 查询所有tasks
     *
     * @return
     */
    @Query("SELECT * FROM task")
    List<Task> getTasks();

    @Query("SELECT * FROM task WHERE id=:taskId")
    Task getTaskById(String taskId);

    /**
     * 插入一个task,如果已经存在,则覆盖(这也是REPLACE的作用)
     *
     * @param task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    /**
     * 更新一个task
     *
     * @param task
     * @return 被更新的task数量, 总为1.
     */
    @Update
    int updateTask(Task... task);

    /**
     * 更新一个task的completed状态
     *
     * @param taskId
     * @param completed
     */
    @Query("UPDATE task SET completed=:completed WHERE id=:taskId")
    void updateCompleted(String taskId, boolean completed);

    /**
     * 删除所有task
     */
    @Delete
    void deleteTasks(List<Task> tasks);

    /**
     * 删除所有task(使用Query注解)
     */
    @Query("DELETE FROM Task")
    void deleteTasks();

    /**
     * 删除指定id的task
     *
     * @param taskId
     * @return
     */
    @Query("DELETE FROM task WHERE id=:taskId")
    int deleteTaskById(String taskId);

    /**
     * 删除所有completed为真的tasks
     *
     * @return
     */
    @Query("DELETE FROM task WHERE completed=1")
    int deleteCompletedTasks();
    //TODO 源码这里completed=1,1是sqlite中boolean的true的替代值
    //TODO 这里的sqlite命令,等号两边都没有加空格

}
