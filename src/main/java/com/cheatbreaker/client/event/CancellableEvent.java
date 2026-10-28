package com.cheatbreaker.client.event;

public class CancellableEvent extends Event {
    public boolean cancelled = false;

    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}
