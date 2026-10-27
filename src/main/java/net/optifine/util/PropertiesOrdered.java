package net.optifine.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class PropertiesOrdered extends Properties {
   public Set<Object> keysOrdered = new LinkedHashSet<>();

   @Override
   public synchronized Object put(Object obj, Object obj2) {
      this.keysOrdered.add(obj);
      return super.put(obj, obj2);
   }

   @Override
   public Set<Object> keySet() {
      Set keySet = super.keySet();
      this.keysOrdered.retainAll(keySet);
      return Collections.unmodifiableSet(this.keysOrdered);
   }

   @Override
   public synchronized Enumeration<Object> keys() {
      return Collections.enumeration(this.keySet());
   }
}
