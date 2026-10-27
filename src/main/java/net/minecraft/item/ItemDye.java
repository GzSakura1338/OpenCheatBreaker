package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemDye extends Item {
   public static int[] dyeColors = new int[]{
      1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
   };

   public ItemDye() {
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabMaterials);
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      int metadata = stack.getMetadata();
      return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(metadata).getUnlocalizedName();
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      EnumDyeColor byDyeDamage = EnumDyeColor.byDyeDamage(stack.getMetadata());
      if (byDyeDamage == EnumDyeColor.WHITE) {
         if (applyBonemeal(stack, worldIn, pos)) {
            if (!worldIn.isRemote) {
               worldIn.playAuxSFX(2005, pos, 0);
            }

            return true;
         }
      } else if (byDyeDamage == EnumDyeColor.BROWN) {
         IBlockState blockState = worldIn.getBlockState(pos);
         Block block = blockState.getBlock();
         if (block == Blocks.log && blockState.getValue(BlockPlanks.VARIANT) == EnumType.JUNGLE) {
            if (side == EnumFacing.DOWN) {
               return false;
            }

            if (side == EnumFacing.UP) {
               return false;
            }

            pos = pos.offset(side);
            if (worldIn.isAirBlock(pos)) {
               IBlockState onBlockPlaced = Blocks.cocoa.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
               worldIn.setBlockState(pos, onBlockPlaced, 2);
               if (!playerIn.capabilities.isCreativeMode) {
                  stack.stackSize--;
               }
            }

            return true;
         }
      }

      return false;
   }

   public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target) {
      IBlockState blockState = worldIn.getBlockState(target);
      if (blockState.getBlock() instanceof IGrowable) {
         IGrowable block = (IGrowable)blockState.getBlock();
         if (block.canGrow(worldIn, target, blockState, worldIn.isRemote)) {
            if (!worldIn.isRemote) {
               if (block.canUseBonemeal(worldIn, worldIn.rand, target, blockState)) {
                  block.grow(worldIn, worldIn.rand, target, blockState);
               }

               stack.stackSize--;
            }

            return true;
         }
      }

      return false;
   }

   public static void spawnBonemealParticles(World worldIn, BlockPos pos, int amount) {
      if (amount == 0) {
         amount = 15;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      if (block.getMaterial() != Material.air) {
         block.setBlockBoundsBasedOnState(worldIn, pos);

         for (int i = 0; i < amount; i++) {
            double d = itemRand.nextGaussian() * 0.02;
            double d2 = itemRand.nextGaussian() * 0.02;
            double d3 = itemRand.nextGaussian() * 0.02;
            worldIn.spawnParticle(
               EnumParticleTypes.VILLAGER_HAPPY,
               pos.getX() + itemRand.nextFloat(),
               pos.getY() + itemRand.nextFloat() * block.getBlockBoundsMaxY(),
               pos.getZ() + itemRand.nextFloat(),
               d,
               d2,
               d3
            );
         }
      }
   }

   @Override
   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target) {
      if (target instanceof EntitySheep) {
         EntitySheep target2 = (EntitySheep)target;
         EnumDyeColor byDyeDamage = EnumDyeColor.byDyeDamage(stack.getMetadata());
         if (!target2.getSheared() && target2.getFleeceColor() != byDyeDamage) {
            target2.setFleeceColor(byDyeDamage);
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (int i = 0; i < 16; i++) {
         subItems.add(new ItemStack(itemIn, 1, i));
      }
   }
}
