package net.minecraft.util;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeBoundList {
    public ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public Class clazz;
    public Object[] data;
    public int lastReadIndex;
    public int lastWrittenIndex;

    public ThreadSafeBoundList(Class clazz, int n) {
        this.clazz = clazz;
        this.data = (Object[])Array.newInstance(clazz, n);
    }

    public Object add(Object obj) {
        this.readWriteLock.writeLock().lock();
        this.data[this.lastWrittenIndex] = obj;
        this.lastWrittenIndex = (this.lastWrittenIndex + 1) % this.size();
        if (this.lastReadIndex < this.size()) {
            this.lastReadIndex++;
        }

        this.readWriteLock.writeLock().unlock();
        return obj;
    }

    public int size() {
        this.readWriteLock.readLock().lock();
        int length = this.data.length;
        this.readWriteLock.readLock().unlock();
        return length;
    }

    public Object[] copyAndGetData() {
        Object[] newInstance = (Object[])Array.newInstance(this.clazz, this.lastReadIndex);
        this.readWriteLock.readLock().lock();

        for (int i = 0; i < this.lastReadIndex; i++) {
            int n = (this.lastWrittenIndex - this.lastReadIndex + i) % this.size();
            if (n < 0) {
                n += this.size();
            }

            newInstance[i] = this.data[n];
        }

        this.readWriteLock.readLock().unlock();
        return newInstance;
    }
}
