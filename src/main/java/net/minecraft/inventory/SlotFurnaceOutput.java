package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnaceOutput extends Slot {
   public EntityPlayer thePlayer;
   public int amount;

   public SlotFurnaceOutput(EntityPlayer player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
      super(inventoryIn, slotIndex, xPosition, yPosition);
      this.thePlayer = player;
   }

   @Override
   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   @Override
   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.amount = this.amount + Math.min(amount, this.getStack().stackSize);
      }

      return super.decrStackSize(amount);
   }

   @Override
   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onCrafting(stack);
      super.onPickupFromSlot(playerIn, stack);
   }

   @Override
   public void onCrafting(ItemStack stack, int amount) {
      this.amount += amount;
      this.onCrafting(stack);
   }

   @Override
   public void onCrafting(ItemStack stack) {
      stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amount);
      if (!this.thePlayer.worldObj.isRemote) {
         int amount = this.amount;
         float smeltingExperience = FurnaceRecipes.instance().getSmeltingExperience(stack);
         if (smeltingExperience == 0.0F) {
            amount = 0;
         } else if (smeltingExperience < 1.0F) {
            int n = MathHelper.floor_float(amount * smeltingExperience);
            if (n < MathHelper.ceiling_float_int(amount * smeltingExperience) && Math.random() < amount * smeltingExperience - n) {
               n++;
            }

            amount = n;
         }

         while (amount > 0) {
            int xPSplit = EntityXPOrb.getXPSplit(amount);
            amount -= xPSplit;
            this.thePlayer
               .worldObj
               .spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5, this.thePlayer.posZ + 0.5, xPSplit));
         }
      }

      this.amount = 0;
      if (stack.getItem() == Items.iron_ingot) {
         this.thePlayer.triggerAchievement(AchievementList.acquireIron);
      }

      if (stack.getItem() == Items.cooked_fish) {
         this.thePlayer.triggerAchievement(AchievementList.cookFish);
      }
   }
}
