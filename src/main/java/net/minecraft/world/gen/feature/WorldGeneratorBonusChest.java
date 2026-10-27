package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator {
   public List<WeightedRandomChestContent> chestItems;
   public int itemsToGenerateInBonusChest;

   public WorldGeneratorBonusChest(List<WeightedRandomChestContent> list, int n) {
      this.chestItems = list;
      this.itemsToGenerateInBonusChest = n;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      Block block;
      while (
         ((block = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && position.getY() > 1
      ) {
         position = position.down();
      }

      if (position.getY() < 1) {
         return false;
      }

      position = position.up();

      for (int i = 0; i < 4; i++) {
         BlockPos blockPos = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));
         if (worldIn.isAirBlock(blockPos) && World.doesBlockHaveSolidTopSurface(worldIn, blockPos.down())) {
            worldIn.setBlockState(blockPos, Blocks.chest.getDefaultState(), 2);
            TileEntity tileEntity = worldIn.getTileEntity(blockPos);
            if (tileEntity instanceof TileEntityChest) {
               WeightedRandomChestContent.generateChestContents(rand, this.chestItems, (TileEntityChest)tileEntity, this.itemsToGenerateInBonusChest);
            }

            BlockPos east = blockPos.east();
            BlockPos west = blockPos.west();
            BlockPos north = blockPos.north();
            BlockPos south = blockPos.south();
            if (worldIn.isAirBlock(west) && World.doesBlockHaveSolidTopSurface(worldIn, west.down())) {
               worldIn.setBlockState(west, Blocks.torch.getDefaultState(), 2);
            }

            if (worldIn.isAirBlock(east) && World.doesBlockHaveSolidTopSurface(worldIn, east.down())) {
               worldIn.setBlockState(east, Blocks.torch.getDefaultState(), 2);
            }

            if (worldIn.isAirBlock(north) && World.doesBlockHaveSolidTopSurface(worldIn, north.down())) {
               worldIn.setBlockState(north, Blocks.torch.getDefaultState(), 2);
            }

            if (worldIn.isAirBlock(south) && World.doesBlockHaveSolidTopSurface(worldIn, south.down())) {
               worldIn.setBlockState(south, Blocks.torch.getDefaultState(), 2);
            }

            return true;
         }
      }

      return false;
   }
}
