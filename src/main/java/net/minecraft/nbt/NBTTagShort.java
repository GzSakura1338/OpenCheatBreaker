package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase.NBTPrimitive {
   public short data;

   public NBTTagShort() {
   }

   public NBTTagShort(short data) {
      this.data = data;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeShort(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(80L);
      this.data = input.readShort();
   }

   @Override
   public byte getId() {
      return 2;
   }

   @Override
   public String toString() {
      return "" + this.data + "s";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagShort(this.data);
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         NBTTagShort nbttagShort = (NBTTagShort)obj;
         return this.data == nbttagShort.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data;
   }

   @Override
   public long getLong() {
      return this.data;
   }

   @Override
   public int getInt() {
      return this.data;
   }

   @Override
   public short getShort() {
      return this.data;
   }

   @Override
   public byte getByte() {
      return (byte)(this.data & 0xFF);
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   public float getFloat() {
      return this.data;
   }
}
