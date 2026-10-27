package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase.NBTPrimitive {
   public long data;

   public NBTTagLong() {
   }

   public NBTTagLong(long data) {
      this.data = data;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeLong(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(128L);
      this.data = input.readLong();
   }

   @Override
   public byte getId() {
      return 4;
   }

   @Override
   public String toString() {
      return "" + this.data + "L";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagLong(this.data);
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         NBTTagLong nbttagLong = (NBTTagLong)obj;
         return this.data == nbttagLong.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
   }

   @Override
   public long getLong() {
      return this.data;
   }

   @Override
   public int getInt() {
      return (int)(this.data & -1L);
   }

   @Override
   public short getShort() {
      return (short)(this.data & 65535L);
   }

   @Override
   public byte getByte() {
      return (byte)(this.data & 255L);
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   public float getFloat() {
      return (float)this.data;
   }
}
