package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {
    public EntityPlayer thePlayer;
    public IMerchant theMerchant;
    public int amount;
    public InventoryMerchant theMerchantInventory;

    public SlotMerchantResult(EntityPlayer entityPlayer, IMerchant merchant, InventoryMerchant inventoryMerchant, int n, int n2, int n3) {
        super(inventoryMerchant, n, n2, n3);
        this.thePlayer = entityPlayer;
        this.theMerchant = merchant;
        this.theMerchantInventory = inventoryMerchant;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int n) {
        if (this.getHasStack()) {
            this.amount = this.amount + Math.min(n, this.getStack().stackSize);
        }

        return super.decrStackSize(n);
    }

    @Override
    public void onCrafting(ItemStack itemStack, int n) {
        this.amount += n;
        this.onCrafting(itemStack);
    }

    @Override
    public void onCrafting(ItemStack itemStack) {
        itemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amount);
        this.amount = 0;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
        this.onCrafting(itemStack);
        MerchantRecipe currentRecipe = this.theMerchantInventory.getCurrentRecipe();
        if (currentRecipe != null) {
            ItemStack stackInSlot = this.theMerchantInventory.getStackInSlot(0);
            ItemStack stackInSlot2 = this.theMerchantInventory.getStackInSlot(1);
            if (this.doTrade(currentRecipe, stackInSlot, stackInSlot2) || this.doTrade(currentRecipe, stackInSlot2, stackInSlot)) {
                this.theMerchant.useRecipe(currentRecipe);
                if (stackInSlot != null && stackInSlot.stackSize <= 0) {
                    stackInSlot = null;
                }

                if (stackInSlot2 != null && stackInSlot2.stackSize <= 0) {
                    stackInSlot2 = null;
                }

                this.theMerchantInventory.setInventorySlotContents(0, stackInSlot);
                this.theMerchantInventory.setInventorySlotContents(1, stackInSlot2);
            }
        }
    }

    public boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem) {
        ItemStack itemToBuy = trade.getItemToBuy();
        ItemStack secondItemToBuy = trade.getSecondItemToBuy();
        if (firstItem != null && firstItem.getItem() == itemToBuy.getItem()) {
            if (secondItemToBuy != null && secondItem != null && secondItemToBuy.getItem() == secondItem.getItem()) {
                firstItem.stackSize = firstItem.stackSize - itemToBuy.stackSize;
                secondItem.stackSize = secondItem.stackSize - secondItemToBuy.stackSize;
                return true;
            }

            if (secondItemToBuy == null && secondItem == null) {
                firstItem.stackSize = firstItem.stackSize - itemToBuy.stackSize;
                return true;
            }
        }

        return false;
    }
}
