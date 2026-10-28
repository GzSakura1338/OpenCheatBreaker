package net.minecraft.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ReportedException;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
    public boolean isBlank = true;
    public Map watchedObjects = new HashMap();
    public static HashMap dataTypes = new HashMap();
    public ReadWriteLock lock = new ReentrantReadWriteLock();
    public boolean objectChanged;
    public Entity entity;

    public DataWatcher(Entity entity) {
        this.entity = entity;
    }

    public void addObject(int n, Object obj) {
        Integer integer = (Integer)dataTypes.get(obj.getClass());
        if (integer == null) {
            throw new IllegalArgumentException("Unknown data type: " + obj.getClass());
        }

        if (n > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + n + "! (Max is " + 31 + ")");
        }

        if (this.watchedObjects.containsKey(n)) {
            throw new IllegalArgumentException("Duplicate id value for " + n + "!");
        }

        DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(integer, n, obj);
        this.lock.writeLock().lock();
        this.watchedObjects.put(n, watchableObject);
        this.lock.writeLock().unlock();
        this.isBlank = false;
    }

    public void addObjectByDataType(int n, int n2) {
        DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(n2, n, null);
        this.lock.writeLock().lock();
        this.watchedObjects.put(n, watchableObject);
        this.lock.writeLock().unlock();
        this.isBlank = false;
    }

    public byte getWatchableObjectByte(int n) {
        return (Byte)this.getWatchedObject(n).getObject();
    }

    public short getWatchableObjectShort(int n) {
        return (Short)this.getWatchedObject(n).getObject();
    }

    public int getWatchableObjectInt(int n) {
        return (Integer)this.getWatchedObject(n).getObject();
    }

    public float getWatchableObjectFloat(int n) {
        return (Float)this.getWatchedObject(n).getObject();
    }

    public String getWatchableObjectString(int n) {
        try {
            return (String)this.getWatchedObject(n).getObject();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return "";
        }
    }

    public ItemStack getWatchableObjectItemStack(int n) {
        return (ItemStack)this.getWatchedObject(n).getObject();
    }

    public DataWatcher.WatchableObject getWatchedObject(int n) {
        this.lock.readLock().lock();

        DataWatcher.WatchableObject watchableObject;
        try {
            watchableObject = (DataWatcher.WatchableObject)this.watchedObjects.get(n);
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Getting synched entity data");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Synched entity data");
            makeCategory.addCrashSection("Data ID", n);
            throw new ReportedException(makeCrashReport);
        }

        this.lock.readLock().unlock();
        return watchableObject;
    }

    public void updateObject(int n, Object obj) {
        DataWatcher.WatchableObject watchedObject = this.getWatchedObject(n);
        if (ObjectUtils.notEqual(obj, watchedObject.getObject())) {
            watchedObject.setObject(obj);
            this.entity.process(n);
            watchedObject.setWatched(true);
            this.objectChanged = true;
        }
    }

    public void setObjectWatched(int n) {
        this.getWatchedObject(n).watched = true;
        this.objectChanged = true;
    }

    public boolean hasObjectChanged() {
        return this.objectChanged;
    }

    public static void writeWatchedListToPacketBuffer(List list, PacketBuffer packetBuffer) throws IOException {
        if (list != null) {
            for (DataWatcher.WatchableObject watchableObject : (Iterable<DataWatcher.WatchableObject>)list) {
                writeWatchableObjectToPacketBuffer(packetBuffer, watchableObject);
            }
        }

        packetBuffer.writeByte(127);
    }

    public List getChanged() {
        ArrayList list = null;
        if (this.objectChanged) {
            this.lock.readLock().lock();

            for (DataWatcher.WatchableObject watchableObject : (Iterable<DataWatcher.WatchableObject>)this.watchedObjects.values()) {
                if (watchableObject.isWatched()) {
                    watchableObject.setWatched(false);
                    if (list == null) {
                        list = new ArrayList();
                    }

                    list.add(watchableObject);
                }
            }

            this.lock.readLock().unlock();
        }

        this.objectChanged = false;
        return list;
    }

    public void write(PacketBuffer packetBuffer) throws IOException {
        this.lock.readLock().lock();

        for (DataWatcher.WatchableObject watchableObject : (Iterable<DataWatcher.WatchableObject>)this.watchedObjects.values()) {
            writeWatchableObjectToPacketBuffer(packetBuffer, watchableObject);
        }

        this.lock.readLock().unlock();
        packetBuffer.writeByte(127);
    }

    public List getAllWatched() {
        ArrayList list = null;
        this.lock.readLock().lock();

        for (DataWatcher.WatchableObject watchableObject : (Iterable<DataWatcher.WatchableObject>)this.watchedObjects.values()) {
            if (list == null) {
                list = new ArrayList();
            }

            list.add(watchableObject);
        }

        this.lock.readLock().unlock();
        return list;
    }

    public static void writeWatchableObjectToPacketBuffer(PacketBuffer packetBuffer, DataWatcher.WatchableObject watchableObject) throws IOException {
        int n = (watchableObject.getObjectType() << 5 | watchableObject.getDataValueId() & 31) & 0xFF;
        packetBuffer.writeByte(n);
        switch (watchableObject.getObjectType()) {
            case 0:
                packetBuffer.writeByte((Byte)watchableObject.getObject());
                break;
            case 1:
                packetBuffer.writeShort((Short)watchableObject.getObject());
                break;
            case 2:
                packetBuffer.writeInt((Integer)watchableObject.getObject());
                break;
            case 3:
                packetBuffer.writeFloat((Float)watchableObject.getObject());
                break;
            case 4:
                packetBuffer.writeStringToBuffer((String)watchableObject.getObject());
                break;
            case 5:
                ItemStack object = (ItemStack)watchableObject.getObject();
                packetBuffer.writeItemStackToBuffer(object);
                break;
            case 6:
                ChunkCoordinates object2 = (ChunkCoordinates)watchableObject.getObject();
                packetBuffer.writeInt(object2.posX);
                packetBuffer.writeInt(object2.posY);
                packetBuffer.writeInt(object2.posZ);
        }
    }

    public static List readWatchedListFromPacketBuffer(PacketBuffer packetBuffer) throws IOException {
        ArrayList list = null;

        for (byte readByte = packetBuffer.readByte(); readByte != 127; readByte = packetBuffer.readByte()) {
            if (list == null) {
                list = new ArrayList();
            }

            int n = (readByte & 224) >> 5;
            int n2 = readByte & 31;
            DataWatcher.WatchableObject watchableObject = null;
            switch (n) {
                case 0:
                    watchableObject = new DataWatcher.WatchableObject(n, n2, packetBuffer.readByte());
                    break;
                case 1:
                    watchableObject = new DataWatcher.WatchableObject(n, n2, packetBuffer.readShort());
                    break;
                case 2:
                    watchableObject = new DataWatcher.WatchableObject(n, n2, packetBuffer.readInt());
                    break;
                case 3:
                    watchableObject = new DataWatcher.WatchableObject(n, n2, packetBuffer.readFloat());
                    break;
                case 4:
                    watchableObject = new DataWatcher.WatchableObject(n, n2, packetBuffer.readStringFromBuffer(32767));
                    break;
                case 5:
                    watchableObject = new DataWatcher.WatchableObject(n, n2, packetBuffer.readItemStackFromBuffer());
                    break;
                case 6:
                    int readInt = packetBuffer.readInt();
                    int readInt2 = packetBuffer.readInt();
                    int readInt3 = packetBuffer.readInt();
                    watchableObject = new DataWatcher.WatchableObject(n, n2, new ChunkCoordinates(readInt, readInt2, readInt3));
            }

            list.add(watchableObject);
        }

        return list;
    }

    public void updateWatchedObjectsFromList(List list) {
        this.lock.writeLock().lock();

        for (DataWatcher.WatchableObject watchableObject : (Iterable<DataWatcher.WatchableObject>)list) {
            DataWatcher.WatchableObject watchableObject2 = (DataWatcher.WatchableObject)this.watchedObjects.get(watchableObject.getDataValueId());
            if (watchableObject2 != null) {
                watchableObject2.setObject(watchableObject.getObject());
                this.entity.process(watchableObject.getDataValueId());
            }
        }

        this.lock.writeLock().unlock();
        this.objectChanged = true;
    }

    public boolean getIsBlank() {
        return this.isBlank;
    }

    public void clearDirty() {
        this.objectChanged = false;
    }

    static {
        dataTypes.put(Byte.class, 0);
        dataTypes.put(Short.class, 1);
        dataTypes.put(Integer.class, 2);
        dataTypes.put(Float.class, 3);
        dataTypes.put(String.class, 4);
        dataTypes.put(ItemStack.class, 5);
        dataTypes.put(ChunkCoordinates.class, 6);
    }

    public static class WatchableObject {
        public int dataValueId;
        public Object watchedObject;
        public int objectType;
        public boolean watched;

        public WatchableObject(int n, int n2, Object obj) {
            this.dataValueId = n2;
            this.watchedObject = obj;
            this.objectType = n;
            this.watched = true;
        }

        public int getDataValueId() {
            return this.dataValueId;
        }

        public void setObject(Object obj) {
            this.watchedObject = obj;
        }

        public Object getObject() {
            return this.watchedObject;
        }

        public int getObjectType() {
            return this.objectType;
        }

        public boolean isWatched() {
            return this.watched;
        }

        public void setWatched(boolean flag) {
            this.watched = flag;
        }
    }
}
