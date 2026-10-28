package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public abstract class Container {
    public List inventoryItemStacks = new ArrayList();
    public List inventorySlots = new ArrayList();
    public int windowId;
    public short transactionID;
    public int dragMode = -1;
    public int dragEvent;
    public Set dragSlots = new HashSet();
    public List crafters = new ArrayList();
    public Set playerList = new HashSet();

    public Slot addSlotToContainer(Slot slot) {
        slot.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slot);
        this.inventoryItemStacks.add(null);
        return slot;
    }

    public void onCraftGuiOpened(ICrafting crafting) {
        if (this.crafters.contains(crafting)) {
            throw new IllegalArgumentException("Listener already listening");
        }

        this.crafters.add(crafting);
        crafting.updateCraftingInventory(this, this.getInventory());
        this.detectAndSendChanges();
    }

    public void removeCraftingFromCrafters(ICrafting crafting) {
        this.crafters.remove(crafting);
    }

    public List getInventory() {
        ArrayList list = new ArrayList();

        for (int i = 0; i < this.inventorySlots.size(); i++) {
            list.add(((Slot)this.inventorySlots.get(i)).getStack());
        }

        return list;
    }

    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); i++) {
            ItemStack stack = ((Slot)this.inventorySlots.get(i)).getStack();
            ItemStack itemStack = (ItemStack)this.inventoryItemStacks.get(i);
            if (!ItemStack.areItemStacksEqual(itemStack, stack)) {
                itemStack = stack == null ? null : stack.copy();
                this.inventoryItemStacks.set(i, itemStack);

                for (int j = 0; j < this.crafters.size(); j++) {
                    ((ICrafting)this.crafters.get(j)).sendSlotContents(this, i, itemStack);
                }
            }
        }
    }

    public boolean enchantItem(EntityPlayer player, int id) {
        return false;
    }

    public Slot getSlotFromInventory(IInventory inventory, int n) {
        for (int i = 0; i < this.inventorySlots.size(); i++) {
            Slot slot = (Slot)this.inventorySlots.get(i);
            if (slot.isHere(inventory, n)) {
                return slot;
            }
        }

        return null;
    }

    public Slot getSlot(int n) {
        return (Slot)this.inventorySlots.get(n);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = (Slot)this.inventorySlots.get(index);
        return slot != null ? slot.getStack() : null;
    }

    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
        ItemStack itemStack = null;
        InventoryPlayer inventory = player.inventory;
        if (mode == 5) {
            int dragEvent = this.dragEvent;
            this.dragEvent = getDragEvent(clickedButton);
            if ((dragEvent != 1 || this.dragEvent != 2) && dragEvent != this.dragEvent) {
                this.resetDrag();
            } else if (inventory.getItemStack() == null) {
                this.resetDrag();
            } else if (this.dragEvent == 0) {
                this.dragMode = extractDragMode(clickedButton);
                if (isValidDragMode(this.dragMode)) {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                } else {
                    this.resetDrag();
                }
            } else if (this.dragEvent == 1) {
                Slot slot = (Slot)this.inventorySlots.get(slotId);
                if (slot != null
                    && canAddItemToSlot(slot, inventory.getItemStack(), true)
                    && slot.isItemValid(inventory.getItemStack())
                    && inventory.getItemStack().stackSize > this.dragSlots.size()
                    && this.canDragIntoSlot(slot)) {
                    this.dragSlots.add(slot);
                }
            } else if (this.dragEvent == 2) {
                if (!this.dragSlots.isEmpty()) {
                    ItemStack itemStack2 = inventory.getItemStack().copy();
                    int stackSize = inventory.getItemStack().stackSize;

                    for (Slot slot : (Iterable<Slot>)this.dragSlots) {
                        if (slot != null
                            && canAddItemToSlot(slot, inventory.getItemStack(), true)
                            && slot.isItemValid(inventory.getItemStack())
                            && inventory.getItemStack().stackSize >= this.dragSlots.size()
                            && this.canDragIntoSlot(slot)) {
                            ItemStack itemStack3 = itemStack2.copy();
                            int n = slot.getHasStack() ? slot.getStack().stackSize : 0;
                            computeStackSize(this.dragSlots, this.dragMode, itemStack3, n);
                            if (itemStack3.stackSize > itemStack3.getMaxStackSize()) {
                                itemStack3.stackSize = itemStack3.getMaxStackSize();
                            }

                            if (itemStack3.stackSize > slot.getSlotStackLimit()) {
                                itemStack3.stackSize = slot.getSlotStackLimit();
                            }

                            stackSize -= itemStack3.stackSize - n;
                            slot.putStack(itemStack3);
                        }
                    }

                    itemStack2.stackSize = stackSize;
                    if (itemStack2.stackSize <= 0) {
                        itemStack2 = null;
                    }

                    inventory.setItemStack(itemStack2);
                }

                this.resetDrag();
            } else {
                this.resetDrag();
            }
        } else if (this.dragEvent != 0) {
            this.resetDrag();
        } else if ((mode == 0 || mode == 1) && (clickedButton == 0 || clickedButton == 1)) {
            if (slotId == -999) {
                if (inventory.getItemStack() != null && slotId == -999) {
                    if (clickedButton == 0) {
                        player.dropPlayerItemWithRandomChoice(inventory.getItemStack(), true);
                        inventory.setItemStack(null);
                    }

                    if (clickedButton == 1) {
                        player.dropPlayerItemWithRandomChoice(inventory.getItemStack().splitStack(1), true);
                        if (inventory.getItemStack().stackSize == 0) {
                            inventory.setItemStack(null);
                        }
                    }
                }
            } else if (mode == 1) {
                if (slotId < 0) {
                    return null;
                }

                Slot slot = (Slot)this.inventorySlots.get(slotId);
                if (slot != null && slot.canTakeStack(player)) {
                    ItemStack transferStackInSlot = this.transferStackInSlot(player, slotId);
                    if (transferStackInSlot != null) {
                        Item item = transferStackInSlot.getItem();
                        itemStack = transferStackInSlot.copy();
                        if (slot.getStack() != null && slot.getStack().getItem() == item) {
                            this.retrySlotClick(slotId, clickedButton, true, player);
                        }
                    }
                }
            } else {
                if (slotId < 0) {
                    return null;
                }

                Slot slot = (Slot)this.inventorySlots.get(slotId);
                if (slot != null) {
                    ItemStack stack = slot.getStack();
                    ItemStack itemStack2 = inventory.getItemStack();
                    if (stack != null) {
                        itemStack = stack.copy();
                    }

                    if (stack == null) {
                        if (itemStack2 != null && slot.isItemValid(itemStack2)) {
                            int n = clickedButton == 0 ? itemStack2.stackSize : 1;
                            if (n > slot.getSlotStackLimit()) {
                                n = slot.getSlotStackLimit();
                            }

                            if (itemStack2.stackSize >= n) {
                                slot.putStack(itemStack2.splitStack(n));
                            }

                            if (itemStack2.stackSize == 0) {
                                inventory.setItemStack(null);
                            }
                        }
                    } else if (slot.canTakeStack(player)) {
                        if (itemStack2 == null) {
                            int n = clickedButton == 0 ? stack.stackSize : (stack.stackSize + 1) / 2;
                            ItemStack decrStackSize = slot.decrStackSize(n);
                            inventory.setItemStack(decrStackSize);
                            if (stack.stackSize == 0) {
                                slot.putStack(null);
                            }

                            slot.onPickupFromSlot(player, inventory.getItemStack());
                        } else if (slot.isItemValid(itemStack2)) {
                            if (stack.getItem() == itemStack2.getItem() && stack.getMetadata() == itemStack2.getMetadata() && ItemStack.areItemStackTagsEqual(stack, itemStack2)) {
                                int n = clickedButton == 0 ? itemStack2.stackSize : 1;
                                if (n > slot.getSlotStackLimit() - stack.stackSize) {
                                    n = slot.getSlotStackLimit() - stack.stackSize;
                                }

                                if (n > itemStack2.getMaxStackSize() - stack.stackSize) {
                                    n = itemStack2.getMaxStackSize() - stack.stackSize;
                                }

                                itemStack2.splitStack(n);
                                if (itemStack2.stackSize == 0) {
                                    inventory.setItemStack(null);
                                }

                                stack.stackSize += n;
                            } else if (itemStack2.stackSize <= slot.getSlotStackLimit()) {
                                slot.putStack(itemStack2);
                                inventory.setItemStack(stack);
                            }
                        } else if (stack.getItem() == itemStack2.getItem()
                            && itemStack2.getMaxStackSize() > 1
                            && (!stack.getHasSubtypes() || stack.getMetadata() == itemStack2.getMetadata())
                            && ItemStack.areItemStackTagsEqual(stack, itemStack2)) {
                            int stackSize = stack.stackSize;
                            if (stackSize > 0 && stackSize + itemStack2.stackSize <= itemStack2.getMaxStackSize()) {
                                itemStack2.stackSize += stackSize;
                                stack = slot.decrStackSize(stackSize);
                                if (stack.stackSize == 0) {
                                    slot.putStack(null);
                                }

                                slot.onPickupFromSlot(player, inventory.getItemStack());
                            }
                        }
                    }

                    slot.onSlotChanged();
                }
            }
        } else if (mode == 2 && clickedButton >= 0 && clickedButton < 9) {
            Slot slot = (Slot)this.inventorySlots.get(slotId);
            if (slot.canTakeStack(player)) {
                ItemStack stackInSlot = inventory.getStackInSlot(clickedButton);
                boolean flag = stackInSlot == null || slot.inventory == inventory && slot.isItemValid(stackInSlot);
                int n = -1;
                if (!flag) {
                    n = inventory.getFirstEmptyStack();
                    flag |= n > -1;
                }

                if (slot.getHasStack() && flag) {
                    ItemStack stack = slot.getStack();
                    inventory.setInventorySlotContents(clickedButton, stack.copy());
                    if ((slot.inventory != inventory || !slot.isItemValid(stackInSlot)) && stackInSlot != null) {
                        if (n > -1) {
                            inventory.addItemStackToInventory(stackInSlot);
                            slot.decrStackSize(stack.stackSize);
                            slot.putStack(null);
                            slot.onPickupFromSlot(player, stack);
                        }
                    } else {
                        slot.decrStackSize(stack.stackSize);
                        slot.putStack(stackInSlot);
                        slot.onPickupFromSlot(player, stack);
                    }
                } else if (!slot.getHasStack() && stackInSlot != null && slot.isItemValid(stackInSlot)) {
                    inventory.setInventorySlotContents(clickedButton, null);
                    slot.putStack(stackInSlot);
                }
            }
        } else if (mode == 3 && player.capabilities.isCreativeMode && inventory.getItemStack() == null && slotId >= 0) {
            Slot slot = (Slot)this.inventorySlots.get(slotId);
            if (slot != null && slot.getHasStack()) {
                ItemStack itemStack2 = slot.getStack().copy();
                itemStack2.stackSize = itemStack2.getMaxStackSize();
                inventory.setItemStack(itemStack2);
            }
        } else if (mode == 4 && inventory.getItemStack() == null && slotId >= 0) {
            Slot slot = (Slot)this.inventorySlots.get(slotId);
            if (slot != null && slot.getHasStack() && slot.canTakeStack(player)) {
                ItemStack decrStackSize = slot.decrStackSize(clickedButton == 0 ? 1 : slot.getStack().stackSize);
                slot.onPickupFromSlot(player, decrStackSize);
                player.dropPlayerItemWithRandomChoice(decrStackSize, true);
            }
        } else if (mode == 6 && slotId >= 0) {
            Slot slot = (Slot)this.inventorySlots.get(slotId);
            ItemStack itemStack2 = inventory.getItemStack();
            if (itemStack2 != null && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
                int n = clickedButton == 0 ? 0 : this.inventorySlots.size() - 1;
                int n2 = clickedButton == 0 ? 1 : -1;

                for (int i = 0; i < 2; i++) {
                    for (int j = n; j >= 0 && j < this.inventorySlots.size() && itemStack2.stackSize < itemStack2.getMaxStackSize(); j += n2) {
                        Slot slot2 = (Slot)this.inventorySlots.get(j);
                        if (slot2.getHasStack()
                            && canAddItemToSlot(slot2, itemStack2, true)
                            && slot2.canTakeStack(player)
                            && this.checkItemStack(itemStack2, slot2)
                            && (i != 0 || slot2.getStack().stackSize != slot2.getStack().getMaxStackSize())) {
                            int min = Math.min(itemStack2.getMaxStackSize() - itemStack2.stackSize, slot2.getStack().stackSize);
                            ItemStack decrStackSize = slot2.decrStackSize(min);
                            itemStack2.stackSize += min;
                            if (decrStackSize.stackSize <= 0) {
                                slot2.putStack(null);
                            }

                            slot2.onPickupFromSlot(player, decrStackSize);
                        }
                    }
                }
            }

            this.detectAndSendChanges();
        }

        return itemStack;
    }

    public boolean checkItemStack(ItemStack itemStack, Slot slot) {
        return true;
    }

    public void retrySlotClick(int n, int n2, boolean flag, EntityPlayer entityPlayer) {
        this.slotClick(n, n2, 1, entityPlayer);
    }

    public void onContainerClosed(EntityPlayer entityPlayer) {
        InventoryPlayer inventory = entityPlayer.inventory;
        if (inventory.getItemStack() != null) {
            entityPlayer.dropPlayerItemWithRandomChoice(inventory.getItemStack(), false);
            inventory.setItemStack(null);
        }
    }

    public void onCraftMatrixChanged(IInventory inventory) {
        this.detectAndSendChanges();
    }

    public void putStackInSlot(int n, ItemStack itemStack) {
        this.getSlot(n).putStack(itemStack);
    }

    public void putStacksInSlots(ItemStack[] itemStacks) {
        for (int i = 0; i < itemStacks.length; i++) {
            this.getSlot(i).putStack(itemStacks[i]);
        }
    }

    public void updateProgressBar(int n, int n2) {
    }

    public short getNextTransactionID(InventoryPlayer inventoryPlayer) {
        this.transactionID++;
        return this.transactionID;
    }

    public boolean getCanCraft(EntityPlayer entityPlayer) {
        return !this.playerList.contains(entityPlayer);
    }

    public void setCanCraft(EntityPlayer entityPlayer, boolean flag) {
        if (flag) {
            this.playerList.remove(entityPlayer);
        } else {
            this.playerList.add(entityPlayer);
        }
    }

    public abstract boolean canInteractWith(EntityPlayer entityPlayer);

    public boolean mergeItemStack(ItemStack itemStack, int n, int n2, boolean flag) {
        boolean flag2 = false;
        int n3 = n;
        if (flag) {
            n3 = n2 - 1;
        }

        if (itemStack.isStackable()) {
            while (itemStack.stackSize > 0 && (!flag && n3 < n2 || flag && n3 >= n)) {
                Slot slot = (Slot)this.inventorySlots.get(n3);
                ItemStack stack = slot.getStack();
                if (stack != null
                    && stack.getItem() == itemStack.getItem()
                    && (!itemStack.getHasSubtypes() || itemStack.getMetadata() == stack.getMetadata())
                    && ItemStack.areItemStackTagsEqual(itemStack, stack)) {
                    int n4 = stack.stackSize + itemStack.stackSize;
                    if (n4 <= itemStack.getMaxStackSize()) {
                        itemStack.stackSize = 0;
                        stack.stackSize = n4;
                        slot.onSlotChanged();
                        flag2 = true;
                    } else if (stack.stackSize < itemStack.getMaxStackSize()) {
                        itemStack.stackSize = itemStack.stackSize - (itemStack.getMaxStackSize() - stack.stackSize);
                        stack.stackSize = itemStack.getMaxStackSize();
                        slot.onSlotChanged();
                        flag2 = true;
                    }
                }

                if (flag) {
                    n3--;
                } else {
                    n3++;
                }
            }
        }

        if (itemStack.stackSize > 0) {
            if (flag) {
                n3 = n2 - 1;
            } else {
                n3 = n;
            }

            while (!flag && n3 < n2 || flag && n3 >= n) {
                Slot slot = (Slot)this.inventorySlots.get(n3);
                ItemStack stack = slot.getStack();
                if (stack == null) {
                    slot.putStack(itemStack.copy());
                    slot.onSlotChanged();
                    itemStack.stackSize = 0;
                    flag2 = true;
                    break;
                }

                if (flag) {
                    n3--;
                } else {
                    n3++;
                }
            }
        }

        return flag2;
    }

    public static int extractDragMode(int n) {
        return n >> 2 & 3;
    }

    public static int getDragEvent(int n) {
        return n & 3;
    }

    public static int packClickData(int n, int n2) {
        return n & 3 | (n2 & 3) << 2;
    }

    public static boolean isValidDragMode(int n) {
        return n == 0 || n == 1;
    }

    public void resetDrag() {
        this.dragEvent = 0;
        this.dragSlots.clear();
    }

    public static boolean canAddItemToSlot(Slot slotIn, ItemStack stack, boolean stackSizeMatters) {
        boolean flag = slotIn == null || !slotIn.getHasStack();
        if (slotIn != null
            && slotIn.getHasStack()
            && stack != null
            && stack.isItemEqual(slotIn.getStack())
            && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack)) {
            int n = stackSizeMatters ? 0 : stack.stackSize;
            flag |= slotIn.getStack().stackSize + n <= stack.getMaxStackSize();
        }

        return flag;
    }

    public static void computeStackSize(Set set, int n, ItemStack itemStack, int n2) {
        switch (n) {
            case 0:
                itemStack.stackSize = MathHelper.floor_float((float)itemStack.stackSize / set.size());
                break;
            case 1:
                itemStack.stackSize = 1;
        }

        itemStack.stackSize += n2;
    }

    public boolean canDragIntoSlot(Slot slot) {
        return true;
    }

    public static int calcRedstoneFromInventory(IInventory inventory) {
        if (inventory == null) {
            return 0;
        }

        int n = 0;
        float f = 0.0F;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventory.getStackInSlot(i);
            if (stackInSlot != null) {
                f += (float)stackInSlot.stackSize / Math.min(inventory.getInventoryStackLimit(), stackInSlot.getMaxStackSize());
                n++;
            }
        }

        f /= inventory.getSizeInventory();
        return MathHelper.floor_float(f * 14.0F) + (n > 0 ? 1 : 0);
    }
}
