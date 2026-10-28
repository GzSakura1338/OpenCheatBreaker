package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap implements IObjectIntIterable {
    public IdentityHashMap idMap = new IdentityHashMap(512);
    public List list = Lists.newArrayList();

    public void set(Object obj, int n) {
        this.idMap.put(obj, n);

        while (this.list.size() <= n) {
            this.list.add(null);
        }

        this.list.set(n, obj);
    }

    public int getId(Object obj) {
        Integer integer = (Integer)this.idMap.get(obj);
        return integer == null ? -1 : integer;
    }

    public Object fromId(int n) {
        return n >= 0 && n < this.list.size() ? this.list.get(n) : null;
    }

    @Override
    public Iterator iterator() {
        return Iterators.filter(this.list.iterator(), Predicates.notNull());
    }

    public boolean hasId(int n) {
        return this.fromId(n) != null;
    }
}
