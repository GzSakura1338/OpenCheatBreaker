package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
    public Map stats = Maps.newConcurrentMap();

    public boolean hasAchievementUnlocked(Achievement achievement) {
        return this.writeStat(achievement) > 0;
    }

    public boolean canUnlockAchievement(Achievement achievement) {
        return achievement.parentAchievement == null || this.hasAchievementUnlocked(achievement.parentAchievement);
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

    public void addStatLevel(EntityPlayer entityPlayer, StatBase statBase, int n) {
        if (!statBase.isAchievement() || this.canUnlockAchievement((Achievement)statBase)) {
            this.handleEntityPlayer(entityPlayer, statBase, this.writeStat(statBase) + n);
        }
    }

    public void handleEntityPlayer(EntityPlayer entityPlayer, StatBase statBase, int n) {
        TupleIntJsonSerializable tupleIntJsonSerializable = (TupleIntJsonSerializable)this.stats.get(statBase);
        if (tupleIntJsonSerializable == null) {
            tupleIntJsonSerializable = new TupleIntJsonSerializable();
            this.stats.put(statBase, tupleIntJsonSerializable);
        }

        tupleIntJsonSerializable.setIntegerValue(n);
    }

    public int writeStat(StatBase statBase) {
        TupleIntJsonSerializable tupleIntJsonSerializable = (TupleIntJsonSerializable)this.stats.get(statBase);
        return tupleIntJsonSerializable == null ? 0 : tupleIntJsonSerializable.getIntegerValue();
    }

    public IJsonSerializable getStat(StatBase statBase) {
        TupleIntJsonSerializable tupleIntJsonSerializable = (TupleIntJsonSerializable)this.stats.get(statBase);
        return tupleIntJsonSerializable != null ? tupleIntJsonSerializable.getJsonSerializableValue() : null;
    }

    public IJsonSerializable setStat(StatBase statBase, IJsonSerializable jsonSerializable) {
        TupleIntJsonSerializable tupleIntJsonSerializable = (TupleIntJsonSerializable)this.stats.get(statBase);
        if (tupleIntJsonSerializable == null) {
            tupleIntJsonSerializable = new TupleIntJsonSerializable();
            this.stats.put(statBase, tupleIntJsonSerializable);
        }

        tupleIntJsonSerializable.setJsonSerializableValue(jsonSerializable);
        return jsonSerializable;
    }
}
