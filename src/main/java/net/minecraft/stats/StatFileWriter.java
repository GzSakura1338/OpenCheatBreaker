package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
   public Map<StatBase, TupleIntJsonSerializable> statsData = Maps.newConcurrentMap();

   public boolean hasAchievementUnlocked(Achievement achievementIn) {
      return this.readStat(achievementIn) > 0;
   }

   public boolean canUnlockAchievement(Achievement achievementIn) {
      return achievementIn.parentAchievement == null || this.hasAchievementUnlocked(achievementIn.parentAchievement);
   }

   public int getAchievementDepth(Achievement achievement) {
      if (this.hasAchievementUnlocked(achievement)) {
         return 0;
      }

      int n = 0;

      for (Achievement parentAchievement = achievement.parentAchievement; parentAchievement != null && !this.hasAchievementUnlocked(parentAchievement); n++) {
         parentAchievement = parentAchievement.parentAchievement;
      }

      return n;
   }

   public void increaseStat(EntityPlayer player, StatBase stat, int amount) {
      if (!stat.isAchievement() || this.canUnlockAchievement((Achievement)stat)) {
         this.unlockAchievement(player, stat, this.readStat(stat) + amount);
      }
   }

   public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int n) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statIn);
      if (tupleIntJsonSerializable == null) {
         tupleIntJsonSerializable = new TupleIntJsonSerializable();
         this.statsData.put(statIn, tupleIntJsonSerializable);
      }

      tupleIntJsonSerializable.setIntegerValue(n);
   }

   public int readStat(StatBase stat) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(stat);
      return tupleIntJsonSerializable == null ? 0 : tupleIntJsonSerializable.getIntegerValue();
   }

   public <T extends IJsonSerializable> T getStat(StatBase statBase) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statBase);
      return tupleIntJsonSerializable != null ? tupleIntJsonSerializable.getJsonSerializableValue() : null;
   }

   public <T extends IJsonSerializable> T setStat(StatBase statBase, T value) {
      TupleIntJsonSerializable tupleIntJsonSerializable = this.statsData.get(statBase);
      if (tupleIntJsonSerializable == null) {
         tupleIntJsonSerializable = new TupleIntJsonSerializable();
         this.statsData.put(statBase, tupleIntJsonSerializable);
      }

      tupleIntJsonSerializable.setJsonSerializableValue(value);
      return (T)value;
   }
}
