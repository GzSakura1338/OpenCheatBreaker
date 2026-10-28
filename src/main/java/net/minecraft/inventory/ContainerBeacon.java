package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBeacon;

public class ContainerBeacon extends Container {
    public TileEntityBeacon tileBeacon;
    public ContainerBeacon.BeaconSlot beaconSlot;
    public int intValue;
    public int intValue2;
    public int intValue3;

    public ContainerBeacon(InventoryPlayer inventoryPlayer, TileEntityBeacon tileEntityBeacon) {
        this.tileBeacon = tileEntityBeacon;
        this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(tileEntityBeacon, 0, 136, 110));
        byte b = 36;
        short s = 137;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, b + j * 18, s + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, b + i * 18, 58 + s));
        }

        this.intValue = tileEntityBeacon.getLevels();
        this.intValue2 = tileEntityBeacon.getPrimaryEffect();
        this.intValue3 = tileEntityBeacon.getSecondaryEffect();
    }

    @Override
    public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.intValue);
        crafting.sendProgressBarUpdate(this, 1, this.intValue2);
        crafting.sendProgressBarUpdate(this, 2, this.intValue3);
    }

    @Override
    public void updateProgressBar(int n, int n2) {
        if (n == 0) {
            this.tileBeacon.setLevels(n2);
        }

        if (n == 1) {
            this.tileBeacon.setPrimaryEffect(n2);
        }

        if (n == 2) {
            this.tileBeacon.setSecondaryEffect(n2);
        }
    }

    public TileEntityBeacon getTileBeacon() {
        return this.tileBeacon;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tileBeacon.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, 37, true)) {
                    return null;
                }

                slot.onSlotChange(stack, itemStack);
            } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(stack) && stack.stackSize == 1) {
                if (!this.mergeItemStack(stack, 0, 1, false)) {
                    return null;
                }
            } else if (index >= 1 && index < 28) {
                if (!this.mergeItemStack(stack, 28, 37, false)) {
                    return null;
                }
            } else if (index >= 28 && index < 37) {
                if (!this.mergeItemStack(stack, 1, 28, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 1, 37, false)) {
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

    public class BeaconSlot extends Slot {
        public BeaconSlot(IInventory inventory, int n, int n2, int n3) {
            super(inventory, n, n2, n3);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null
                && (
                    stack.getItem() == Items.emerald
                        || stack.getItem() == Items.diamond
                        || stack.getItem() == Items.gold_ingot
                        || stack.getItem() == Items.iron_ingot
                );
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}
