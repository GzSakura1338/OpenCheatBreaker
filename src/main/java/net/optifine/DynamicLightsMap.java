package net.optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicLightsMap {
    public Map<Integer, DynamicLight> map = new HashMap<>();
    public List<DynamicLight> list = new ArrayList<>();
    public boolean dirty = false;

    public DynamicLight put(int n, DynamicLight dynamicLight) {
        DynamicLight dynamicLight2 = this.map.put(n, dynamicLight);
        this.setDirty();
        return dynamicLight2;
    }

    public DynamicLight get(int n) {
        return this.map.get(n);
    }

    public int size() {
        return this.map.size();
    }

    public DynamicLight remove(int n) {
        DynamicLight dynamicLight = this.map.remove(n);
        if (dynamicLight != null) {
            this.setDirty();
        }

        return dynamicLight;
    }

    public void clear() {
        this.map.clear();
        this.setDirty();
    }

    public void setDirty() {
        this.dirty = true;
    }

    public List<DynamicLight> valueList() {
        if (this.dirty) {
            this.list.clear();
            this.list.addAll(this.map.values());
            this.dirty = false;
        }

        return this.list;
    }
}
