package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
   public String data;

   public NBTTagString() {
      this.data = "";
   }

   public NBTTagString(String data) {
      this.data = data;
      if (data == null) {
         throw new IllegalArgumentException("Empty string not allowed");
      }
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeUTF(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(288L);
      this.data = input.readUTF();
      sizeTracker.read(16 * this.data.length());
   }

   @Override
   public byte getId() {
      return 8;
   }

   @Override
   public String toString() {
      return "\"" + this.data.replace("\"", "\\\"") + "\"";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagString(this.data);
   }

   @Override
   public boolean hasNoTags() {
      return this.data.isEmpty();
   }

   @Override
   public boolean equals(Object obj) {
      if (!super.equals(obj)) {
         return false;
      }

      NBTTagString nbttagString = (NBTTagString)obj;
      return this.data == null && nbttagString.data == null || this.data != null && this.data.equals(nbttagString.data);
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data.hashCode();
   }

   @Override
   public String getString() {
      return this.data;
   }
}
