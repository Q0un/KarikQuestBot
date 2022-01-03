package com.qqun.cooldown;

public class Cooldown {
    private long begin;
    private final long duration;

    Cooldown(long begin, long duration) {
        this.begin = begin;
        this.duration = duration;
    }

    public final boolean isEnded(long time) {
        return time - begin >= duration;
    }

    public final void setBegin(long time) {
        begin = time;
    }
}
