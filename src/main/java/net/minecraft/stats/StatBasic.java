package net.minecraft.stats;

import net.minecraft.util.IChatComponent;

public class StatBasic extends StatBase {
    public StatBasic(String str, IChatComponent chatComponent, IStatType statType) {
        super(str, chatComponent, statType);
    }

    public StatBasic(String str, IChatComponent chatComponent) {
        super(str, chatComponent);
    }

    @Override
    public StatBase registerStat() {
        super.registerStat();
        StatList.generalStats.add(this);
        return this;
    }
}
