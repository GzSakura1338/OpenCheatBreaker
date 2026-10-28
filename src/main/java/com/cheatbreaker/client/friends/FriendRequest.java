package com.cheatbreaker.client.friends;

public class FriendRequest {
    public String text;
    public String text2;
    public boolean flag;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public FriendRequest(String str, String str2, boolean flag) {
        this.text = str;
        this.text2 = str2;
        this.flag = flag;
    }

    public FriendRequest(String str, String str2) {
        this.text = str;
        this.text2 = str2;
    }

    public String getText2() {
        return this.text2;
    }

    public String getText() {
        return this.text;
    }
}
