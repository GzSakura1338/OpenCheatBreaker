package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.util.DeveloperFlags;

public class AbstractInternalMod extends AbstractMod {
   @Override
   public void onEnable() {
      if (DeveloperFlags.showInternalMods) {
         super.onEnable();
      }
   }

   public AbstractInternalMod(String str) {
      super(str);
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && DeveloperFlags.showInternalMods;
   }
}
