package com.cheatbreaker.client.event;

public class StringByteArrayEvent extends Event {
    public String text;
    public byte[] bytes;

    public String getText() {
        return this.text;
    }

    public StringByteArrayEvent(String str, byte[] bytes) {
        this.text = str;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }
}
