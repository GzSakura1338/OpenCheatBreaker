package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

public class BlockMushroom extends BlockBush implements IGrowable {
   public BlockMushroom() {
      float f = 0.2F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (rand.nextInt(25) == 0) {
         int n = 5;
         byte b = 4;

         for (BlockPos blockPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
            if (worldIn.getBlockState(blockPos).getBlock() == this) {
               if (--n <= 0) {
                  return;
               }
            }
         }

         BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

         for (int i = 0; i < 4; i++) {
            if (worldIn.isAirBlock(blockPos) && this.canBlockStay(worldIn, blockPos, this.getDefaultState())) {
               pos = blockPos;
            }

            blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
         }

         if (worldIn.isAirBlock(blockPos) && this.canBlockStay(worldIn, blockPos, this.getDefaultState())) {
            worldIn.setBlockState(blockPos, this.getDefaultState(), 2);
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
   }

   @Override
   public boolean canPlaceBlockOn(Block ground) {
      return ground.isFullBlock();
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         IBlockState blockState = worldIn.getBlockState(pos.down());
         return blockState.getBlock() == Blocks.mycelium
            ? true
            : (
               blockState.getBlock() == Blocks.dirt && blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL
                  ? true
                  : worldIn.getLight(pos) < 13 && this.canPlaceBlockOn(blockState.getBlock())
            );
      } else {
         return false;
      }
   }

   public boolean generateBigMushroom(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      worldIn.setBlockToAir(pos);
      WorldGenBigMushroom worldGenBigMushroom = null;
      if (this == Blocks.brown_mushroom) {
         worldGenBigMushroom = new WorldGenBigMushroom(Blocks.brown_mushroom_block);
      } else if (this == Blocks.red_mushroom) {
         worldGenBigMushroom = new WorldGenBigMushroom(Blocks.red_mushroom_block);
      }

      if (worldGenBigMushroom != null && worldGenBigMushroom.generate(worldIn, rand, pos)) {
         return true;
      }

      worldIn.setBlockState(pos, state, 3);
      return false;
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return rand.nextFloat() < 0.4;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.generateBigMushroom(worldIn, pos, state, rand);
   }
}
