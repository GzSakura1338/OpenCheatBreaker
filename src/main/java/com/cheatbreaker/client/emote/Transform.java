package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.Easing;
import net.minecraft.nbt.NBTTagCompound;

public class Transform {
    public float z;
    public String name = "";
    public float rotationX;
    public float y;
    public float x;
    public float rotationY;
    public float scaleX = 1.0F;
    public float scaleY = 1.0F;
    public float scaleZ = 1.0F;
    public float rotationZ;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transform)) {
            return super.equals(obj);
        }

        Transform transform = (Transform)obj;
        boolean flag = transform.x == this.x && transform.y == this.y && transform.z == this.z;
        flag = flag && transform.scaleX == this.scaleX && transform.scaleY == this.scaleY && transform.scaleZ == this.scaleZ;
        return flag && transform.rotationX == this.rotationX && transform.rotationY == this.rotationY && transform.rotationZ == this.rotationZ;
    }

    public void interpolate(Transform transform, Transform transform2, float f, Easing easing) {
        this.x = easing.ease(transform.x, transform2.x, f);
        this.y = easing.ease(transform.y, transform2.y, f);
        this.z = easing.ease(transform.z, transform2.z, f);
        this.scaleX = easing.ease(transform.scaleX, transform2.scaleX, f);
        this.scaleY = easing.ease(transform.scaleY, transform2.scaleY, f);
        this.scaleZ = easing.ease(transform.scaleZ, transform2.scaleZ, f);
        this.rotationX = easing.ease(transform.rotationX, transform2.rotationX, f);
        this.rotationY = easing.ease(transform.rotationY, transform2.rotationY, f);
        this.rotationZ = easing.ease(transform.rotationZ, transform2.rotationZ, f);
    }

    public Transform(String str) {
        this.name = str;
    }

    public Transform clone() {
        Transform transform = new Transform(this.name);
        transform.x = this.x;
        transform.y = this.y;
        transform.z = this.z;
        transform.scaleX = this.scaleX;
        transform.scaleY = this.scaleY;
        transform.scaleZ = this.scaleZ;
        transform.rotationX = this.rotationX;
        transform.rotationY = this.rotationY;
        transform.rotationZ = this.rotationZ;
        return transform;
    }

    public NBTTagCompound writeToNbt(NBTTagCompound nbttagCompound) {
        if (nbttagCompound == null) {
            nbttagCompound = new NBTTagCompound();
        }

        if (this.x != 0.0F) {
            nbttagCompound.setFloat("X", this.x);
        }

        if (this.y != 0.0F) {
            nbttagCompound.setFloat("Y", this.y);
        }

        if (this.z != 0.0F) {
            nbttagCompound.setFloat("Z", this.z);
        }

        if (this.scaleX != 1.0F) {
            nbttagCompound.setFloat("SX", this.scaleX);
        }

        if (this.scaleY != 1.0F) {
            nbttagCompound.setFloat("SY", this.scaleY);
        }

        if (this.scaleZ != 1.0F) {
            nbttagCompound.setFloat("SZ", this.scaleZ);
        }

        if (this.rotationX != 0.0F) {
            nbttagCompound.setFloat("RX", this.rotationX);
        }

        if (this.rotationY != 0.0F) {
            nbttagCompound.setFloat("RY", this.rotationY);
        }

        if (this.rotationZ != 0.0F) {
            nbttagCompound.setFloat("RZ", this.rotationZ);
        }

        return nbttagCompound;
    }

    public void readFromNbt(NBTTagCompound nbttagCompound) {
        if (nbttagCompound.hasKey("X")) {
            this.x = nbttagCompound.getFloat("X");
        }

        if (nbttagCompound.hasKey("Y")) {
            this.y = nbttagCompound.getFloat("Y");
        }

        if (nbttagCompound.hasKey("Z")) {
            this.z = nbttagCompound.getFloat("Z");
        }

        if (nbttagCompound.hasKey("SX")) {
            this.scaleX = nbttagCompound.getFloat("SX");
        }

        if (nbttagCompound.hasKey("SY")) {
            this.scaleY = nbttagCompound.getFloat("SY");
        }

        if (nbttagCompound.hasKey("SZ")) {
            this.scaleZ = nbttagCompound.getFloat("SZ");
        }

        if (nbttagCompound.hasKey("RX")) {
            this.rotationX = nbttagCompound.getFloat("RX");
        }

        if (nbttagCompound.hasKey("RY")) {
            this.rotationY = nbttagCompound.getFloat("RY");
        }

        if (nbttagCompound.hasKey("RZ")) {
            this.rotationZ = nbttagCompound.getFloat("RZ");
        }
    }
}
