package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
   public boolean isBlank = true;
   public Map<Integer, DataWatcher.WatchableObject> watchedObjects = Maps.newHashMap();
   public static Map<Class<?>, Integer> dataTypes = Maps.newHashMap();
   public ReadWriteLock lock = new ReentrantReadWriteLock();
   public boolean objectChanged;
   public BiomeGenBase spawnBiome = BiomeGenBase.plains;
   public BlockPos spawnPosition = BlockPos.ORIGIN;
   public Entity owner;

   public DataWatcher(Entity owner) {
      this.owner = owner;
   }

   public <T> void addObject(int id, T object) {
      Integer integer = dataTypes.get(object.getClass());
      if (integer == null) {
         throw new IllegalArgumentException("Unknown data type: " + object.getClass());
      }

      if (id > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
      }

      if (this.watchedObjects.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id value for " + id + "!");
      }

      DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(integer, id, object);
      this.lock.writeLock().lock();
      this.watchedObjects.put(id, watchableObject);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public void addObjectByDataType(int id, int type) {
      DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(type, id, null);
      this.lock.writeLock().lock();
      this.watchedObjects.put(id, watchableObject);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public byte getWatchableObjectByte(int id) {
      return (Byte)this.getWatchedObject(id).getObject();
   }

   public short getWatchableObjectShort(int id) {
      return (Short)this.getWatchedObject(id).getObject();
   }

   public int getWatchableObjectInt(int id) {
      return (Integer)this.getWatchedObject(id).getObject();
   }

   public float getWatchableObjectFloat(int id) {
      return (Float)this.getWatchedObject(id).getObject();
   }

   public String getWatchableObjectString(int id) {
      return (String)this.getWatchedObject(id).getObject();
   }

   public ItemStack getWatchableObjectItemStack(int id) {
      return (ItemStack)this.getWatchedObject(id).getObject();
   }

   public DataWatcher.WatchableObject getWatchedObject(int id) {
      this.lock.readLock().lock();

      DataWatcher.WatchableObject watchableObject;
      try {
         watchableObject = this.watchedObjects.get(id);
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Getting synched entity data");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Synched entity data");
         makeCategory.addCrashSection("Data ID", id);
         throw new ReportedException(makeCrashReport);
      }

      this.lock.readLock().unlock();
      return watchableObject;
   }

   public Rotations getWatchableObjectRotations(int id) {
      return (Rotations)this.getWatchedObject(id).getObject();
   }

   public <T> void updateObject(int id, T newData) {
      DataWatcher.WatchableObject watchedObject = this.getWatchedObject(id);
      if (ObjectUtils.notEqual(newData, watchedObject.getObject())) {
         watchedObject.setObject(newData);
         this.owner.onDataWatcherUpdate(id);
         watchedObject.setWatched(true);
         this.objectChanged = true;
      }
   }

   public void setObjectWatched(int id) {
      this.getWatchedObject(id).watched = true;
      this.objectChanged = true;
   }

   public boolean hasObjectChanged() {
      return this.objectChanged;
   }

   public static void writeWatchedListToPacketBuffer(List<DataWatcher.WatchableObject> objectsList, PacketBuffer buffer) throws IOException {
      if (objectsList != null) {
         for (DataWatcher.WatchableObject watchableObject : objectsList) {
            writeWatchableObjectToPacketBuffer(buffer, watchableObject);
         }
      }

      buffer.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> getChanged() {
      ArrayList list = null;
      if (this.objectChanged) {
         this.lock.readLock().lock();

         for (DataWatcher.WatchableObject watchableObject : this.watchedObjects.values()) {
            if (watchableObject.isWatched()) {
               watchableObject.setWatched(false);
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(watchableObject);
            }
         }

         this.lock.readLock().unlock();
      }

      this.objectChanged = false;
      return list;
   }

   public void writeTo(PacketBuffer buffer) throws IOException {
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject watchableObject : this.watchedObjects.values()) {
         writeWatchableObjectToPacketBuffer(buffer, watchableObject);
      }

      this.lock.readLock().unlock();
      buffer.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> getAllWatched() {
      ArrayList list = null;
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject watchableObject : this.watchedObjects.values()) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         list.add(watchableObject);
      }

      this.lock.readLock().unlock();
      return list;
   }

   public static void writeWatchableObjectToPacketBuffer(PacketBuffer buffer, DataWatcher.WatchableObject object) throws IOException {
      int n = (object.getObjectType() << 5 | object.getDataValueId() & 31) & 0xFF;
      buffer.writeByte(n);
      switch (object.getObjectType()) {
         case 0:
            buffer.writeByte((Byte)object.getObject());
            break;
         case 1:
            buffer.writeShort((Short)object.getObject());
            break;
         case 2:
            buffer.writeInt((Integer)object.getObject());
            break;
         case 3:
            buffer.writeFloat((Float)object.getObject());
            break;
         case 4:
            buffer.writeString((String)object.getObject());
            break;
         case 5:
            ItemStack object2 = (ItemStack)object.getObject();
            buffer.writeItemStackToBuffer(object2);
            break;
         case 6:
            BlockPos object3 = (BlockPos)object.getObject();
            buffer.writeInt(object3.getX());
            buffer.writeInt(object3.getY());
            buffer.writeInt(object3.getZ());
            break;
         case 7:
            Rotations object4 = (Rotations)object.getObject();
            buffer.writeFloat(object4.getX());
            buffer.writeFloat(object4.getY());
            buffer.writeFloat(object4.getZ());
      }
   }

   public static List<DataWatcher.WatchableObject> readWatchedListFromPacketBuffer(PacketBuffer buffer) throws IOException {
      ArrayList list = null;

      for (byte readByte = buffer.readByte(); readByte != 127; readByte = buffer.readByte()) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         int n = (readByte & 224) >> 5;
         int n2 = readByte & 31;
         DataWatcher.WatchableObject watchableObject = null;
         switch (n) {
            case 0:
               watchableObject = new DataWatcher.WatchableObject(n, n2, buffer.readByte());
               break;
            case 1:
               watchableObject = new DataWatcher.WatchableObject(n, n2, buffer.readShort());
               break;
            case 2:
               watchableObject = new DataWatcher.WatchableObject(n, n2, buffer.readInt());
               break;
            case 3:
               watchableObject = new DataWatcher.WatchableObject(n, n2, buffer.readFloat());
               break;
            case 4:
               watchableObject = new DataWatcher.WatchableObject(n, n2, buffer.readStringFromBuffer(32767));
               break;
            case 5:
               watchableObject = new DataWatcher.WatchableObject(n, n2, buffer.readItemStackFromBuffer());
               break;
            case 6:
               int readInt = buffer.readInt();
               int readInt2 = buffer.readInt();
               int readInt3 = buffer.readInt();
               watchableObject = new DataWatcher.WatchableObject(n, n2, new BlockPos(readInt, readInt2, readInt3));
               break;
            case 7:
               float readFloat = buffer.readFloat();
               float readFloat2 = buffer.readFloat();
               float readFloat3 = buffer.readFloat();
               watchableObject = new DataWatcher.WatchableObject(n, n2, new Rotations(readFloat, readFloat2, readFloat3));
         }

         list.add(watchableObject);
      }

      return list;
   }

   public void updateWatchedObjectsFromList(List<DataWatcher.WatchableObject> list) {
      this.lock.writeLock().lock();

      for (DataWatcher.WatchableObject watchableObject : list) {
         DataWatcher.WatchableObject watchableObject2 = this.watchedObjects.get(watchableObject.getDataValueId());
         if (watchableObject2 != null) {
            watchableObject2.setObject(watchableObject.getObject());
            this.owner.onDataWatcherUpdate(watchableObject.getDataValueId());
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
      dataTypes.put(BlockPos.class, 6);
      dataTypes.put(Rotations.class, 7);
   }

   public static class WatchableObject {
      public int dataValueId;
      public Object watchedObject;
      public int objectType;
      public boolean watched;

      public WatchableObject(int type, int id, Object object) {
         this.dataValueId = id;
         this.watchedObject = object;
         this.objectType = type;
         this.watched = true;
      }

      public int getDataValueId() {
         return this.dataValueId;
      }

      public void setObject(Object object) {
         this.watchedObject = object;
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

      public void setWatched(boolean watched) {
         this.watched = watched;
      }
   }
}
