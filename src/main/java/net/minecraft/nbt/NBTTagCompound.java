package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class NBTTagCompound extends NBTBase {
   public Map<String, NBTBase> tagMap = Maps.newHashMap();

   @Override
   public void write(DataOutput output) throws IOException {
      for (String str : this.tagMap.keySet()) {
         NBTBase nbtbase = this.tagMap.get(str);
         writeEntry(str, nbtbase, output);
      }

      output.writeByte(0);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(384L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagMap.clear();

      byte b;
      while ((b = readType(input, sizeTracker)) != 0) {
         String readKey2 = readKey(input, sizeTracker);
         sizeTracker.read(224 + 16 * readKey2.length());
         NBTBase readNBT2 = readNBT(b, readKey2, input, depth + 1, sizeTracker);
         if (this.tagMap.put(readKey2, readNBT2) != null) {
            sizeTracker.read(288L);
         }
      }
   }

   public Set<String> getKeySet() {
      return this.tagMap.keySet();
   }

   @Override
   public byte getId() {
      return 10;
   }

   public void setTag(String key, NBTBase value) {
      this.tagMap.put(key, value);
   }

   public void setByte(String key, byte value) {
      this.tagMap.put(key, new NBTTagByte(value));
   }

   public void setShort(String key, short value) {
      this.tagMap.put(key, new NBTTagShort(value));
   }

   public void setInteger(String key, int value) {
      this.tagMap.put(key, new NBTTagInt(value));
   }

   public void setLong(String key, long value) {
      this.tagMap.put(key, new NBTTagLong(value));
   }

   public void setFloat(String key, float value) {
      this.tagMap.put(key, new NBTTagFloat(value));
   }

   public void setDouble(String key, double value) {
      this.tagMap.put(key, new NBTTagDouble(value));
   }

   public void setString(String key, String value) {
      this.tagMap.put(key, new NBTTagString(value));
   }

   public void setByteArray(String key, byte[] value) {
      this.tagMap.put(key, new NBTTagByteArray(value));
   }

   public void setIntArray(String key, int[] value) {
      this.tagMap.put(key, new NBTTagIntArray(value));
   }

   public void setBoolean(String key, boolean value) {
      this.setByte(key, (byte)(value ? 1 : 0));
   }

   public NBTBase getTag(String key) {
      return this.tagMap.get(key);
   }

   public byte getTagId(String key) {
      NBTBase nbtbase = this.tagMap.get(key);
      return nbtbase != null ? nbtbase.getId() : 0;
   }

   public boolean hasKey(String key) {
      return this.tagMap.containsKey(key);
   }

   public boolean hasKey(String key, int type) {
      byte tagId = this.getTagId(key);
      if (tagId == type) {
         return true;
      }

      if (type != 99) {
         if (tagId > 0) {
         }

         return false;
      } else {
         return tagId == 1 || tagId == 2 || tagId == 3 || tagId == 4 || tagId == 5 || tagId == 6;
      }
   }

   public byte getByte(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getByte();
      } catch (ClassCastException e) {
         return 0;
      }
   }

   public short getShort(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getShort();
      } catch (ClassCastException e) {
         return 0;
      }
   }

   public int getInteger(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getInt();
      } catch (ClassCastException e) {
         return 0;
      }
   }

   public long getLong(String key) {
      try {
         return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getLong();
      } catch (ClassCastException e) {
         return 0L;
      }
   }

   public float getFloat(String key) {
      try {
         return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getFloat();
      } catch (ClassCastException e) {
         return 0.0F;
      }
   }

   public double getDouble(String key) {
      try {
         return !this.hasKey(key, 99) ? 0.0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getDouble();
      } catch (ClassCastException e) {
         return 0.0;
      }
   }

   public String getString(String key) {
      try {
         return !this.hasKey(key, 8) ? "" : this.tagMap.get(key).getString();
      } catch (ClassCastException e) {
         return "";
      }
   }

   public byte[] getByteArray(String key) {
      try {
         return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(key)).getByteArray();
      } catch (ClassCastException e) {
         throw new ReportedException(this.createCrashReport(key, 7, e));
      }
   }

   public int[] getIntArray(String key) {
      try {
         return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(key)).getIntArray();
      } catch (ClassCastException e) {
         throw new ReportedException(this.createCrashReport(key, 11, e));
      }
   }

   public NBTTagCompound getCompoundTag(String key) {
      try {
         return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(key);
      } catch (ClassCastException e) {
         throw new ReportedException(this.createCrashReport(key, 10, e));
      }
   }

   public NBTTagList getTagList(String key, int type) {
      try {
         if (this.getTagId(key) != 9) {
            return new NBTTagList();
         }

         NBTTagList nbttagList = (NBTTagList)this.tagMap.get(key);
         return nbttagList.tagCount() > 0 && nbttagList.getTagType() != type ? new NBTTagList() : nbttagList;
      } catch (ClassCastException e) {
         throw new ReportedException(this.createCrashReport(key, 9, e));
      }
   }

   public boolean getBoolean(String key) {
      return this.getByte(key) != 0;
   }

   public void removeTag(String key) {
      this.tagMap.remove(key);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("{");

      for (Entry entry : this.tagMap.entrySet()) {
         if (sb.length() != 1) {
            sb.append(',');
         }

         sb.append((String)entry.getKey()).append(':').append(entry.getValue());
      }

      return sb.append('}').toString();
   }

   @Override
   public boolean hasNoTags() {
      return this.tagMap.isEmpty();
   }

   public CrashReport createCrashReport(final String key, final int expectedType, ClassCastException ex) {
      CrashReport makeCrashReport = CrashReport.makeCrashReport(ex, "Reading NBT data");
      CrashReportCategory makeCategoryDepth = makeCrashReport.makeCategoryDepth("Corrupt NBT tag", 1);
      makeCategoryDepth.addCrashSectionCallable("Tag type found", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[NBTTagCompound.this.tagMap.get(key).getId()];
         }
      });
      makeCategoryDepth.addCrashSectionCallable("Tag type expected", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[expectedType];
         }
      });
      makeCategoryDepth.addCrashSection("Tag name", key);
      return makeCrashReport;
   }

   @Override
   public NBTBase copy() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();

      for (String str : this.tagMap.keySet()) {
         nbttagCompound.setTag(str, this.tagMap.get(str).copy());
      }

      return nbttagCompound;
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         NBTTagCompound nbttagCompound = (NBTTagCompound)obj;
         return this.tagMap.entrySet().equals(nbttagCompound.tagMap.entrySet());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagMap.hashCode();
   }

   public static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
      output.writeByte(data.getId());
      if (data.getId() != 0) {
         output.writeUTF(name);
         data.write(output);
      }
   }

   public static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readByte();
   }

   public static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readUTF();
   }

   public static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      NBTBase createNewByType = NBTBase.createNewByType(id);

      try {
         createNewByType.read(input, depth, sizeTracker);
         return createNewByType;
      } catch (IOException e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Loading NBT data");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("NBT Tag");
         makeCategory.addCrashSection("Tag name", key);
         makeCategory.addCrashSection("Tag type", id);
         throw new ReportedException(makeCrashReport);
      }
   }

   public void merge(NBTTagCompound other) {
      for (String str : other.tagMap.keySet()) {
         NBTBase nbtbase = other.tagMap.get(str);
         if (nbtbase.getId() == 10) {
            if (this.hasKey(str, 10)) {
               NBTTagCompound compoundTag = this.getCompoundTag(str);
               compoundTag.merge((NBTTagCompound)nbtbase);
            } else {
               this.setTag(str, nbtbase.copy());
            }
         } else {
            this.setTag(str, nbtbase.copy());
         }
      }
   }
}
