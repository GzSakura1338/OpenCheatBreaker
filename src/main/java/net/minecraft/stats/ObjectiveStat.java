package net.minecraft.stats;

import net.minecraft.scoreboard.ScoreDummyCriteria;

public class ObjectiveStat extends ScoreDummyCriteria {
    public StatBase stat;

    public ObjectiveStat(StatBase statBase) {
        super(statBase.statId);
        this.stat = statBase;
    }
}
