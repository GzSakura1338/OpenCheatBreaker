package net.minecraft.profiler;

public interface IPlayerUsage {
    void addServerStatsToSnooper(PlayerUsageSnooper playerUsageSnooper);

    void addServerTypeToSnooper(PlayerUsageSnooper playerUsageSnooper);

    boolean isSnooperEnabled();
}
