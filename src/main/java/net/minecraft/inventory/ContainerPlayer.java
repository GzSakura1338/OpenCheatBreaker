package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.IIcon;

public class ContainerPlayer extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public boolean isLocalWorld;
    public EntityPlayer thePlayer;

    public ContainerPlayer(InventoryPlayer inventoryPlayer, boolean flag, EntityPlayer entityPlayer) {
        this.isLocalWorld = flag;
        this.thePlayer = entityPlayer;
        this.addSlotToContainer(new SlotCrafting(inventoryPlayer.player, this.craftMatrix, this.craftResult, 0, 144, 36));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
            }
        }

        for (int i = 0; i < 4; i++) {
            final int n = i;
            this.addSlotToContainer(
                new Slot(inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 8, 8 + i * 18) {
                    @Override
                    public int getSlotStackLimit() {
                        return 1;
                    }

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return stack != null
                            && (
                                stack.getItem() instanceof ItemArmor
                                    ? ((ItemArmor)stack.getItem()).armorType == n
                                    : (stack.getItem() == Item.getItemFromBlock(Blocks.pumpkin) || stack.getItem() == Items.skull) && n == 0
                            );
                    }

                    @Override
                    public IIcon getBackgroundIconIndex() {
                        return ItemArmor.getBackgroundIcon(n);
                    }
                }
            );
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);

        for (int i = 0; i < 4; i++) {
            ItemStack stackInSlotOnClosing = this.craftMatrix.getStackInSlotOnClosing(i);
            if (stackInSlotOnClosing != null) {
                entityPlayer.dropPlayerItemWithRandomChoice(stackInSlotOnClosing, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, null);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 9, 45, true)) {
                    return null;
                }

                slot.onSlotChange(stack, itemStack);
            } else if (index >= 1 && index < 5) {
                if (!this.mergeItemStack(stack, 9, 45, false)) {
                    return null;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.mergeItemStack(stack, 9, 45, false)) {
                    return null;
                }
            } else if (itemStack.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemStack.getItem()).armorType)).getHasStack()) {
                int n = 5 + ((ItemArmor)itemStack.getItem()).armorType;
                if (!this.mergeItemStack(stack, n, n + 1, false)) {
                    return null;
                }
            } else if (index >= 9 && index < 36) {
                if (!this.mergeItemStack(stack, 36, 45, false)) {
                    return null;
                }
            } else if (index >= 36 && index < 45) {
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(stack, 9, 45, false)) {
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
