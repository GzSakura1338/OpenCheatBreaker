package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
   public static Map<World, List<BlockRedstoneTorch.Toggle>> toggles = Maps.newHashMap();
   public boolean isOn;

   public boolean isBurnedOut(World worldIn, BlockPos pos, boolean turnOff) {
      if (!toggles.containsKey(worldIn)) {
         toggles.put(worldIn, Lists.newArrayList());
      }

      List list = toggles.get(worldIn);
      if (turnOff) {
         list.add(new BlockRedstoneTorch.Toggle(pos, worldIn.getTotalWorldTime()));
      }

      int n = 0;

      for (int i = 0; i < list.size(); i++) {
         BlockRedstoneTorch.Toggle toggle = (BlockRedstoneTorch.Toggle)list.get(i);
         if (toggle.pos.equals(pos)) {
            if (++n >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   public BlockRedstoneTorch(boolean isOn) {
      this.isOn = isOn;
      this.setTickRandomly(true);
      this.setCreativeTab((CreativeTabs)null);
   }

   @Override
   public int tickRate(World worldIn) {
      return 2;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (this.isOn) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.isOn) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.isOn && state.getValue(FACING) != side ? 15 : 0;
   }

   public boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing opposite = state.getValue(FACING).getOpposite();
      return worldIn.isSidePowered(pos.offset(opposite), opposite);
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      boolean shouldBeOff = this.shouldBeOff(worldIn, pos, state);
      List list = toggles.get(worldIn);

      while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - ((BlockRedstoneTorch.Toggle)list.get(0)).time > 60L) {
         list.remove(0);
      }

      if (this.isOn) {
         if (shouldBeOff) {
            worldIn.setBlockState(pos, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
            if (this.isBurnedOut(worldIn, pos, true)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  "random.fizz",
                  0.5F,
                  2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F
               );

               for (int i = 0; i < 5; i++) {
                  double d = pos.getX() + rand.nextDouble() * 0.6 + 0.2;
                  double d2 = pos.getY() + rand.nextDouble() * 0.6 + 0.2;
                  double d3 = pos.getZ() + rand.nextDouble() * 0.6 + 0.2;
                  worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, 0.0, 0.0, 0.0);
               }

               worldIn.scheduleUpdate(pos, worldIn.getBlockState(pos).getBlock(), 160);
            }
         }
      } else if (!shouldBeOff && !this.isBurnedOut(worldIn, pos, false)) {
         worldIn.setBlockState(pos, Blocks.redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.onNeighborChangeInternal(worldIn, pos, state) && this.isOn == this.shouldBeOff(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return side == EnumFacing.DOWN ? this.getWeakPower(worldIn, pos, state, side) : 0;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.redstone_torch);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isOn) {
         double d = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
         double d2 = pos.getY() + 0.7 + (rand.nextDouble() - 0.5) * 0.2;
         double d3 = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
         EnumFacing value = state.getValue(FACING);
         if (value.getAxis().isHorizontal()) {
            EnumFacing opposite = value.getOpposite();
            double d4 = 0.27;
            d += 0.27 * opposite.getFrontOffsetX();
            d2 += 0.22;
            d3 += 0.27 * opposite.getFrontOffsetZ();
         }

         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d, d2, d3, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.redstone_torch);
   }

   @Override
   public boolean isAssociatedBlock(Block other) {
      return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
   }

   static class Toggle {
      public BlockPos pos;
      public long time;

      public Toggle(BlockPos pos, long time) {
         this.pos = pos;
         this.time = time;
      }
   }
}
