package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean fallInstantly;

   public BlockFalling() {
      super(Material.sand);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public BlockFalling(Material materialIn) {
      super(materialIn);
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
      if (!worldIn.isRemote) {
         this.checkFallable(worldIn, pos);
      }
   }

   public void checkFallable(World worldIn, BlockPos pos) {
      if (canFallInto(worldIn, pos.down()) && pos.getY() >= 0) {
         byte b = 32;
         if (fallInstantly || !worldIn.isAreaLoaded(pos.add(-b, -b, -b), pos.add(b, b, b))) {
            worldIn.setBlockToAir(pos);
            BlockPos down = pos.down();

            while (canFallInto(worldIn, down) && down.getY() > 0) {
               down = down.down();
            }

            if (down.getY() > 0) {
               worldIn.setBlockState(down.up(), this.getDefaultState());
            }
         } else if (!worldIn.isRemote) {
            EntityFallingBlock entityFallingBlock = new EntityFallingBlock(worldIn, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, worldIn.getBlockState(pos));
            this.onStartFalling(entityFallingBlock);
            worldIn.spawnEntityInWorld(entityFallingBlock);
         }
      }
   }

   public void onStartFalling(EntityFallingBlock fallingEntity) {
   }

   @Override
   public int tickRate(World worldIn) {
      return 2;
   }

   public static boolean canFallInto(World worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      Material blockMaterial = block.blockMaterial;
      return block == Blocks.fire || blockMaterial == Material.air || blockMaterial == Material.water || blockMaterial == Material.lava;
   }

   public void onEndFalling(World worldIn, BlockPos pos) {
   }
}
