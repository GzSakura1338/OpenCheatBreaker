package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
   public int[] intArray;

   public NBTTagIntArray() {
   }

   public NBTTagIntArray(int[] ints) {
      this.intArray = ints;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.intArray.length);

      for (int i = 0; i < this.intArray.length; i++) {
         output.writeInt(this.intArray[i]);
      }
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int readInt = input.readInt();
      sizeTracker.read(32 * readInt);
      this.intArray = new int[readInt];

      for (int i = 0; i < readInt; i++) {
         this.intArray[i] = input.readInt();
      }
   }

   @Override
   public byte getId() {
      return 11;
   }

   @Override
   public String toString() {
      String str = "[";

      for (int n : this.intArray) {
         str = str + n + ",";
      }

      return str + "]";
   }

   @Override
   public NBTBase copy() {
      int[] ints = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, ints, 0, this.intArray.length);
      return new NBTTagIntArray(ints);
   }

   @Override
   public boolean equals(Object obj) {
      return super.equals(obj) ? Arrays.equals(this.intArray, ((NBTTagIntArray)obj).intArray) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }
}
