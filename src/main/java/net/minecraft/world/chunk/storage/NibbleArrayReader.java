package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
   public byte[] data;
   public int depthBits;
   public int depthBitsPlusFour;

   public NibbleArrayReader(byte[] dataIn, int depthBitsIn) {
      this.data = dataIn;
      this.depthBits = depthBitsIn;
      this.depthBitsPlusFour = depthBitsIn + 4;
   }

   public int get(int n, int n2, int n3) {
      int n4 = n << this.depthBitsPlusFour | n3 << this.depthBits | n2;
      int n5 = n4 >> 1;
      int n6 = n4 & 1;
      return n6 == 0 ? this.data[n5] & 15 : this.data[n5] >> 4 & 15;
   }
}
