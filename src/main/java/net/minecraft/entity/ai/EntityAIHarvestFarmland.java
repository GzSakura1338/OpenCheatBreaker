package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock {
   public EntityVillager theVillager;
   public boolean hasFarmItem;
   public boolean flag;
   public int intValue;

   public EntityAIHarvestFarmland(EntityVillager theVillagerIn, double speedIn) {
      super(theVillagerIn, speedIn, 16);
      this.theVillager = theVillagerIn;
   }

   @Override
   public boolean shouldExecute() {
      if (this.runDelay <= 0) {
         if (!this.theVillager.worldObj.getGameRules().getBoolean("mobGriefing")) {
            return false;
         }

         this.intValue = -1;
         this.hasFarmItem = this.theVillager.isFarmItemInInventory();
         this.flag = this.theVillager.check();
      }

      return super.shouldExecute();
   }

   @Override
   public boolean continueExecuting() {
      return this.intValue >= 0 && super.continueExecuting();
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
   }

   @Override
   public void resetTask() {
      super.resetTask();
   }

   @Override
   public void updateTask() {
      super.updateTask();
      this.theVillager
         .getLookHelper()
         .setLookPosition(
            this.destinationBlock.getX() + 0.5,
            this.destinationBlock.getY() + 1,
            this.destinationBlock.getZ() + 0.5,
            10.0F,
            this.theVillager.getVerticalFaceSpeed()
         );
      if (this.getIsAboveDestination()) {
         World worldObj = this.theVillager.worldObj;
         BlockPos up = this.destinationBlock.up();
         IBlockState blockState = worldObj.getBlockState(up);
         Block block = blockState.getBlock();
         if (this.intValue == 0 && block instanceof BlockCrops && blockState.getValue(BlockCrops.AGE) == 7) {
            worldObj.destroyBlock(up, true);
         } else if (this.intValue == 1 && block == Blocks.air) {
            InventoryBasic villagerInventory = this.theVillager.getVillagerInventory();

            for (int i = 0; i < villagerInventory.getSizeInventory(); i++) {
               ItemStack stackInSlot = villagerInventory.getStackInSlot(i);
               boolean flag = false;
               if (stackInSlot != null) {
                  if (stackInSlot.getItem() == Items.wheat_seeds) {
                     worldObj.setBlockState(up, Blocks.wheat.getDefaultState(), 3);
                     flag = true;
                  } else if (stackInSlot.getItem() == Items.potato) {
                     worldObj.setBlockState(up, Blocks.potatoes.getDefaultState(), 3);
                     flag = true;
                  } else if (stackInSlot.getItem() == Items.carrot) {
                     worldObj.setBlockState(up, Blocks.carrots.getDefaultState(), 3);
                     flag = true;
                  }
               }

               if (flag) {
                  stackInSlot.stackSize--;
                  if (stackInSlot.stackSize <= 0) {
                     villagerInventory.setInventorySlotContents(i, (ItemStack)null);
                  }
                  break;
               }
            }
         }

         this.intValue = -1;
         this.runDelay = 10;
      }
   }

   @Override
   public boolean shouldMoveTo(World worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      if (block == Blocks.farmland) {
         pos = pos.up();
         IBlockState blockState = worldIn.getBlockState(pos);
         block = blockState.getBlock();
         if (block instanceof BlockCrops && blockState.getValue(BlockCrops.AGE) == 7 && this.flag && (this.intValue == 0 || this.intValue < 0)) {
            this.intValue = 0;
            return true;
         }

         if (block == Blocks.air && this.hasFarmItem && (this.intValue == 1 || this.intValue < 0)) {
            this.intValue = 1;
            return true;
         }
      }

      return false;
   }
}
