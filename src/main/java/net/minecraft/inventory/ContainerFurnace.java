package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerFurnace extends Container {
    public TileEntityFurnace tileFurnace;
    public int lastCookTime;
    public int lastBurnTime;
    public int lastItemBurnTime;

    public ContainerFurnace(InventoryPlayer invPlayer, TileEntityFurnace teFurnace) {
        this.tileFurnace = teFurnace;
        this.addSlotToContainer(new Slot(teFurnace, 0, 56, 17));
        this.addSlotToContainer(new Slot(teFurnace, 1, 56, 53));
        this.addSlotToContainer(new SlotFurnace(invPlayer.player, teFurnace, 2, 116, 35));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.furnaceCookTime);
        crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.furnaceBurnTime);
        crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.currentItemBurnTime);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting)this.crafters.get(i);
            if (this.lastCookTime != this.tileFurnace.furnaceCookTime) {
                crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.furnaceCookTime);
            }

            if (this.lastBurnTime != this.tileFurnace.furnaceBurnTime) {
                crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.furnaceBurnTime);
            }

            if (this.lastItemBurnTime != this.tileFurnace.currentItemBurnTime) {
                crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.currentItemBurnTime);
            }
        }

        this.lastCookTime = this.tileFurnace.furnaceCookTime;
        this.lastBurnTime = this.tileFurnace.furnaceBurnTime;
        this.lastItemBurnTime = this.tileFurnace.currentItemBurnTime;
    }

    @Override
    public void updateProgressBar(int n, int n2) {
        if (n == 0) {
            this.tileFurnace.furnaceCookTime = n2;
        }

        if (n == 1) {
            this.tileFurnace.furnaceBurnTime = n2;
        }

        if (n == 2) {
            this.tileFurnace.currentItemBurnTime = n2;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tileFurnace.isUseableByPlayer(player);
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
            } else if (index != 1 && index != 0) {
                if (FurnaceRecipes.instance().getSmeltingResult(stack) != null) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return null;
                    }
                } else if (TileEntityFurnace.isItemFuel(stack)) {
                    if (!this.mergeItemStack(stack, 1, 2, false)) {
                        return null;
                    }
                } else if (index >= 3 && index < 30) {
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
}
