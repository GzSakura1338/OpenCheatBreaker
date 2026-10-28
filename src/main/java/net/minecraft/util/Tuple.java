package net.minecraft.util;

public class Tuple {
    public Object first;
    public Object second;

    public Tuple(Object obj, Object obj2) {
        this.first = obj;
        this.second = obj2;
    }

    public Object getFirst() {
        return this.first;
    }

    public Object getSecond() {
        return this.second;
    }
}
