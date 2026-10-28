package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityDispenser extends TileEntity implements IInventory {
    public ItemStack[] items = new ItemStack[9];
    public Random random = new Random();
    public String customName;

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.items[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.items[index] != null) {
            if (this.items[index].stackSize <= count) {
                ItemStack itemStack = this.items[index];
                this.items[index] = null;
                this.markDirty();
                return itemStack;
            }

            ItemStack splitStack = this.items[index].splitStack(count);
            if (this.items[index].stackSize == 0) {
                this.items[index] = null;
            }

            this.markDirty();
            return splitStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.items[index] != null) {
            ItemStack itemStack = this.items[index];
            this.items[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    public int chooseNonEmptySlot() {
        int n = -1;
        int n2 = 1;

        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] != null && this.random.nextInt(n2++) == 0) {
                n = i;
            }
        }

        return n;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.items[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    public int addToFirstFreeSlot(ItemStack itemStack) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null || this.items[i].getItem() == null) {
                this.setInventorySlotContents(i, itemStack);
                return i;
            }
        }

        return -1;
    }

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.customName : "container.dispenser";
    }

    public void setCustomName(String str) {
        this.customName = str;
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.customName != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList tagList = compound.getTagList("Items", 10);
        this.items = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            int n = compoundTagAt.getByte("Slot") & 255;
            if (n >= 0 && n < this.items.length) {
                this.items[n] = ItemStack.loadItemStackFromNBT(compoundTagAt);
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

        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.items[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        compound.setTag("Items", nbttagList);
        if (this.isCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
}
