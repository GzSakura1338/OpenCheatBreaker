package net.minecraft.server.management;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LowerStringMap implements Map {
    public Map internalMap = new LinkedHashMap();

    @Override
    public int size() {
        return this.internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object obj) {
        return this.internalMap.containsKey(obj.toString().toLowerCase());
    }

    @Override
    public boolean containsValue(Object obj) {
        return this.internalMap.containsKey(obj);
    }

    @Override
    public Object get(Object obj) {
        return this.internalMap.get(obj.toString().toLowerCase());
    }

    public Object put(String str, Object obj) {
        return this.internalMap.put(str.toLowerCase(), obj);
    }

    @Override
    public Object remove(Object obj) {
        return this.internalMap.remove(obj.toString().toLowerCase());
    }

    @Override
    public void putAll(Map map) {
        for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            this.put((String)entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.internalMap.clear();
    }

    @Override
    public Set keySet() {
        return this.internalMap.keySet();
    }

    @Override
    public Collection values() {
        return this.internalMap.values();
    }

    @Override
    public Set entrySet() {
        return this.internalMap.entrySet();
    }

    @Override
    public Object put(Object obj, Object obj2) {
        return this.put((String)obj, obj2);
    }
}
