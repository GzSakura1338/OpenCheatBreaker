package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
    public WorldServerMulti(MinecraftServer minecraftServer, ISaveHandler saveHandler, String str, int n, WorldSettings worldSettings, WorldServer worldServer, Profiler profiler) {
        super(minecraftServer, saveHandler, str, n, worldSettings, profiler);
        this.mapStorage = worldServer.mapStorage;
        this.worldScoreboard = worldServer.getScoreboard();
        this.worldInfo = new DerivedWorldInfo(worldServer.getWorldInfo());
    }

    @Override
    public void saveLevel() throws MinecraftException {
    }
}
