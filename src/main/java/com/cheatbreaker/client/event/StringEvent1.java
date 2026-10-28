package com.cheatbreaker.client.event;

public class StringEvent1 extends CancellableEvent {
    public String message;

    public StringEvent1(String str) {
        this.message = str;
    }

    public String getMessage() {
        return this.message;
    }
}
