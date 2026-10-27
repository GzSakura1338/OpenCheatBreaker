package com.cheatbreaker.client.font;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class StringCacheMap<K, V> extends Object2ObjectOpenHashMap<K, V> {
   public StringCacheKey lookupKey = new StringCacheKey(null, 0, false);

   public CachedString lookup(String str, int n, boolean flag) {
      StringCacheKey.setText(this.lookupKey, str);
      StringCacheKey.setColor(this.lookupKey, n);
      StringCacheKey.setShadow(this.lookupKey, flag);
      return (CachedString)this.get(this.lookupKey);
   }
}
