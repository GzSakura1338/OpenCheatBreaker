package net.optifine;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class DynamicLight {
    public Entity entity = null;
    public double offsetY = 0.0;
    public double lastPosX = -2.1474836E9F;
    public double lastPosY = -2.1474836E9F;
    public double lastPosZ = -2.1474836E9F;
    public int lastLightLevel = 0;
    public boolean underwater = false;
    public long timeCheckMs = 0L;
    public Set<BlockPos> setLitChunkPos = new HashSet<>();

    public DynamicLight(Entity entity) {
        this.entity = entity;
        this.offsetY = entity.getEyeHeight();
    }

    public void update(RenderGlobal renderGlobal) {
        if (Config.isDynamicLightsFast()) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < this.timeCheckMs + 500L) {
                return;
            }

            this.timeCheckMs = currentTimeMillis;
        }

        double d = this.entity.posX - 0.5;
        double d2 = this.entity.posY - 0.5 + this.offsetY;
        double d3 = this.entity.posZ - 0.5;
        int lightLevel = DynamicLights.getLightLevel(this.entity);
        double d4 = d - this.lastPosX;
        double d5 = d2 - this.lastPosY;
        double d6 = d3 - this.lastPosZ;
        double d7 = 0.1;
        if (Math.abs(d4) > d7 || Math.abs(d5) > d7 || Math.abs(d6) > d7 || this.lastLightLevel != lightLevel) {
            this.lastPosX = d;
            this.lastPosY = d2;
            this.lastPosZ = d3;
            this.lastLightLevel = lightLevel;
            this.underwater = false;
            WorldClient theWorld = renderGlobal.theWorld;
            if (theWorld != null) {
                Block block = theWorld.getBlock(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3));
                this.underwater = block == Blocks.water;
            }

            HashSet set = new HashSet();
            if (lightLevel > 0) {
                EnumFacing enumFacing = (MathHelper.floor_double(d) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
                EnumFacing enumFacing2 = (MathHelper.floor_double(d2) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
                EnumFacing enumFacing3 = (MathHelper.floor_double(d3) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;
                enumFacing = this.getOpposite(enumFacing);
                BlockPos blockPos = new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3));
                BlockPos blockPos2 = new BlockPos(
                    MathHelper.bucketInt(blockPos.getX(), 16) * 16, MathHelper.bucketInt(blockPos.getY(), 16) * 16, MathHelper.bucketInt(blockPos.getZ(), 16) * 16
                );
                BlockPos renderChunk = getRenderChunk(blockPos2, enumFacing);
                BlockPos renderChunk2 = getRenderChunk(blockPos2, enumFacing3);
                BlockPos renderChunk3 = getRenderChunk(renderChunk, enumFacing3);
                BlockPos renderChunk4 = getRenderChunk(blockPos2, enumFacing2);
                BlockPos renderChunk5 = getRenderChunk(renderChunk4, enumFacing);
                BlockPos renderChunk6 = getRenderChunk(renderChunk4, enumFacing3);
                BlockPos renderChunk7 = getRenderChunk(renderChunk5, enumFacing3);
                this.updateChunkLight(renderGlobal, blockPos2, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk2, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk3, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk4, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk5, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk6, this.setLitChunkPos, set);
                this.updateChunkLight(renderGlobal, renderChunk7, this.setLitChunkPos, set);
            }

            this.updateLitChunks(renderGlobal);
            this.setLitChunkPos = set;
        }
    }

    public EnumFacing getOpposite(EnumFacing enumFacing) {
        switch (enumFacing) {
            case DOWN:
                return EnumFacing.UP;
            case UP:
                return EnumFacing.DOWN;
            case NORTH:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.NORTH;
            case EAST:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.EAST;
            default:
                return EnumFacing.DOWN;
        }
    }

    public static BlockPos getRenderChunk(BlockPos blockPos, EnumFacing enumFacing) {
        return new BlockPos(blockPos.getX() + enumFacing.getFrontOffsetX() * 16, blockPos.getY() + enumFacing.getFrontOffsetY() * 16, blockPos.getZ() + enumFacing.getFrontOffsetZ() * 16);
    }

    public void updateChunkLight(RenderGlobal renderGlobal, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2) {
        if (blockPos != null) {
            renderGlobal.markBlockForUpdate(blockPos.getX() + 8, blockPos.getY() + 8, blockPos.getZ() + 8);
            if (set != null) {
                set.remove(blockPos);
            }

            if (set2 != null) {
                set2.add(blockPos);
            }
        }
    }

    public void updateLitChunks(RenderGlobal renderGlobal) {
        for (BlockPos blockPos : this.setLitChunkPos) {
            this.updateChunkLight(renderGlobal, blockPos, (Set<BlockPos>)null, (Set<BlockPos>)null);
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public double getLastPosX() {
        return this.lastPosX;
    }

    public double getLastPosY() {
        return this.lastPosY;
    }

    public double getLastPosZ() {
        return this.lastPosZ;
    }

    public int getLastLightLevel() {
        return this.lastLightLevel;
    }

    public boolean isUnderwater() {
        return this.underwater;
    }

    public double getOffsetY() {
        return this.offsetY;
    }

    @Override
    public String toString() {
        return "Entity: " + this.entity + ", offsetY: " + this.offsetY;
    }
}
