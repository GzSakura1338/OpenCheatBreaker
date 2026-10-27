package net.minecraft.util;

public interface IRegistry<K, V> extends Iterable<V> {
   V getObject(K value);

   void putObject(K value, V value2);
}
