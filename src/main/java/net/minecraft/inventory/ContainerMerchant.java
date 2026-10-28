package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
    public IMerchant theMerchant;
    public World theWorld;
    public InventoryMerchant merchantInventory;

    public ContainerMerchant(InventoryPlayer inventoryPlayer, IMerchant merchant, World world) {
        this.theMerchant = merchant;
        this.theWorld = world;
        this.merchantInventory = new InventoryMerchant(inventoryPlayer.player, merchant);
        this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
        this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
        this.addSlotToContainer(new SlotMerchantResult(inventoryPlayer.player, merchant, this.merchantInventory, 2, 120, 53));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    public InventoryMerchant getMerchantInventory() {
        return this.merchantInventory;
    }

    @Override
    public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(inventory);
    }

    public void setCurrentRecipeIndex(int n) {
        this.merchantInventory.setCurrentRecipeIndex(n);
    }

    @Override
    public void updateProgressBar(int n, int n2) {
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.theMerchant.getCustomer() == player;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if (index == 2) {
                if (!this.mergeItemStack(stack, 3, 39, true)) {
                    return null;
                }

                slot.onSlotChange(stack, itemStack);
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 30) {
                    if (!this.mergeItemStack(stack, 30, 39, false)) {
                        return null;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(stack, 3, 30, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 3, 39, false)) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (stack.stackSize == itemStack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, stack);
        }

        return itemStack;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        this.theMerchant.setCustomer(null);
        super.onContainerClosed(entityPlayer);
        if (!this.theWorld.isRemote) {
            ItemStack stackInSlotOnClosing = this.merchantInventory.getStackInSlotOnClosing(0);
            if (stackInSlotOnClosing != null) {
                entityPlayer.dropPlayerItemWithRandomChoice(stackInSlotOnClosing, false);
            }

            stackInSlotOnClosing = this.merchantInventory.getStackInSlotOnClosing(1);
            if (stackInSlotOnClosing != null) {
                entityPlayer.dropPlayerItemWithRandomChoice(stackInSlotOnClosing, false);
            }
        }
    }
}
