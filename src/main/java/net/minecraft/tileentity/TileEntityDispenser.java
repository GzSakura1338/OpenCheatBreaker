package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntityLockable implements IInventory {
   public static Random RNG = new Random();
   public ItemStack[] stacks = new ItemStack[9];
   public String customName;

   @Override
   public int getSizeInventory() {
      return 9;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.stacks[index];
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.stacks[index] != null) {
         if (this.stacks[index].stackSize <= count) {
            ItemStack itemStack = this.stacks[index];
            this.stacks[index] = null;
            this.markDirty();
            return itemStack;
         }

         ItemStack splitStack = this.stacks[index].splitStack(count);
         if (this.stacks[index].stackSize == 0) {
            this.stacks[index] = null;
         }

         this.markDirty();
         return splitStack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.stacks[index] != null) {
         ItemStack itemStack = this.stacks[index];
         this.stacks[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   public int getDispenseSlot() {
      int n = -1;
      int n2 = 1;

      for (int i = 0; i < this.stacks.length; i++) {
         if (this.stacks[i] != null && RNG.nextInt(n2++) == 0) {
            n = i;
         }
      }

      return n;
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stacks[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   public int addItemStack(ItemStack stack) {
      for (int i = 0; i < this.stacks.length; i++) {
         if (this.stacks[i] == null || this.stacks[i].getItem() == null) {
            this.setInventorySlotContents(i, stack);
            return i;
         }
      }

      return -1;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.dispenser";
   }

   public void setCustomName(String customName) {
      this.customName = customName;
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList tagList = compound.getTagList("Items", 10);
      this.stacks = new ItemStack[this.getSizeInventory()];

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         int n = compoundTagAt.getByte("Slot") & 255;
         if (n >= 0 && n < this.stacks.length) {
            this.stacks[n] = ItemStack.loadItemStackFromNBT(compoundTagAt);
         }
      }

      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < this.stacks.length; i++) {
         if (this.stacks[i] != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Slot", (byte)i);
            this.stacks[i].writeToNBT(nbttagCompound);
            nbttagList.appendTag(nbttagCompound);
         }
      }

      compound.setTag("Items", nbttagList);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
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
   public String getGuiID() {
      return "minecraft:dispenser";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerDispenser(playerInventory, this);
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
      for (int i = 0; i < this.stacks.length; i++) {
         this.stacks[i] = null;
      }
   }
}
