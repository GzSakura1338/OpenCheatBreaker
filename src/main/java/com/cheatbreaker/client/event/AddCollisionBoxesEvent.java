package com.cheatbreaker.client.event;

import java.util.List;
import net.minecraft.util.AxisAlignedBB;

public class AddCollisionBoxesEvent extends CancellableEvent {
    public List<AxisAlignedBB> axisAlignedBBList;
    public double x;
    public double y;
    public double z;

    public List<AxisAlignedBB> getAxisAlignedBBList() {
        return this.axisAlignedBBList;
    }

    public double getY() {
        return this.y;
    }

    public AddCollisionBoxesEvent(List<AxisAlignedBB> list, double d, double d2, double d3) {
        this.axisAlignedBBList = list;
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    public double getZ() {
        return this.z;
    }

    public double getX() {
        return this.x;
    }
}
