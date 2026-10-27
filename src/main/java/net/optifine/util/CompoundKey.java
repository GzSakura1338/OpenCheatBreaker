package net.optifine.util;

import net.optifine.Config;

public class CompoundKey {
   public int hashcode = 0;
   public Object[] keys;

   public CompoundKey(Object[] objs) {
      this.keys = (Object[])objs.clone();
   }

   public CompoundKey(Object obj, Object obj2) {
      this(new Object[]{obj, obj2});
   }

   public CompoundKey(Object obj, Object obj2, Object obj3) {
      this(new Object[]{obj, obj2, obj3});
   }

   @Override
   public int hashCode() {
      if (this.hashcode == 0) {
         this.hashcode = 7;

         for (int i = 0; i < this.keys.length; i++) {
            Object obj = this.keys[i];
            if (obj != null) {
               this.hashcode = 31 * this.hashcode + obj.hashCode();
            }
         }
      }

      return this.hashcode;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (obj == this) {
         return true;
      }

      if (!(obj instanceof CompoundKey)) {
         return false;
      }

      CompoundKey compoundKey = (CompoundKey)obj;
      Object[] keys = compoundKey.getKeys();
      if (keys.length != this.keys.length) {
         return false;
      }

      for (int i = 0; i < this.keys.length; i++) {
         if (!compareKeys(this.keys[i], keys[i])) {
            return false;
         }
      }

      return true;
   }

   public static boolean compareKeys(Object obj, Object obj2) {
      return obj == obj2 ? true : (obj == null ? false : (obj2 == null ? false : obj.equals(obj2)));
   }

   public Object[] getKeys() {
      return this.keys;
   }

   public Object[] getKeysCopy() {
      return (Object[])this.keys.clone();
   }

   @Override
   public String toString() {
      return "[" + Config.arrayToString(this.keys) + "]";
   }
}
