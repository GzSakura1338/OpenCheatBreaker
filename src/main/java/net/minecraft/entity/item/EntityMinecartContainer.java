package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer {
   public ItemStack[] minecartContainerItems = new ItemStack[36];
   public boolean dropContentsWhenDead = true;

   public EntityMinecartContainer(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartContainer(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         InventoryHelper.dropInventoryItems(this.worldObj, this, this);
      }
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.minecartContainerItems[index];
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.minecartContainerItems[index] != null) {
         if (this.minecartContainerItems[index].stackSize <= count) {
            ItemStack itemStack = this.minecartContainerItems[index];
            this.minecartContainerItems[index] = null;
            return itemStack;
         }

         ItemStack splitStack = this.minecartContainerItems[index].splitStack(count);
         if (this.minecartContainerItems[index].stackSize == 0) {
            this.minecartContainerItems[index] = null;
         }

         return splitStack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.minecartContainerItems[index] != null) {
         ItemStack itemStack = this.minecartContainerItems[index];
         this.minecartContainerItems[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.minecartContainerItems[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.isDead ? false : player.getDistanceSqToEntity(this) <= 64.0;
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

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void travelToDimension(int dimensionId) {
      this.dropContentsWhenDead = false;
      super.travelToDimension(dimensionId);
   }

   @Override
   public void setDead() {
      if (this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.worldObj, this, this);
      }

      super.setDead();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < this.minecartContainerItems.length; i++) {
         if (this.minecartContainerItems[i] != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Slot", (byte)i);
            this.minecartContainerItems[i].writeToNBT(nbttagCompound);
            nbttagList.appendTag(nbttagCompound);
         }
      }

      tagCompound.setTag("Items", nbttagList);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      NBTTagList tagList = tagCompund.getTagList("Items", 10);
      this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         int n = compoundTagAt.getByte("Slot") & 255;
         if (n >= 0 && n < this.minecartContainerItems.length) {
            this.minecartContainerItems[n] = ItemStack.loadItemStackFromNBT(compoundTagAt);
         }
      }
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (!this.worldObj.isRemote) {
         playerIn.displayGUIChest(this);
      }

      return true;
   }

   @Override
   public void applyDrag() {
      int n = 15 - Container.calcRedstoneFromInventory(this);
      float f = 0.98F + n * 0.001F;
      this.motionX *= f;
      this.motionY *= 0.0;
      this.motionZ *= f;
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
   public boolean isLocked() {
      return false;
   }

   @Override
   public void setLockCode(LockCode code) {
   }

   @Override
   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.minecartContainerItems.length; i++) {
         this.minecartContainerItems[i] = null;
      }
   }
}
