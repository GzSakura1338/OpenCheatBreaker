package com.cheatbreaker.client.mod.impl;

import java.awt.Color;
import net.minecraft.util.Vec3;

public class TeamMember {
    public boolean leader = false;
    public String playerId;
    public Vec3 vec3;
    public long updatedAt;
    public Color color;
    public long updateInterval;

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public boolean isLeader() {
        return this.leader;
    }

    public Color getColor() {
        return this.color;
    }

    public void updatePosition(double d, double d2, double d3, long l) {
        this.vec3 = new Vec3(d, d2, d3);
        this.updatedAt = System.currentTimeMillis();
        this.updateInterval = l;
    }

    public void setUpdatedAt(long l) {
        this.updatedAt = l;
    }

    public TeamMember(String str, boolean flag) {
        this.playerId = str;
        this.leader = flag;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setVec3(Vec3 vec3) {
        this.vec3 = vec3;
    }

    public TeamMember(String str, boolean flag, Vec3 vec3, long l, Color color, long l2) {
        this.playerId = str;
        this.leader = flag;
        this.vec3 = vec3;
        this.updatedAt = l;
        this.color = color;
        this.updateInterval = l2;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPlayerId(String str) {
        this.playerId = str;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void setUpdateInterval(long l) {
        this.updateInterval = l;
    }

    public void setLeader(boolean flag) {
        this.leader = flag;
    }

    public Vec3 getVec3() {
        return this.vec3;
    }

    public long getUpdateInterval() {
        return this.updateInterval;
    }
}
