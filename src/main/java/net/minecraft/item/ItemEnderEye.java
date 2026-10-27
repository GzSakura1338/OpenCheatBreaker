package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
   public ItemEnderEye() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack) || blockState.getBlock() != Blocks.end_portal_frame || blockState.getValue(BlockEndPortalFrame.EYE)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      worldIn.setBlockState(pos, blockState.withProperty(BlockEndPortalFrame.EYE, true), 2);
      worldIn.updateComparatorOutputLevel(pos, Blocks.end_portal_frame);
      stack.stackSize--;

      for (int i = 0; i < 16; i++) {
         double d = pos.getX() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double d2 = pos.getY() + 0.8125F;
         double d3 = pos.getZ() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
         double d4 = 0.0;
         double d5 = 0.0;
         double d6 = 0.0;
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, d4, d5, d6);
      }

      EnumFacing value = blockState.getValue(BlockEndPortalFrame.FACING);
      int n = 0;
      int n2 = 0;
      boolean flag = false;
      boolean flag2 = true;
      EnumFacing rotateY = value.rotateY();

      for (int i = -2; i <= 2; i++) {
         BlockPos offset = pos.offset(rotateY, i);
         IBlockState blockState2 = worldIn.getBlockState(offset);
         if (blockState2.getBlock() == Blocks.end_portal_frame) {
            if (!blockState2.getValue(BlockEndPortalFrame.EYE)) {
               flag2 = false;
               break;
            }

            n2 = i;
            if (!flag) {
               n = i;
               flag = true;
            }
         }
      }

      if (flag2 && n2 == n + 2) {
         BlockPos offset = pos.offset(value, 4);

         for (int i = n; i <= n2; i++) {
            BlockPos offset2 = offset.offset(rotateY, i);
            IBlockState blockState2 = worldIn.getBlockState(offset2);
            if (blockState2.getBlock() != Blocks.end_portal_frame || !blockState2.getValue(BlockEndPortalFrame.EYE)) {
               flag2 = false;
               break;
            }
         }

         for (int i = n - 1; i <= n2 + 1; i += 4) {
            offset = pos.offset(rotateY, i);

            for (int j = 1; j <= 3; j++) {
               BlockPos offset2 = offset.offset(value, j);
               IBlockState blockState2 = worldIn.getBlockState(offset2);
               if (blockState2.getBlock() != Blocks.end_portal_frame || !blockState2.getValue(BlockEndPortalFrame.EYE)) {
                  flag2 = false;
                  break;
               }
            }
         }

         if (flag2) {
            for (int i = n; i <= n2; i++) {
               offset = pos.offset(rotateY, i);

               for (int j = 1; j <= 3; j++) {
                  BlockPos offset2 = offset.offset(value, j);
                  worldIn.setBlockState(offset2, Blocks.end_portal.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);
      if (movingObjectPositionFromPlayer != null
         && movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK
         && worldIn.getBlockState(movingObjectPositionFromPlayer.getBlockPos()).getBlock() == Blocks.end_portal_frame) {
         return itemStackIn;
      }

      if (!worldIn.isRemote) {
         BlockPos strongholdPos = worldIn.getStrongholdPos("Stronghold", new BlockPos(playerIn));
         if (strongholdPos != null) {
            EntityEnderEye entityEnderEye = new EntityEnderEye(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);
            entityEnderEye.moveTowards(strongholdPos);
            worldIn.spawnEntityInWorld(entityEnderEye);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1002, new BlockPos(playerIn), 0);
            if (!playerIn.capabilities.isCreativeMode) {
               itemStackIn.stackSize--;
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return itemStackIn;
   }
}
