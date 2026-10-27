package net.minecraft.util;

public class Tuple<A, B> {
   public A a;
   public B b;

   public Tuple(A aIn, B bIn) {
      this.a = (A)aIn;
      this.b = (B)bIn;
   }

   public A getFirst() {
      return this.a;
   }

   public B getSecond() {
      return this.b;
   }
}
