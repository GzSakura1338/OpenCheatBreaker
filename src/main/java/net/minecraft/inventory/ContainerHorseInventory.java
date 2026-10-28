package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
    public IInventory playerInv;
    public EntityHorse theHorse;

    public ContainerHorseInventory(IInventory inventory, IInventory inventory2, final EntityHorse entityHorse) {
        this.playerInv = inventory2;
        this.theHorse = entityHorse;
        byte b = 3;
        inventory2.openChest();
        int n = (b - 4) * 18;
        this.addSlotToContainer(new Slot(inventory2, 0, 8, 18) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && stack.getItem() == Items.saddle && !this.getHasStack();
            }
        });
        this.addSlotToContainer(new Slot(inventory2, 1, 8, 36) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && entityHorse.canWearArmor() && EntityHorse.isHorseArmor(stack.getItem());
            }

            @Override
            public boolean canBeHovered() {
                return entityHorse.canWearArmor();
            }
        });
        if (entityHorse.isChested()) {
            for (int i = 0; i < b; i++) {
                for (int j = 0; j < 5; j++) {
                    this.addSlotToContainer(new Slot(inventory2, 2 + j + i * 5, 80 + j * 18, 18 + i * 18));
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18 + n));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 160 + n));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.playerInv.isUseableByPlayer(player) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(player) < 8.0F;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if (index < this.playerInv.getSizeInventory()) {
                if (!this.mergeItemStack(stack, this.playerInv.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (this.getSlot(1).isItemValid(stack) && !this.getSlot(1).getHasStack()) {
                if (!this.mergeItemStack(stack, 1, 2, false)) {
                    return null;
                }
            } else if (this.getSlot(0).isItemValid(stack)) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
                    return null;
                }
            } else if (this.playerInv.getSizeInventory() <= 2 || !this.mergeItemStack(stack, 2, this.playerInv.getSizeInventory(), false)) {
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
        this.playerInv.closeChest();
    }
}
