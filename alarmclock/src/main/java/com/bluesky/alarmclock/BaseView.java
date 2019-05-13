package com.bluesky.alarmclock;

public interface BaseView<T extends BasePresenter> {
    /**
     * 给V指定P
     *
     * @param presenter
     */
    void setPresenter(T presenter);
}
