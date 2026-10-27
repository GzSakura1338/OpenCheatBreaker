package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe> {
   public MerchantRecipeList() {
   }

   public MerchantRecipeList(NBTTagCompound compound) {
      this.readRecipiesFromTags(compound);
   }

   public MerchantRecipe canRecipeBeUsed(ItemStack itemStack, ItemStack itemStack2, int n) {
      if (n > 0 && n < this.size()) {
         MerchantRecipe merchantRecipe = this.get(n);
         return this.checkItemStack(itemStack, merchantRecipe.getItemToBuy())
               && (itemStack2 == null && !merchantRecipe.hasSecondItemToBuy() || merchantRecipe.hasSecondItemToBuy() && this.checkItemStack(itemStack2, merchantRecipe.getSecondItemToBuy()))
               && itemStack.stackSize >= merchantRecipe.getItemToBuy().stackSize
               && (!merchantRecipe.hasSecondItemToBuy() || itemStack2.stackSize >= merchantRecipe.getSecondItemToBuy().stackSize)
            ? merchantRecipe
            : null;
      }

      for (int i = 0; i < this.size(); i++) {
         MerchantRecipe merchantRecipe = this.get(i);
         if (this.checkItemStack(itemStack, merchantRecipe.getItemToBuy())
            && itemStack.stackSize >= merchantRecipe.getItemToBuy().stackSize
            && (
               !merchantRecipe.hasSecondItemToBuy() && itemStack2 == null
                  || merchantRecipe.hasSecondItemToBuy() && this.checkItemStack(itemStack2, merchantRecipe.getSecondItemToBuy()) && itemStack2.stackSize >= merchantRecipe.getSecondItemToBuy().stackSize
            )) {
            return merchantRecipe;
         }
      }

      return null;
   }

   public boolean checkItemStack(ItemStack itemStack, ItemStack itemStack2) {
      return ItemStack.areItemsEqual(itemStack, itemStack2)
         && (!itemStack2.hasTagCompound() || itemStack.hasTagCompound() && NBTUtil.matches(itemStack2.getTagCompound(), itemStack.getTagCompound(), false));
   }

   public void writeToBuf(PacketBuffer buffer) {
      buffer.writeByte((byte)(this.size() & 0xFF));

      for (int i = 0; i < this.size(); i++) {
         MerchantRecipe merchantRecipe = this.get(i);
         buffer.writeItemStackToBuffer(merchantRecipe.getItemToBuy());
         buffer.writeItemStackToBuffer(merchantRecipe.getItemToSell());
         ItemStack secondItemToBuy = merchantRecipe.getSecondItemToBuy();
         buffer.writeBoolean(secondItemToBuy != null);
         if (secondItemToBuy != null) {
            buffer.writeItemStackToBuffer(secondItemToBuy);
         }

         buffer.writeBoolean(merchantRecipe.isRecipeDisabled());
         buffer.writeInt(merchantRecipe.getToolUses());
         buffer.writeInt(merchantRecipe.getMaxTradeUses());
      }
   }

   public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
      MerchantRecipeList merchantRecipeList = new MerchantRecipeList();
      int n = buffer.readByte() & 255;

      for (int i = 0; i < n; i++) {
         ItemStack readItemStackFromBuffer = buffer.readItemStackFromBuffer();
         ItemStack readItemStackFromBuffer2 = buffer.readItemStackFromBuffer();
         ItemStack itemStack = null;
         if (buffer.readBoolean()) {
            itemStack = buffer.readItemStackFromBuffer();
         }

         boolean readBoolean = buffer.readBoolean();
         int readInt = buffer.readInt();
         int readInt2 = buffer.readInt();
         MerchantRecipe merchantRecipe = new MerchantRecipe(readItemStackFromBuffer, itemStack, readItemStackFromBuffer2, readInt, readInt2);
         if (readBoolean) {
            merchantRecipe.compensateToolUses();
         }

         merchantRecipeList.add(merchantRecipe);
      }

      return merchantRecipeList;
   }

   public void readRecipiesFromTags(NBTTagCompound compound) {
      NBTTagList tagList = compound.getTagList("Recipes", 10);

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         this.add(new MerchantRecipe(compoundTagAt));
      }
   }

   public NBTTagCompound getRecipiesAsTags() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < this.size(); i++) {
         MerchantRecipe merchantRecipe = this.get(i);
         nbttagList.appendTag(merchantRecipe.writeToTags());
      }

      nbttagCompound.setTag("Recipes", nbttagList);
      return nbttagCompound;
   }
}
