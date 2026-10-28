package com.cheatbreaker.client.event;

public class StringBooleanIntEvent extends Event {
    public int intValue;
    public String text;
    public boolean flag;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public void setIntValue(int n) {
        this.intValue = n;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public StringBooleanIntEvent(int n, String str, boolean flag) {
        this.intValue = n;
        this.text = str;
        this.flag = flag;
    }
}
