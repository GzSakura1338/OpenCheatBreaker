package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class ContainerBrewingStand extends Container {
    public TileEntityBrewingStand tileBrewingStand;
    public Slot theSlot;
    public int brewTime;

    public ContainerBrewingStand(InventoryPlayer inventoryPlayer, TileEntityBrewingStand tileEntityBrewingStand) {
        this.tileBrewingStand = tileEntityBrewingStand;
        this.addSlotToContainer(new ContainerBrewingStand.Potion(inventoryPlayer.player, tileEntityBrewingStand, 0, 56, 46));
        this.addSlotToContainer(new ContainerBrewingStand.Potion(inventoryPlayer.player, tileEntityBrewingStand, 1, 79, 53));
        this.addSlotToContainer(new ContainerBrewingStand.Potion(inventoryPlayer.player, tileEntityBrewingStand, 2, 102, 46));
        this.theSlot = this.addSlotToContainer(new ContainerBrewingStand.Ingredient(tileEntityBrewingStand, 3, 79, 17));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting)this.crafters.get(i);
            if (this.brewTime != this.tileBrewingStand.getBrewTime()) {
                crafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getBrewTime());
            }
        }

        this.brewTime = this.tileBrewingStand.getBrewTime();
    }

    @Override
    public void updateProgressBar(int n, int n2) {
        if (n == 0) {
            this.tileBrewingStand.setBrewTime(n2);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tileBrewingStand.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemStack = stack.copy();
            if ((index < 0 || index > 2) && index != 3) {
                if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(stack)) {
                    if (!this.mergeItemStack(stack, 3, 4, false)) {
                        return null;
                    }
                } else if (ContainerBrewingStand.Potion.canHoldPotion(itemStack)) {
                    if (!this.mergeItemStack(stack, 0, 3, false)) {
                        return null;
                    }
                } else if (index >= 4 && index < 31) {
                    if (!this.mergeItemStack(stack, 31, 40, false)) {
                        return null;
                    }
                } else if (index >= 31 && index < 40) {
                    if (!this.mergeItemStack(stack, 4, 31, false)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(stack, 4, 40, false)) {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(stack, 4, 40, true)) {
                    return null;
                }

                slot.onSlotChange(stack, itemStack);
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

    public class Ingredient extends Slot {
        public Ingredient(IInventory inventory, int n, int n2, int n3) {
            super(inventory, n, n2, n3);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null && stack.getItem().isPotionIngredient(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }

    public static class Potion extends Slot {
        public EntityPlayer player;

        public Potion(EntityPlayer entityPlayer, IInventory inventory, int n, int n2, int n3) {
            super(inventory, n, n2, n3);
            this.player = entityPlayer;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return canHoldPotion(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
            if (itemStack.getItem() == Items.potionitem && itemStack.getMetadata() > 0) {
                this.player.addStat(AchievementList.potion, 1);
            }

            super.onPickupFromSlot(entityPlayer, itemStack);
        }

        public static boolean canHoldPotion(ItemStack itemStack) {
            return itemStack != null && (itemStack.getItem() == Items.potionitem || itemStack.getItem() == Items.glass_bottle);
        }
    }
}
