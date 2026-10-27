package com.cheatbreaker.client.ui.screen;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.DefaultResourcePack;

public class CustomDefaultResourcePack extends DefaultResourcePack {
   public Map map;
   public static Set set = null;

   public CustomDefaultResourcePack(Map map, String str) {
      super(map);
      set = ImmutableSet.of(str);
      this.map = map;
   }
}
