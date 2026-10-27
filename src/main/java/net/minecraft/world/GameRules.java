package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   public TreeMap<String, GameRules.Value> theGameRules = new TreeMap<>();

   public GameRules() {
      this.addGameRule("doFireTick", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("mobGriefing", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("keepInventory", "false", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doMobSpawning", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doMobLoot", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doTileDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doEntityDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("commandBlockOutput", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("naturalRegeneration", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doDaylightCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("logAdminCommands", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("showDeathMessages", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("randomTickSpeed", "3", GameRules.ValueType.NUMERICAL_VALUE);
      this.addGameRule("sendCommandFeedback", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("reducedDebugInfo", "false", GameRules.ValueType.BOOLEAN_VALUE);
   }

   public void addGameRule(String key, String value, GameRules.ValueType type) {
      this.theGameRules.put(key, new GameRules.Value(value, type));
   }

   public void setOrCreateGameRule(String key, String ruleValue) {
      GameRules.Value value = this.theGameRules.get(key);
      if (value != null) {
         value.setValue(ruleValue);
      } else {
         this.addGameRule(key, ruleValue, GameRules.ValueType.ANY_VALUE);
      }
   }

   public String getString(String name) {
      GameRules.Value value = this.theGameRules.get(name);
      return value != null ? value.getString() : "";
   }

   public boolean getBoolean(String name) {
      GameRules.Value value = this.theGameRules.get(name);
      return value != null ? value.getBoolean() : false;
   }

   public int getInt(String name) {
      GameRules.Value value = this.theGameRules.get(name);
      return value != null ? value.getInt() : 0;
   }

   public NBTTagCompound writeToNBT() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();

      for (String str : this.theGameRules.keySet()) {
         GameRules.Value value = this.theGameRules.get(str);
         nbttagCompound.setString(str, value.getString());
      }

      return nbttagCompound;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      for (String str : nbt.getKeySet()) {
         String string = nbt.getString(str);
         this.setOrCreateGameRule(str, string);
      }
   }

   public String[] getRules() {
      Set keySet = this.theGameRules.keySet();
      return (String[])keySet.toArray(new String[keySet.size()]);
   }

   public boolean hasRule(String name) {
      return this.theGameRules.containsKey(name);
   }

   public boolean areSameType(String key, GameRules.ValueType otherValue) {
      GameRules.Value value = this.theGameRules.get(key);
      return value != null && (value.getType() == otherValue || otherValue == GameRules.ValueType.ANY_VALUE);
   }

   static class Value {
      public String valueString;
      public boolean valueBoolean;
      public int valueInteger;
      public double valueDouble;
      public GameRules.ValueType type;

      public Value(String value, GameRules.ValueType type) {
         this.type = type;
         this.setValue(value);
      }

      public void setValue(String value) {
         this.valueString = value;
         if (value != null) {
            if (value.equals("false")) {
               this.valueBoolean = false;
               return;
            }

            if (value.equals("true")) {
               this.valueBoolean = true;
               return;
            }
         }

         this.valueBoolean = Boolean.parseBoolean(value);
         this.valueInteger = this.valueBoolean ? 1 : 0;

         try {
            this.valueInteger = Integer.parseInt(value);
         } catch (NumberFormatException e) {
         }

         try {
            this.valueDouble = Double.parseDouble(value);
         } catch (NumberFormatException e) {
         }
      }

      public String getString() {
         return this.valueString;
      }

      public boolean getBoolean() {
         return this.valueBoolean;
      }

      public int getInt() {
         return this.valueInteger;
      }

      public GameRules.ValueType getType() {
         return this.type;
      }
   }

   public enum ValueType {
      ANY_VALUE,
      BOOLEAN_VALUE,
      NUMERICAL_VALUE;
   }
}
