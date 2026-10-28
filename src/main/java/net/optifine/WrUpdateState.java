package net.optifine;

import java.util.HashSet;
import net.minecraft.client.renderer.RenderBlocks;

public class WrUpdateState {
    public ChunkCacheOF chunkcache = null;
    public RenderBlocks renderblocks = null;
    public HashSet setOldEntityRenders = null;
    public int viewEntityPosX = 0;
    public int viewEntityPosY = 0;
    public int viewEntityPosZ = 0;
    public int renderPass = 0;
    public int intValue = 0;
    public boolean flag = false;
    public boolean hasRenderedBlocks = false;
    public boolean hasGlList = false;
}
