package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.AnimationAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ActionMap {
   public Map<Object, AnimationAction> actions = new HashMap<>();

   public NBTTagCompound toNbt(NBTTagCompound nbttagCompound) {
      if (this.actions.isEmpty()) {
         return null;
      }

      if (nbttagCompound == null) {
         nbttagCompound = new NBTTagCompound();
      }

      for (Entry entry : this.actions.entrySet()) {
         AnimationAction value = (AnimationAction)entry.getValue();
         String key = (String)entry.getKey();
         if (!key.equals(value.name) || !value.isDefault()) {
            nbttagCompound.setTag(key, value.toNbt());
         }
      }

      return nbttagCompound;
   }

   public void copyFrom(ActionMap actionMap) {
      this.actions.clear();
      this.actions.putAll(actionMap.actions);
   }

   public void fromNbt(NBTTagCompound nbttagCompound) {
      this.actions.clear();

      for (String str : nbttagCompound.getKeySet()) {
         NBTBase tag = nbttagCompound.getTag(str);
         String toSnakeCase = this.toSnakeCase(str);
         AnimationAction animationAction = new AnimationAction(toSnakeCase);
         animationAction.fromNbt(tag);
         this.actions.put(toSnakeCase, animationAction);
      }
   }

   public String toSnakeCase(String str) {
      return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
   }

   public AnimationAction getAction(String str) {
      AnimationAction animationAction = this.actions.get(str);
      return animationAction == null ? new AnimationAction(str) : animationAction;
   }
}
