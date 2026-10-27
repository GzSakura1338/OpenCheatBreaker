package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
   public EmptyChunk(World worldIn, int x, int z) {
      super(worldIn, x, z);
   }

   @Override
   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   @Override
   public int getHeightValue(int x, int z) {
      return 0;
   }

   @Override
   public void generateHeightMap() {
   }

   @Override
   public void generateSkylightMap() {
   }

   @Override
   public Block getBlock(BlockPos pos) {
      return Blocks.air;
   }

   @Override
   public int getBlockLightOpacity(BlockPos pos) {
      return 255;
   }

   @Override
   public int getBlockMetadata(BlockPos pos) {
      return 0;
   }

   @Override
   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      return enumSkyBlock.defaultLightValue;
   }

   @Override
   public void setLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos, int value) {
   }

   @Override
   public int getLightSubtracted(BlockPos pos, int amount) {
      return 0;
   }

   @Override
   public void addEntity(Entity entityIn) {
   }

   @Override
   public void removeEntity(Entity entityIn) {
   }

   @Override
   public void removeEntityAtIndex(Entity entityIn, int n) {
   }

   @Override
   public boolean canSeeSky(BlockPos pos) {
      return false;
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType enumCreateEntityType) {
      return null;
   }

   @Override
   public void addTileEntity(TileEntity tileEntityIn) {
   }

   @Override
   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   @Override
   public void removeTileEntity(BlockPos pos) {
   }

   @Override
   public void onChunkLoad() {
   }

   @Override
   public void onChunkUnload() {
   }

   @Override
   public void setChunkModified() {
   }

   @Override
   public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate<? super Entity> predicate) {
   }

   @Override
   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate<? super T> predicate) {
   }

   @Override
   public boolean needsSaving(boolean flag) {
      return false;
   }

   @Override
   public Random getRandomWithSeed(long seed) {
      return new Random(
         this.getWorld().getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ seed
      );
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean getAreLevelsEmpty(int startY, int endY) {
      return true;
   }
}
