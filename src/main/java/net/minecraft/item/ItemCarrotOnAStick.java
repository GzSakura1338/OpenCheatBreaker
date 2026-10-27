package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {
   public ItemCarrotOnAStick() {
      this.setCreativeTab(CreativeTabs.tabTransport);
      this.setMaxStackSize(1);
      this.setMaxDamage(25);
   }

   @Override
   public boolean isFull3D() {
      return true;
   }

   @Override
   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (playerIn.isRiding() && playerIn.ridingEntity instanceof EntityPig) {
         EntityPig ridingEntity = (EntityPig)playerIn.ridingEntity;
         if (ridingEntity.getAIControlledByPlayer().isControlledByPlayer() && itemStackIn.getMaxDamage() - itemStackIn.getMetadata() >= 7) {
            ridingEntity.getAIControlledByPlayer().boostSpeed();
            itemStackIn.damageItem(7, playerIn);
            if (itemStackIn.stackSize == 0) {
               ItemStack itemStack = new ItemStack(Items.fishing_rod);
               itemStack.setTagCompound(itemStackIn.getTagCompound());
               return itemStack;
            }
         }
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }
}
