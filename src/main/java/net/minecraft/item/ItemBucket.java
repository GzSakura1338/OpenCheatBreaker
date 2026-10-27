package net.minecraft.item;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   public Block isFull;

   public ItemBucket(Block containedBlock) {
      this.maxStackSize = 1;
      this.isFull = containedBlock;
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      boolean flag = this.isFull == Blocks.air;
      MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, flag);
      if (movingObjectPositionFromPlayer == null) {
         return itemStackIn;
      }

      if (movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPositionFromPlayer.getBlockPos();
         if (!worldIn.isBlockModifiable(playerIn, blockPos)) {
            return itemStackIn;
         }

         if (flag) {
            if (!playerIn.canPlayerEdit(blockPos.offset(movingObjectPositionFromPlayer.sideHit), movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
               return itemStackIn;
            }

            IBlockState blockState = worldIn.getBlockState(blockPos);
            Material material = blockState.getBlock().getMaterial();
            if (material == Material.water && blockState.getValue(BlockLiquid.LEVEL) == 0) {
               worldIn.setBlockToAir(blockPos);
               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return this.fillBucket(itemStackIn, playerIn, Items.water_bucket);
            }

            if (material == Material.lava && blockState.getValue(BlockLiquid.LEVEL) == 0) {
               worldIn.setBlockToAir(blockPos);
               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return this.fillBucket(itemStackIn, playerIn, Items.lava_bucket);
            }
         } else {
            if (this.isFull == Blocks.air) {
               return new ItemStack(Items.bucket);
            }

            BlockPos offset = blockPos.offset(movingObjectPositionFromPlayer.sideHit);
            if (!playerIn.canPlayerEdit(offset, movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
               return itemStackIn;
            }

            if ((
                  CheatBreaker.getInstance().getKeybindSettings().ghostLiquidFix.getBooleanValue()
                     ? this.checkWorld(worldIn, offset)
                     : this.tryPlaceContainedLiquid(worldIn, offset)
               )
               && !playerIn.capabilities.isCreativeMode) {
               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
               return new ItemStack(Items.bucket);
            }
         }
      }

      return itemStackIn;
   }

   public ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket) {
      if (player.capabilities.isCreativeMode) {
         return emptyBuckets;
      }

      if (--emptyBuckets.stackSize <= 0) {
         return new ItemStack(fullBucket);
      }

      if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
         player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket, 1, 0), false);
      }

      return emptyBuckets;
   }

   public boolean tryPlaceContainedLiquid(World worldIn, BlockPos pos) {
      if (this.isFull == Blocks.air) {
         return false;
      }

      Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
      boolean flag = !material.isSolid();
      if (!worldIn.isAirBlock(pos) && !flag) {
         return false;
      }

      if (worldIn.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
         int n = pos.getX();
         int n2 = pos.getY();
         int n3 = pos.getZ();
         worldIn.playSoundEffect(
            n + 0.5F, n2 + 0.5F, n3 + 0.5F, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F
         );

         for (int i = 0; i < 8; i++) {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, n + Math.random(), n2 + Math.random(), n3 + Math.random(), 0.0, 0.0, 0.0);
         }
      } else {
         if (!worldIn.isRemote && flag && !material.isLiquid()) {
            worldIn.destroyBlock(pos, true);
         }

         worldIn.setBlockState(pos, this.isFull.getDefaultState(), 3);
      }

      return true;
   }

   public boolean checkWorld(World world, BlockPos blockPos) {
      if (this.isFull == Blocks.air) {
         return false;
      }

      Material material = world.getBlockState(blockPos).getBlock().getMaterial();
      boolean flag = !material.isSolid();
      if (!world.isAirBlock(blockPos) && !flag) {
         return false;
      }

      if (world.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
         int n = blockPos.getX();
         int n2 = blockPos.getY();
         int n3 = blockPos.getZ();
         world.playSoundEffect(n + 0.5F, n2 + 0.5F, n3 + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

         for (int i = 0; i < 8; i++) {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, n + Math.random(), n2 + Math.random(), n3 + Math.random(), 0.0, 0.0, 0.0);
         }
      } else {
         if (!world.isRemote && flag && !material.isLiquid()) {
            world.destroyBlock(blockPos, true);
         }

         if (!world.isRemote) {
            world.setBlockState(blockPos, this.isFull.getDefaultState(), 3);
         }
      }

      return true;
   }
}
