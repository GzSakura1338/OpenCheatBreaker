package com.cheatbreaker.client.mod.impl;

public class VoiceChatEntry {
    public String name;
    public boolean speaking;
    public boolean showHead;

    public VoiceChatEntry(String str, boolean flag, boolean flag2) {
        this.name = str;
        this.speaking = flag;
        this.showHead = flag2;
    }
}
