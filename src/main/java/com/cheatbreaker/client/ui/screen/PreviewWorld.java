package com.cheatbreaker.client.ui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;

public class PreviewWorld extends WorldClient {
    public PreviewWorld(Minecraft mc) {
        super(new PreviewNetHandler(mc), new WorldSettings(0L, GameType.CREATIVE, true, false, WorldType.DEFAULT), 0, EnumDifficulty.NORMAL, new Profiler());
    }
}
