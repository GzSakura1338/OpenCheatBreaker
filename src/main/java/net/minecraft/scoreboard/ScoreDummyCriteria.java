package net.minecraft.scoreboard;

import java.util.List;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria {
    public String name;

    public ScoreDummyCriteria(String str) {
        this.name = str;
        IScoreObjectiveCriteria.INSTANCES.put(str, this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int computeInt(List list) {
        return 0;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
