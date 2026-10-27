package net.optifine;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
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
   public MutableBlockPos blockPosMutable = new MutableBlockPos();

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
         WorldClient world = renderGlobal.getWorld();
         if (world != null) {
            this.blockPosMutable.set(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3));
            IBlockState blockState = world.getBlockState(this.blockPosMutable);
            Block block = blockState.getBlock();
            this.underwater = block == Blocks.water;
         }

         HashSet set = new HashSet();
         if (lightLevel > 0) {
            EnumFacing enumFacing = (MathHelper.floor_double(d) & 15) >= 8 ? EnumFacing.EAST : EnumFacing.WEST;
            EnumFacing enumFacing2 = (MathHelper.floor_double(d2) & 15) >= 8 ? EnumFacing.UP : EnumFacing.DOWN;
            EnumFacing enumFacing3 = (MathHelper.floor_double(d3) & 15) >= 8 ? EnumFacing.SOUTH : EnumFacing.NORTH;
            BlockPos blockPos = new BlockPos(d, d2, d3);
            RenderChunk renderChunk = renderGlobal.getRenderChunk(blockPos);
            BlockPos chunkPos = this.getChunkPos(renderChunk, blockPos, enumFacing);
            RenderChunk renderChunk2 = renderGlobal.getRenderChunk(chunkPos);
            BlockPos chunkPos2 = this.getChunkPos(renderChunk, blockPos, enumFacing3);
            RenderChunk renderChunk3 = renderGlobal.getRenderChunk(chunkPos2);
            BlockPos chunkPos3 = this.getChunkPos(renderChunk2, chunkPos, enumFacing3);
            RenderChunk renderChunk4 = renderGlobal.getRenderChunk(chunkPos3);
            BlockPos chunkPos4 = this.getChunkPos(renderChunk, blockPos, enumFacing2);
            RenderChunk renderChunk5 = renderGlobal.getRenderChunk(chunkPos4);
            BlockPos chunkPos5 = this.getChunkPos(renderChunk5, chunkPos4, enumFacing);
            RenderChunk renderChunk6 = renderGlobal.getRenderChunk(chunkPos5);
            BlockPos chunkPos6 = this.getChunkPos(renderChunk5, chunkPos4, enumFacing3);
            RenderChunk renderChunk7 = renderGlobal.getRenderChunk(chunkPos6);
            BlockPos chunkPos7 = this.getChunkPos(renderChunk6, chunkPos5, enumFacing3);
            RenderChunk renderChunk8 = renderGlobal.getRenderChunk(chunkPos7);
            this.updateChunkLight(renderChunk, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk2, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk3, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk4, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk5, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk6, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk7, this.setLitChunkPos, set);
            this.updateChunkLight(renderChunk8, this.setLitChunkPos, set);
         }

         this.updateLitChunks(renderGlobal);
         this.setLitChunkPos = set;
      }
   }

   public BlockPos getChunkPos(RenderChunk renderChunk, BlockPos blockPos, EnumFacing enumFacing) {
      return renderChunk != null ? renderChunk.getBlockPosOffset16(enumFacing) : blockPos.offset(enumFacing, 16);
   }

   public void updateChunkLight(RenderChunk renderChunk, Set<BlockPos> set, Set<BlockPos> set2) {
      if (renderChunk != null) {
         CompiledChunk compiledChunk = renderChunk.getCompiledChunk();
         if (compiledChunk != null && !compiledChunk.isEmpty()) {
            renderChunk.setNeedsUpdate(true);
         }

         BlockPos position = renderChunk.getPosition();
         if (set != null) {
            set.remove(position);
         }

         if (set2 != null) {
            set2.add(position);
         }
      }
   }

   public void updateLitChunks(RenderGlobal renderGlobal) {
      for (BlockPos blockPos : this.setLitChunkPos) {
         RenderChunk renderChunk = renderGlobal.getRenderChunk(blockPos);
         this.updateChunkLight(renderChunk, (Set<BlockPos>)null, (Set<BlockPos>)null);
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
