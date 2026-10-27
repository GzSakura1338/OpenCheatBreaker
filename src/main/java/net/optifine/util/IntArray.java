package net.optifine.util;

public class IntArray {
   public int[] array = null;
   public int position = 0;
   public int limit = 0;

   public IntArray(int n) {
      this.array = new int[n];
   }

   public void put(int n) {
      this.array[this.position] = n;
      this.position++;
      if (this.limit < this.position) {
         this.limit = this.position;
      }
   }

   public void put(int n, int n2) {
      this.array[n] = n2;
      if (this.limit < n) {
         this.limit = n;
      }
   }

   public void position(int n) {
      this.position = n;
   }

   public void put(int[] ints) {
      int length = ints.length;

      for (int i = 0; i < length; i++) {
         this.array[this.position] = ints[i];
         this.position++;
      }

      if (this.limit < this.position) {
         this.limit = this.position;
      }
   }

   public int get(int n) {
      return this.array[n];
   }

   public int[] getArray() {
      return this.array;
   }

   public void clear() {
      this.position = 0;
      this.limit = 0;
   }

   public int getPosition() {
      return this.position;
   }

   public int getLimit() {
      return this.limit;
   }
}
