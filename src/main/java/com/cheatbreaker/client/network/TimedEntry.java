package com.cheatbreaker.client.network;

public class TimedEntry {
    public long removeAt = -1L;
    public String key;

    public String getKey() {
        return this.key;
    }

    public TimedEntry(String str, boolean flag) {
        this.key = str;
        if (!flag) {
            this.removeAt = System.currentTimeMillis();
        }
    }

    public void setRemoveAt(long l) {
        this.removeAt = l;
    }

    public boolean isExpired() {
        return this.removeAt != -1L && this.removeAt + 2500L < System.currentTimeMillis();
    }
}
