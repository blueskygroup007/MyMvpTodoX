package com.bluesky.alarmclock.data;

import java.io.Serializable;

public class Alarm implements Serializable {
    private int id;
    private String name;
    private int interval;

    public Alarm(String name, int interval) {
        this.name = name;
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", interval=" + interval +
                '}';
    }
}
