package com.bluesky.mymvptodox.data;

import com.google.common.base.Strings;

import java.util.Objects;
import java.util.UUID;

/**
 * @author BlueSky
 * @date 2019/3/4
 * Description: 一个任务类的描述
 */
public final class Task {

    private final String mId;
    private final String mTitle;
    private final String mDescription;
    private final boolean mCompleted;

    public Task(String title, String description) {
        this(UUID.randomUUID().toString(), title, description, false);
    }

    public Task(String id, String title, String description) {
        this(id, title, description, false);
    }

    public Task(String title, String description, boolean completed) {
        this(UUID.randomUUID().toString(), title, description, completed);
    }

    public Task(String id, String title, String description, boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    /**
     * 如何title为空,那么返回desc
     *
     * @return
     */
    public String getTitleForList() {
        if (Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    /**
     * 没有完成,就是活动状态
     *
     * @return
     */
    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) && Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public String toString() {
        return "Task with title " + mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(mId, task.mId) &&
                Objects.equals(mTitle, task.mTitle) &&
                Objects.equals(mDescription, task.mDescription);
    }

    /**
     * 源码中,使用的objects类是guava里的com.google.com.base
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mDescription);
    }
}