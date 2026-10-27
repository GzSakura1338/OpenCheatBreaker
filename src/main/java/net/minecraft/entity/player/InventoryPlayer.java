package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory {
   public ItemStack[] mainInventory = new ItemStack[36];
   public ItemStack[] armorInventory = new ItemStack[4];
   public int currentItem;
   public EntityPlayer player;
   public ItemStack itemStack;
   public boolean inventoryChanged;

   public InventoryPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   public ItemStack getCurrentItem() {
      return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
   }

   public static int getHotbarSize() {
      return 9;
   }

   public int getInventorySlotContainItem(Item itemIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn) {
            return i;
         }
      }

      return -1;
   }

   public int getInventorySlotContainItemAndDamage(Item itemIn, int metadataIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn && this.mainInventory[i].getMetadata() == metadataIn) {
            return i;
         }
      }

      return -1;
   }

   public int storeItemStack(ItemStack itemStackIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null
            && this.mainInventory[i].getItem() == itemStackIn.getItem()
            && this.mainInventory[i].isStackable()
            && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize()
            && this.mainInventory[i].stackSize < this.getInventoryStackLimit()
            && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getMetadata() == itemStackIn.getMetadata())
            && ItemStack.areItemStackTagsEqual(this.mainInventory[i], itemStackIn)) {
            return i;
         }
      }

      return -1;
   }

   public int getFirstEmptyStack() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] == null) {
            return i;
         }
      }

      return -1;
   }

   public void setCurrentItem(Item itemIn, int metadataIn, boolean isMetaSpecific, boolean flag) {
      ItemStack currentItem = this.getCurrentItem();
      int n = isMetaSpecific ? this.getInventorySlotContainItemAndDamage(itemIn, metadataIn) : this.getInventorySlotContainItem(itemIn);
      if (n >= 0 && n < 9) {
         this.currentItem = n;
      } else if (flag && itemIn != null) {
         int firstEmptyStack = this.getFirstEmptyStack();
         if (firstEmptyStack >= 0 && firstEmptyStack < 9) {
            this.currentItem = firstEmptyStack;
         }

         if (currentItem == null || !currentItem.isItemEnchantable() || this.getInventorySlotContainItemAndDamage(currentItem.getItem(), currentItem.getItemDamage()) != this.currentItem) {
            int n2 = this.getInventorySlotContainItemAndDamage(itemIn, metadataIn);
            int n3;
            if (n2 >= 0) {
               n3 = this.mainInventory[n2].stackSize;
               this.mainInventory[n2] = this.mainInventory[this.currentItem];
            } else {
               n3 = 1;
            }

            this.mainInventory[this.currentItem] = new ItemStack(itemIn, n3, metadataIn);
         }
      }
   }

   public void changeCurrentItem(int direction) {
      if (direction > 0) {
         direction = 1;
      }

      if (direction < 0) {
         direction = -1;
      }

      this.currentItem -= direction;

      while (this.currentItem < 0) {
         this.currentItem += 9;
      }

      while (this.currentItem >= 9) {
         this.currentItem -= 9;
      }
   }

   public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT) {
      int n = 0;

      for (int i = 0; i < this.mainInventory.length; i++) {
         ItemStack itemStack = this.mainInventory[i];
         if (itemStack != null
            && (itemIn == null || itemStack.getItem() == itemIn)
            && (metadataIn <= -1 || itemStack.getMetadata() == metadataIn)
            && (itemNBT == null || NBTUtil.matches(itemNBT, itemStack.getTagCompound(), true))) {
            int n2 = removeCount <= 0 ? itemStack.stackSize : Math.min(removeCount - n, itemStack.stackSize);
            n += n2;
            if (removeCount != 0) {
               this.mainInventory[i].stackSize -= n2;
               if (this.mainInventory[i].stackSize == 0) {
                  this.mainInventory[i] = null;
               }

               if (removeCount > 0 && n >= removeCount) {
                  return n;
               }
            }
         }
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         ItemStack itemStack = this.armorInventory[i];
         if (itemStack != null
            && (itemIn == null || itemStack.getItem() == itemIn)
            && (metadataIn <= -1 || itemStack.getMetadata() == metadataIn)
            && (itemNBT == null || NBTUtil.matches(itemNBT, itemStack.getTagCompound(), false))) {
            int n2 = removeCount <= 0 ? itemStack.stackSize : Math.min(removeCount - n, itemStack.stackSize);
            n += n2;
            if (removeCount != 0) {
               this.armorInventory[i].stackSize -= n2;
               if (this.armorInventory[i].stackSize == 0) {
                  this.armorInventory[i] = null;
               }

               if (removeCount > 0 && n >= removeCount) {
                  return n;
               }
            }
         }
      }

      if (this.itemStack != null) {
         if (itemIn != null && this.itemStack.getItem() != itemIn) {
            return n;
         }

         if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
            return n;
         }

         if (itemNBT != null && !NBTUtil.matches(itemNBT, this.itemStack.getTagCompound(), false)) {
            return n;
         }

         int n2 = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - n, this.itemStack.stackSize);
         n += n2;
         if (removeCount != 0) {
            this.itemStack.stackSize -= n2;
            if (this.itemStack.stackSize == 0) {
               this.itemStack = null;
            }

            if (removeCount > 0 && n >= removeCount) {
               return n;
            }
         }
      }

      return n;
   }

   public int storePartialItemStack(ItemStack itemStackIn) {
      Item item = itemStackIn.getItem();
      int stackSize = itemStackIn.stackSize;
      int storeItemStack = this.storeItemStack(itemStackIn);
      if (storeItemStack < 0) {
         storeItemStack = this.getFirstEmptyStack();
      }

      if (storeItemStack < 0) {
         return stackSize;
      }

      if (this.mainInventory[storeItemStack] == null) {
         this.mainInventory[storeItemStack] = new ItemStack(item, 0, itemStackIn.getMetadata());
         if (itemStackIn.hasTagCompound()) {
            this.mainInventory[storeItemStack].setTagCompound((NBTTagCompound)itemStackIn.getTagCompound().copy());
         }
      }

      int n = stackSize;
      if (stackSize > this.mainInventory[storeItemStack].getMaxStackSize() - this.mainInventory[storeItemStack].stackSize) {
         n = this.mainInventory[storeItemStack].getMaxStackSize() - this.mainInventory[storeItemStack].stackSize;
      }

      if (n > this.getInventoryStackLimit() - this.mainInventory[storeItemStack].stackSize) {
         n = this.getInventoryStackLimit() - this.mainInventory[storeItemStack].stackSize;
      }

      if (n == 0) {
         return stackSize;
      }

      stackSize -= n;
      this.mainInventory[storeItemStack].stackSize += n;
      this.mainInventory[storeItemStack].animationsToGo = 5;
      return stackSize;
   }

   public void decrementAnimations() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
         }
      }
   }

   public boolean consumeInventoryItem(Item itemIn) {
      int inventorySlotContainItem = this.getInventorySlotContainItem(itemIn);
      if (inventorySlotContainItem < 0) {
         return false;
      }

      if (--this.mainInventory[inventorySlotContainItem].stackSize <= 0) {
         this.mainInventory[inventorySlotContainItem] = null;
      }

      return true;
   }

   public boolean hasItem(Item itemIn) {
      int inventorySlotContainItem = this.getInventorySlotContainItem(itemIn);
      return inventorySlotContainItem >= 0;
   }

   public boolean addItemStackToInventory(final ItemStack itemStackIn) {
      if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
         try {
            if (itemStackIn.isItemDamaged()) {
               int firstEmptyStack = this.getFirstEmptyStack();
               if (firstEmptyStack >= 0) {
                  this.mainInventory[firstEmptyStack] = ItemStack.copyItemStack(itemStackIn);
                  this.mainInventory[firstEmptyStack].animationsToGo = 5;
                  itemStackIn.stackSize = 0;
                  return true;
               } else if (this.player.capabilities.isCreativeMode) {
                  itemStackIn.stackSize = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               int n;
               do {
                  n = itemStackIn.stackSize;
                  itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);
               } while (itemStackIn.stackSize > 0 && itemStackIn.stackSize < n);

               if (itemStackIn.stackSize == n && this.player.capabilities.isCreativeMode) {
                  itemStackIn.stackSize = 0;
                  return true;
               } else {
                  return itemStackIn.stackSize < n;
               }
            }
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Adding item to inventory");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Item being added");
            makeCategory.addCrashSection("Item ID", Item.getIdFromItem(itemStackIn.getItem()));
            makeCategory.addCrashSection("Item data", itemStackIn.getMetadata());
            makeCategory.addCrashSectionCallable("Item name", new Callable<String>() {
               public String call() throws Exception {
                  return itemStackIn.getDisplayName();
               }
            });
            throw new ReportedException(makeCrashReport);
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      ItemStack[] mainInventory = this.mainInventory;
      if (index >= this.mainInventory.length) {
         mainInventory = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (mainInventory[index] != null) {
         if (mainInventory[index].stackSize <= count) {
            ItemStack itemStack = mainInventory[index];
            mainInventory[index] = null;
            return itemStack;
         }

         ItemStack splitStack = mainInventory[index].splitStack(count);
         if (mainInventory[index].stackSize == 0) {
            mainInventory[index] = null;
         }

         return splitStack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      ItemStack[] mainInventory = this.mainInventory;
      if (index >= this.mainInventory.length) {
         mainInventory = this.armorInventory;
         index -= this.mainInventory.length;
      }

      if (mainInventory[index] != null) {
         ItemStack itemStack = mainInventory[index];
         mainInventory[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      ItemStack[] mainInventory = this.mainInventory;
      if (index >= mainInventory.length) {
         index -= mainInventory.length;
         mainInventory = this.armorInventory;
      }

      mainInventory[index] = stack;
   }

   public float getStrVsBlock(Block blockIn) {
      float f = 1.0F;
      if (this.mainInventory[this.currentItem] != null) {
         f *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
      }

      return f;
   }

   public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Slot", (byte)i);
            this.mainInventory[i].writeToNBT(nbttagCompound);
            nbtTagListIn.appendTag(nbttagCompound);
         }
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Slot", (byte)(i + 100));
            this.armorInventory[i].writeToNBT(nbttagCompound);
            nbtTagListIn.appendTag(nbttagCompound);
         }
      }

      return nbtTagListIn;
   }

   public void readFromNBT(NBTTagList nbtTagListIn) {
      this.mainInventory = new ItemStack[36];
      this.armorInventory = new ItemStack[4];

      for (int i = 0; i < nbtTagListIn.tagCount(); i++) {
         NBTTagCompound compoundTagAt = nbtTagListIn.getCompoundTagAt(i);
         int n = compoundTagAt.getByte("Slot") & 255;
         ItemStack loadItemStackFromNBT = ItemStack.loadItemStackFromNBT(compoundTagAt);
         if (loadItemStackFromNBT != null) {
            if (n >= 0 && n < this.mainInventory.length) {
               this.mainInventory[n] = loadItemStackFromNBT;
            }

            if (n >= 100 && n < this.armorInventory.length + 100) {
               this.armorInventory[n - 100] = loadItemStackFromNBT;
            }
         }
      }
   }

   @Override
   public int getSizeInventory() {
      return this.mainInventory.length + 4;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      ItemStack[] mainInventory = this.mainInventory;
      if (index >= mainInventory.length) {
         index -= mainInventory.length;
         mainInventory = this.armorInventory;
      }

      return mainInventory[index];
   }

   @Override
   public String getName() {
      return "container.inventory";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean canHeldItemHarvest(Block blockIn) {
      if (blockIn.getMaterial().isToolNotRequired()) {
         return true;
      }

      ItemStack stackInSlot = this.getStackInSlot(this.currentItem);
      return stackInSlot != null ? stackInSlot.canHarvestBlock(blockIn) : false;
   }

   public ItemStack armorItemInSlot(int slotIn) {
      return this.armorInventory[slotIn];
   }

   public int getTotalArmorValue() {
      int n = 0;

      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
            int damageReduceAmount = ((ItemArmor)this.armorInventory[i].getItem()).damageReduceAmount;
            n += damageReduceAmount;
         }
      }

      return n;
   }

   public void damageArmor(float damage) {
      damage /= 4.0F;
      if (damage < 1.0F) {
         damage = 1.0F;
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
            this.armorInventory[i].damageItem((int)damage, this.player);
            if (this.armorInventory[i].stackSize == 0) {
               this.armorInventory[i] = null;
            }
         }
      }
   }

   public void dropAllItems() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null) {
            this.player.dropItem(this.mainInventory[i], true, false);
            this.mainInventory[i] = null;
         }
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null) {
            this.player.dropItem(this.armorInventory[i], true, false);
            this.armorInventory[i] = null;
         }
      }
   }

   @Override
   public void markDirty() {
      this.inventoryChanged = true;
   }

   public void setItemStack(ItemStack itemStackIn) {
      this.itemStack = itemStackIn;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0;
   }

   public boolean hasItemStack(ItemStack itemStackIn) {
      for (int i = 0; i < this.armorInventory.length; i++) {
         if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(itemStackIn)) {
            return true;
         }
      }

      for (int i = 0; i < this.mainInventory.length; i++) {
         if (this.mainInventory[i] != null && this.mainInventory[i].isItemEqual(itemStackIn)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public void copyInventory(InventoryPlayer playerInventory) {
      for (int i = 0; i < this.mainInventory.length; i++) {
         this.mainInventory[i] = ItemStack.copyItemStack(playerInventory.mainInventory[i]);
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         this.armorInventory[i] = ItemStack.copyItemStack(playerInventory.armorInventory[i]);
      }

      this.currentItem = playerInventory.currentItem;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.mainInventory.length; i++) {
         this.mainInventory[i] = null;
      }

      for (int i = 0; i < this.armorInventory.length; i++) {
         this.armorInventory[i] = null;
      }
   }
}
