package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    public World worldObj;
    public int posX;
    public int posY;
    public int posZ;

    public ContainerWorkbench(InventoryPlayer inventoryPlayer, World world, int n, int n2, int n3) {
        this.worldObj = world;
        this.posX = n;
        this.posY = n2;
        this.posZ = n3;
        this.addSlotToContainer(new SlotCrafting(inventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!this.worldObj.isRemote) {
            for (int i = 0; i < 9; i++) {
                ItemStack stackInSlotOnClosing = this.craftMatrix.getStackInSlotOnClosing(i);
                if (stackInSlotOnClosing != null) {
                    entityPlayer.dropPlayerItemWithRandomChoice(stackInSlotOnClosing, false);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.worldObj.getBlock(this.posX, this.posY, this.posZ) == Blocks.crafting_table
            && player.getDistanceSq(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5) <= 64.0;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 10, 46, true)) {
                    return null;
                }

                slot.onSlotChange(stack, itemStack);
            } else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(stack, 37, 46, false)) {
                    return null;
                }
            } else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(stack, 10, 37, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 10, 46, false)) {
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
    public boolean checkItemStack(ItemStack itemStack, Slot slot) {
        return slot.inventory != this.craftResult && super.checkItemStack(itemStack, slot);
    }
}
