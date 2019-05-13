package com.bluesky.alarmclock.data;

public class Alarm {
    private String name;
    private int interval;

    public Alarm(String name, int interval) {
        this.name = name;
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
