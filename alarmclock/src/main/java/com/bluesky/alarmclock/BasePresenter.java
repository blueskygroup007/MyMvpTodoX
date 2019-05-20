package com.bluesky.alarmclock;

public interface BasePresenter<T> {
    /**
     * P的启动函数
     */
    void start();


    /**
     * 设置V
     */
    void setView(T view);
}
