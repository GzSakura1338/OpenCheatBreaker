package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   public ItemWritableBook() {
      this.setMaxStackSize(1);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      playerIn.displayGUIBook(itemStackIn);
      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }

   public static boolean isNBTValid(NBTTagCompound nbt) {
      if (nbt == null) {
         return false;
      }

      if (!nbt.hasKey("pages", 9)) {
         return false;
      }

      NBTTagList tagList = nbt.getTagList("pages", 8);

      for (int i = 0; i < tagList.tagCount(); i++) {
         String stringTagAt = tagList.getStringTagAt(i);
         if (stringTagAt == null) {
            return false;
         }

         if (stringTagAt.length() > 32767) {
            return false;
         }
      }

      return true;
   }
}
