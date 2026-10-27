package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {
   public BlockDragonEgg() {
      super(Material.dragonEgg, MapColor.blackColor);
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.checkFall(worldIn, pos);
   }

   public void checkFall(World worldIn, BlockPos pos) {
      if (BlockFalling.canFallInto(worldIn, pos.down()) && pos.getY() >= 0) {
         byte b = 32;
         if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-b, -b, -b), pos.add(b, b, b))) {
            worldIn.spawnEntityInWorld(new EntityFallingBlock(worldIn, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, this.getDefaultState()));
         } else {
            worldIn.setBlockToAir(pos);
            BlockPos pos2 = pos;

            while (BlockFalling.canFallInto(worldIn, pos2) && pos2.getY() > 0) {
               pos2 = pos2.down();
            }

            if (pos2.getY() > 0) {
               worldIn.setBlockState(pos2, this.getDefaultState(), 2);
            }
         }
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      this.teleport(worldIn, pos);
      return true;
   }

   @Override
   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      this.teleport(worldIn, pos);
   }

   public void teleport(World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this) {
         for (int i = 0; i < 1000; i++) {
            BlockPos blockPos = pos.add(
               worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16),
               worldIn.rand.nextInt(8) - worldIn.rand.nextInt(8),
               worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16)
            );
            if (worldIn.getBlockState(blockPos).getBlock().blockMaterial == Material.air) {
               if (worldIn.isRemote) {
                  for (int j = 0; j < 128; j++) {
                     double nextDouble = worldIn.rand.nextDouble();
                     float f = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                     float f2 = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                     float f3 = (worldIn.rand.nextFloat() - 0.5F) * 0.2F;
                     double d = blockPos.getX() + (pos.getX() - blockPos.getX()) * nextDouble + (worldIn.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                     double d2 = blockPos.getY() + (pos.getY() - blockPos.getY()) * nextDouble + worldIn.rand.nextDouble() * 1.0 - 0.5;
                     double d3 = blockPos.getZ() + (pos.getZ() - blockPos.getZ()) * nextDouble + (worldIn.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                     worldIn.spawnParticle(EnumParticleTypes.PORTAL, d, d2, d3, f, f2, f3);
                  }
               } else {
                  worldIn.setBlockState(blockPos, blockState, 2);
                  worldIn.setBlockToAir(pos);
               }

               return;
            }
         }
      }
   }

   @Override
   public int tickRate(World worldIn) {
      return 5;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }
}
