package com.cheatbreaker.client.emote.bobj;

import java.util.HashMap;
import java.util.Map;

public class BobjAction {
   public Map<Object, BobjGroup> groups = new HashMap<>();
   public String name;

   public int getLength() {
      int n = 0;

      for (BobjGroup bobjGroup : this.groups.values()) {
         n = Math.max(n, bobjGroup.getLength());
      }

      return n;
   }

   public BobjAction(String str) {
      this.name = str;
   }
}
