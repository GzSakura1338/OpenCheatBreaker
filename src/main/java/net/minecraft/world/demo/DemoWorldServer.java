package net.minecraft.world.demo;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.ISaveHandler;

public class DemoWorldServer extends WorldServer {
    public static long demoWorldSeed = "North Carolina".hashCode();
    public static WorldSettings demoWorldSettings = new WorldSettings(demoWorldSeed, GameType.SURVIVAL, true, false, WorldType.DEFAULT).enableBonusChest();

    public DemoWorldServer(MinecraftServer minecraftServer, ISaveHandler saveHandler, String str, int n, Profiler profiler) {
        super(minecraftServer, saveHandler, str, n, demoWorldSettings, profiler);
    }
}
