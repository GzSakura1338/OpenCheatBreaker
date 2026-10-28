package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
    public IInventory lowerChestInventory;
    public int numRows;

    public ContainerChest(IInventory inventory, IInventory inventory2) {
        this.lowerChestInventory = inventory2;
        this.numRows = inventory2.getSizeInventory() / 9;
        inventory2.openChest();
        int n = (this.numRows - 4) * 18;

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventory2, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + n));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 161 + n));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.lowerChestInventory.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if (index < this.numRows * 9) {
                if (!this.mergeItemStack(stack, this.numRows * 9, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 0, this.numRows * 9, false)) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        this.lowerChestInventory.closeChest();
    }

    public IInventory getLowerChestInventory() {
        return this.lowerChestInventory;
    }
}
