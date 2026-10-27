package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTBase {
   public static Logger LOGGER = LogManager.getLogger();
   public List<NBTBase> tagList = Lists.newArrayList();
   public byte tagType = 0;

   @Override
   public void write(DataOutput output) throws IOException {
      if (!this.tagList.isEmpty()) {
         this.tagType = this.tagList.get(0).getId();
      } else {
         this.tagType = 0;
      }

      output.writeByte(this.tagType);
      output.writeInt(this.tagList.size());

      for (int i = 0; i < this.tagList.size(); i++) {
         this.tagList.get(i).write(output);
      }
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(296L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagType = input.readByte();
      int readInt = input.readInt();
      if (this.tagType == 0 && readInt > 0) {
         throw new RuntimeException("Missing type on ListTag");
      }

      sizeTracker.read(32L * readInt);
      this.tagList = Lists.newArrayListWithCapacity(readInt);

      for (int i = 0; i < readInt; i++) {
         NBTBase createNewByType = NBTBase.createNewByType(this.tagType);
         createNewByType.read(input, depth + 1, sizeTracker);
         this.tagList.add(createNewByType);
      }
   }

   @Override
   public byte getId() {
      return 9;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("[");

      for (int i = 0; i < this.tagList.size(); i++) {
         if (i != 0) {
            sb.append(',');
         }

         sb.append(i).append(':').append(this.tagList.get(i));
      }

      return sb.append(']').toString();
   }

   public void appendTag(NBTBase nbt) {
      if (nbt.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else {
         if (this.tagType == 0) {
            this.tagType = nbt.getId();
         } else if (this.tagType != nbt.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.add(nbt);
      }
   }

   public void set(int idx, NBTBase nbt) {
      if (nbt.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else if (idx >= 0 && idx < this.tagList.size()) {
         if (this.tagType == 0) {
            this.tagType = nbt.getId();
         } else if (this.tagType != nbt.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.set(idx, nbt);
      } else {
         LOGGER.warn("index out of bounds to set tag in tag list");
      }
   }

   public NBTBase removeTag(int i) {
      return this.tagList.remove(i);
   }

   @Override
   public boolean hasNoTags() {
      return this.tagList.isEmpty();
   }

   public NBTTagCompound getCompoundTagAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtbase = this.tagList.get(i);
         return nbtbase.getId() == 10 ? (NBTTagCompound)nbtbase : new NBTTagCompound();
      } else {
         return new NBTTagCompound();
      }
   }

   public int[] getIntArrayAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtbase = this.tagList.get(i);
         return nbtbase.getId() == 11 ? ((NBTTagIntArray)nbtbase).getIntArray() : new int[0];
      } else {
         return new int[0];
      }
   }

   public double getDoubleAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtbase = this.tagList.get(i);
         return nbtbase.getId() == 6 ? ((NBTTagDouble)nbtbase).getDouble() : 0.0;
      } else {
         return 0.0;
      }
   }

   public float getFloatAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtbase = this.tagList.get(i);
         return nbtbase.getId() == 5 ? ((NBTTagFloat)nbtbase).getFloat() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public String getStringTagAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtbase = this.tagList.get(i);
         return nbtbase.getId() == 8 ? nbtbase.getString() : nbtbase.toString();
      } else {
         return "";
      }
   }

   public NBTBase get(int idx) {
      return idx >= 0 && idx < this.tagList.size() ? this.tagList.get(idx) : new NBTTagEnd();
   }

   public int tagCount() {
      return this.tagList.size();
   }

   @Override
   public NBTBase copy() {
      NBTTagList nbttagList = new NBTTagList();
      nbttagList.tagType = this.tagType;

      for (NBTBase nbtbase : this.tagList) {
         NBTBase nbtbase2 = nbtbase.copy();
         nbttagList.tagList.add(nbtbase2);
      }

      return nbttagList;
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         NBTTagList nbttagList = (NBTTagList)obj;
         if (this.tagType == nbttagList.tagType) {
            return this.tagList.equals(nbttagList.tagList);
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagList.hashCode();
   }

   public int getTagType() {
      return this.tagType;
   }
}
