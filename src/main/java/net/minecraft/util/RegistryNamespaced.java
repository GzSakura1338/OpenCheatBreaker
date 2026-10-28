package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced extends RegistrySimple implements IObjectIntIterable {
    public ObjectIntIdentityMap underlyingIntegerMap = new ObjectIntIdentityMap();
    public Map objects = ((BiMap)this.registryObjects).inverse();

    public void addObject(int n, String str, Object obj) {
        this.underlyingIntegerMap.set(obj, n);
        this.putObject(ensureNamespaced(str), obj);
    }

    @Override
    public Map createUnderlyingMap() {
        return HashBiMap.create();
    }

    public Object getObject(String str) {
        return super.getObject(ensureNamespaced(str));
    }

    public String getNameForObject(Object obj) {
        return (String)this.objects.get(obj);
    }

    public boolean containsKey(String str) {
        return super.containsKey(ensureNamespaced(str));
    }

    public int getIDForObject(Object obj) {
        return this.underlyingIntegerMap.getId(obj);
    }

    public Object getObjectById(int n) {
        return this.underlyingIntegerMap.fromId(n);
    }

    @Override
    public Iterator iterator() {
        return this.underlyingIntegerMap.iterator();
    }

    public boolean containsId(int n) {
        return this.underlyingIntegerMap.hasId(n);
    }

    public static String ensureNamespaced(String str) {
        return str.indexOf(58) == -1 ? "minecraft:" + str : str;
    }

    @Override
    public boolean containsKey(Object obj) {
        return this.containsKey((String)obj);
    }

    @Override
    public Object getObject(Object obj) {
        return this.getObject((String)obj);
    }
}
