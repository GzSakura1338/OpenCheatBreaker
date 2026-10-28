package com.cheatbreaker.client.manager;

import java.util.List;

public class BlockedServer {
    public String text;
    public List<String> domains;
    public String text2;
    public BlockedServerLevel blockedServerLevel;
    public int intValue;
    public boolean flag;
    public String text3;

    public int getIntValue() {
        return this.intValue;
    }

    public String getText2() {
        return this.text2;
    }

    public String getText3() {
        return this.text3;
    }

    public List<String> getDomains() {
        return this.domains;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public BlockedServer(String str, List<String> list, String str2, BlockedServerLevel blockedServerLevel, int n, boolean flag, String str3) {
        this.text = str;
        this.domains = list;
        this.text2 = str2;
        this.blockedServerLevel = blockedServerLevel;
        this.intValue = n;
        this.flag = flag;
        this.text3 = str3;
    }

    public BlockedServerLevel getBlockedServerLevel() {
        return this.blockedServerLevel;
    }

    public String getText() {
        return this.text;
    }
}
