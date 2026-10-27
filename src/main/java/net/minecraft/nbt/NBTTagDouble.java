package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
   public double data;

   public NBTTagDouble() {
   }

   public NBTTagDouble(double data) {
      this.data = data;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeDouble(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(128L);
      this.data = input.readDouble();
   }

   @Override
   public byte getId() {
      return 6;
   }

   @Override
   public String toString() {
      return "" + this.data + "d";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagDouble(this.data);
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) {
         NBTTagDouble nbttagDouble = (NBTTagDouble)obj;
         return this.data == nbttagDouble.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      long doubleToLongBits = Double.doubleToLongBits(this.data);
      return super.hashCode() ^ (int)(doubleToLongBits ^ doubleToLongBits >>> 32);
   }

   @Override
   public long getLong() {
      return (long)Math.floor(this.data);
   }

   @Override
   public int getInt() {
      return MathHelper.floor_double(this.data);
   }

   @Override
   public short getShort() {
      return (short)(MathHelper.floor_double(this.data) & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(MathHelper.floor_double(this.data) & 0xFF);
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
