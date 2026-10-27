package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class BlockSponge extends Block {
   public static PropertyBool WET = PropertyBool.create("wet");

   public BlockSponge() {
      super(Material.sponge);
      this.setDefaultState(this.blockState.getBaseState().withProperty(WET, false));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".dry.name");
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(WET) ? 1 : 0;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.tryAbsorb(worldIn, pos, state);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.tryAbsorb(worldIn, pos, state);
      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
   }

   public void tryAbsorb(World worldIn, BlockPos pos, IBlockState state) {
      if (!state.getValue(WET) && this.absorb(worldIn, pos)) {
         worldIn.setBlockState(pos, state.withProperty(WET, true), 2);
         worldIn.playAuxSFX(2001, pos, Block.getIdFromBlock(Blocks.water));
      }
   }

   public boolean absorb(World worldIn, BlockPos pos) {
      LinkedList newLinkedList = Lists.newLinkedList();
      ArrayList newArrayList = Lists.newArrayList();
      newLinkedList.add(new Tuple<>(pos, 0));
      int n = 0;

      while (!newLinkedList.isEmpty()) {
         Tuple poll = (Tuple)newLinkedList.poll();
         BlockPos first = (BlockPos)poll.getFirst();
         int second = (Integer)poll.getSecond();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos offset = first.offset(enumFacing);
            if (worldIn.getBlockState(offset).getBlock().getMaterial() == Material.water) {
               worldIn.setBlockState(offset, Blocks.air.getDefaultState(), 2);
               newArrayList.add(offset);
               n++;
               if (second < 6) {
                  newLinkedList.add(new Tuple<>(offset, second + 1));
               }
            }
         }

         if (n > 64) {
            break;
         }
      }

      for (BlockPos blockPos : (Iterable<BlockPos>)newArrayList) {
         worldIn.notifyNeighborsOfStateChange(blockPos, Blocks.air);
      }

      return n > 0;
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, 0));
      list.add(new ItemStack(itemIn, 1, 1));
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(WET, (meta & 1) == 1);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(WET) ? 1 : 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, WET);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (state.getValue(WET)) {
         EnumFacing random = EnumFacing.random(rand);
         if (random != EnumFacing.UP && !World.doesBlockHaveSolidTopSurface(worldIn, pos.offset(random))) {
            double d = pos.getX();
            double d2 = pos.getY();
            double d3 = pos.getZ();
            if (random == EnumFacing.DOWN) {
               d2 -= 0.05;
               d += rand.nextDouble();
               d3 += rand.nextDouble();
            } else {
               d2 += rand.nextDouble() * 0.8;
               if (random.getAxis() == Axis.X) {
                  d3 += rand.nextDouble();
                  if (random == EnumFacing.EAST) {
                     d++;
                  } else {
                     d += 0.05;
                  }
               } else {
                  d += rand.nextDouble();
                  if (random == EnumFacing.SOUTH) {
                     d3++;
                  } else {
                     d3 += 0.05;
                  }
               }
            }

            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d, d2, d3, 0.0, 0.0, 0.0);
         }
      }
   }
}
