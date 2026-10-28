package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {
    public ItemStack[] theInventory = new ItemStack[3];
    public EntityPlayer thePlayer;
    public IMerchant theMerchant;
    public MerchantRecipe currentRecipe;
    public int currentRecipeIndex;

    public InventoryMerchant(EntityPlayer entityPlayer, IMerchant merchant) {
        this.thePlayer = entityPlayer;
        this.theMerchant = merchant;
    }

    @Override
    public int getSizeInventory() {
        return this.theInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.theInventory[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.theInventory[index] != null) {
            if (index == 2) {
                ItemStack itemStack = this.theInventory[index];
                this.theInventory[index] = null;
                return itemStack;
            }

            if (this.theInventory[index].stackSize <= count) {
                ItemStack itemStack = this.theInventory[index];
                this.theInventory[index] = null;
                if (this.inventoryResetNeededOnSlotChange(index)) {
                    this.resetRecipeAndSlots();
                }

                return itemStack;
            } else {
                ItemStack splitStack = this.theInventory[index].splitStack(count);
                if (this.theInventory[index].stackSize == 0) {
                    this.theInventory[index] = null;
                }

                if (this.inventoryResetNeededOnSlotChange(index)) {
                    this.resetRecipeAndSlots();
                }

                return splitStack;
            }
        } else {
            return null;
        }
    }

    public boolean inventoryResetNeededOnSlotChange(int n) {
        return n == 0 || n == 1;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.theInventory[index] != null) {
            ItemStack itemStack = this.theInventory[index];
            this.theInventory[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.theInventory[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (this.inventoryResetNeededOnSlotChange(index)) {
            this.resetRecipeAndSlots();
        }
    }

    @Override
    public String getInventoryName() {
        return "mob.villager";
    }

    @Override
    public boolean isCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.theMerchant.getCustomer() == player;
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

    @Override
    public void markDirty() {
        this.resetRecipeAndSlots();
    }

    public void resetRecipeAndSlots() {
        this.currentRecipe = null;
        ItemStack itemStack = this.theInventory[0];
        ItemStack itemStack2 = this.theInventory[1];
        if (itemStack == null) {
            itemStack = itemStack2;
            itemStack2 = null;
        }

        if (itemStack == null) {
            this.setInventorySlotContents(2, null);
        } else {
            MerchantRecipeList recipes = this.theMerchant.getRecipes(this.thePlayer);
            if (recipes != null) {
                MerchantRecipe canRecipeBeUsed = recipes.canRecipeBeUsed(itemStack, itemStack2, this.currentRecipeIndex);
                if (canRecipeBeUsed != null && !canRecipeBeUsed.isRecipeDisabled()) {
                    this.currentRecipe = canRecipeBeUsed;
                    this.setInventorySlotContents(2, canRecipeBeUsed.getItemToSell().copy());
                } else if (itemStack2 != null) {
                    canRecipeBeUsed = recipes.canRecipeBeUsed(itemStack2, itemStack, this.currentRecipeIndex);
                    if (canRecipeBeUsed != null && !canRecipeBeUsed.isRecipeDisabled()) {
                        this.currentRecipe = canRecipeBeUsed;
                        this.setInventorySlotContents(2, canRecipeBeUsed.getItemToSell().copy());
                    } else {
                        this.setInventorySlotContents(2, null);
                    }
                } else {
                    this.setInventorySlotContents(2, null);
                }
            }
        }

        this.theMerchant.verifySellingItem(this.getStackInSlot(2));
    }

    public MerchantRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }

    public void setCurrentRecipeIndex(int n) {
        this.currentRecipeIndex = n;
        this.resetRecipeAndSlots();
    }
}
