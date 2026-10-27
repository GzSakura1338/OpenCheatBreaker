package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public abstract class Container {
   public List<ItemStack> inventoryItemStacks = Lists.newArrayList();
   public List<Slot> inventorySlots = Lists.newArrayList();
   public int windowId;
   public short transactionID;
   public int dragMode = -1;
   public int dragEvent;
   public Set<Slot> dragSlots = Sets.newHashSet();
   public List<ICrafting> crafters = Lists.newArrayList();
   public Set<EntityPlayer> playerList = Sets.newHashSet();

   public Slot addSlotToContainer(Slot slotIn) {
      slotIn.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(slotIn);
      this.inventoryItemStacks.add((ItemStack)null);
      return slotIn;
   }

   public void onCraftGuiOpened(ICrafting listener) {
      if (this.crafters.contains(listener)) {
         throw new IllegalArgumentException("Listener already listening");
      }

      this.crafters.add(listener);
      listener.updateCraftingInventory(this, this.getInventory());
      this.detectAndSendChanges();
   }

   public void removeCraftingFromCrafters(ICrafting listeners) {
      this.crafters.remove(listeners);
   }

   public List<ItemStack> getInventory() {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < this.inventorySlots.size(); i++) {
         newArrayList.add(this.inventorySlots.get(i).getStack());
      }

      return newArrayList;
   }

   public void detectAndSendChanges() {
      for (int i = 0; i < this.inventorySlots.size(); i++) {
         ItemStack stack = this.inventorySlots.get(i).getStack();
         ItemStack itemStack = this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemStack, stack)) {
            itemStack = stack == null ? null : stack.copy();
            this.inventoryItemStacks.set(i, itemStack);

            for (int j = 0; j < this.crafters.size(); j++) {
               this.crafters.get(j).sendSlotContents(this, i, itemStack);
            }
         }
      }
   }

   public boolean enchantItem(EntityPlayer playerIn, int id) {
      return false;
   }

   public Slot getSlotFromInventory(IInventory inv, int slotIn) {
      for (int i = 0; i < this.inventorySlots.size(); i++) {
         Slot slot = this.inventorySlots.get(i);
         if (slot.isHere(inv, slotIn)) {
            return slot;
         }
      }

      return null;
   }

   public Slot getSlot(int slotId) {
      return this.inventorySlots.get(slotId);
   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = this.inventorySlots.get(index);
      return slot != null ? slot.getStack() : null;
   }

   public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn) {
      ItemStack itemStack = null;
      InventoryPlayer inventory = playerIn.inventory;
      if (mode == 5) {
         int dragEvent = this.dragEvent;
         this.dragEvent = getDragEvent(clickedButton);
         if ((dragEvent != 1 || this.dragEvent != 2) && dragEvent != this.dragEvent) {
            this.resetDrag();
         } else if (inventory.getItemStack() == null) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = extractDragMode(clickedButton);
            if (isValidDragMode(this.dragMode, playerIn)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot slot = this.inventorySlots.get(slotId);
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

               for (Slot slot : this.dragSlots) {
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

                     if (itemStack3.stackSize > slot.getItemStackLimit(itemStack3)) {
                        itemStack3.stackSize = slot.getItemStackLimit(itemStack3);
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
            if (inventory.getItemStack() != null) {
               if (clickedButton == 0) {
                  playerIn.dropPlayerItemWithRandomChoice(inventory.getItemStack(), true);
                  inventory.setItemStack((ItemStack)null);
               }

               if (clickedButton == 1) {
                  playerIn.dropPlayerItemWithRandomChoice(inventory.getItemStack().splitStack(1), true);
                  if (inventory.getItemStack().stackSize == 0) {
                     inventory.setItemStack((ItemStack)null);
                  }
               }
            }
         } else if (mode == 1) {
            if (slotId < 0) {
               return null;
            }

            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null && slot.canTakeStack(playerIn)) {
               ItemStack transferStackInSlot = this.transferStackInSlot(playerIn, slotId);
               if (transferStackInSlot != null) {
                  Item item = transferStackInSlot.getItem();
                  itemStack = transferStackInSlot.copy();
                  if (slot.getStack() != null && slot.getStack().getItem() == item) {
                     this.retrySlotClick(slotId, clickedButton, true, playerIn);
                  }
               }
            }
         } else {
            if (slotId < 0) {
               return null;
            }

            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null) {
               ItemStack stack = slot.getStack();
               ItemStack itemStack2 = inventory.getItemStack();
               if (stack != null) {
                  itemStack = stack.copy();
               }

               if (stack == null) {
                  if (itemStack2 != null && slot.isItemValid(itemStack2)) {
                     int n = clickedButton == 0 ? itemStack2.stackSize : 1;
                     if (n > slot.getItemStackLimit(itemStack2)) {
                        n = slot.getItemStackLimit(itemStack2);
                     }

                     if (itemStack2.stackSize >= n) {
                        slot.putStack(itemStack2.splitStack(n));
                     }

                     if (itemStack2.stackSize == 0) {
                        inventory.setItemStack((ItemStack)null);
                     }
                  }
               } else if (slot.canTakeStack(playerIn)) {
                  if (itemStack2 == null) {
                     int n = clickedButton == 0 ? stack.stackSize : (stack.stackSize + 1) / 2;
                     ItemStack decrStackSize = slot.decrStackSize(n);
                     inventory.setItemStack(decrStackSize);
                     if (stack.stackSize == 0) {
                        slot.putStack((ItemStack)null);
                     }

                     slot.onPickupFromSlot(playerIn, inventory.getItemStack());
                  } else if (slot.isItemValid(itemStack2)) {
                     if (stack.getItem() == itemStack2.getItem() && stack.getMetadata() == itemStack2.getMetadata() && ItemStack.areItemStackTagsEqual(stack, itemStack2)) {
                        int n = clickedButton == 0 ? itemStack2.stackSize : 1;
                        if (n > slot.getItemStackLimit(itemStack2) - stack.stackSize) {
                           n = slot.getItemStackLimit(itemStack2) - stack.stackSize;
                        }

                        if (n > itemStack2.getMaxStackSize() - stack.stackSize) {
                           n = itemStack2.getMaxStackSize() - stack.stackSize;
                        }

                        itemStack2.splitStack(n);
                        if (itemStack2.stackSize == 0) {
                           inventory.setItemStack((ItemStack)null);
                        }

                        stack.stackSize += n;
                     } else if (itemStack2.stackSize <= slot.getItemStackLimit(itemStack2)) {
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
                           slot.putStack((ItemStack)null);
                        }

                        slot.onPickupFromSlot(playerIn, inventory.getItemStack());
                     }
                  }
               }

               slot.onSlotChanged();
            }
         }
      } else if (mode == 2 && clickedButton >= 0 && clickedButton < 9) {
         Slot slot = this.inventorySlots.get(slotId);
         if (slot.canTakeStack(playerIn)) {
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
                     slot.putStack((ItemStack)null);
                     slot.onPickupFromSlot(playerIn, stack);
                  }
               } else {
                  slot.decrStackSize(stack.stackSize);
                  slot.putStack(stackInSlot);
                  slot.onPickupFromSlot(playerIn, stack);
               }
            } else if (!slot.getHasStack() && stackInSlot != null && slot.isItemValid(stackInSlot)) {
               inventory.setInventorySlotContents(clickedButton, (ItemStack)null);
               slot.putStack(stackInSlot);
            }
         }
      } else if (mode == 3 && playerIn.capabilities.isCreativeMode && inventory.getItemStack() == null && slotId >= 0) {
         Slot slot = this.inventorySlots.get(slotId);
         if (slot != null && slot.getHasStack()) {
            ItemStack itemStack2 = slot.getStack().copy();
            itemStack2.stackSize = itemStack2.getMaxStackSize();
            inventory.setItemStack(itemStack2);
         }
      } else if (mode == 4 && inventory.getItemStack() == null && slotId >= 0) {
         Slot slot = this.inventorySlots.get(slotId);
         if (slot != null && slot.getHasStack() && slot.canTakeStack(playerIn)) {
            ItemStack decrStackSize = slot.decrStackSize(clickedButton == 0 ? 1 : slot.getStack().stackSize);
            slot.onPickupFromSlot(playerIn, decrStackSize);
            playerIn.dropPlayerItemWithRandomChoice(decrStackSize, true);
         }
      } else if (mode == 6 && slotId >= 0) {
         Slot slot = this.inventorySlots.get(slotId);
         ItemStack itemStack2 = inventory.getItemStack();
         if (itemStack2 != null && (slot == null || !slot.getHasStack() || !slot.canTakeStack(playerIn))) {
            int n = clickedButton == 0 ? 0 : this.inventorySlots.size() - 1;
            int n2 = clickedButton == 0 ? 1 : -1;

            for (int i = 0; i < 2; i++) {
               for (int j = n; j >= 0 && j < this.inventorySlots.size() && itemStack2.stackSize < itemStack2.getMaxStackSize(); j += n2) {
                  Slot slot2 = this.inventorySlots.get(j);
                  if (slot2.getHasStack()
                     && canAddItemToSlot(slot2, itemStack2, true)
                     && slot2.canTakeStack(playerIn)
                     && this.canMergeSlot(itemStack2, slot2)
                     && (i != 0 || slot2.getStack().stackSize != slot2.getStack().getMaxStackSize())) {
                     int min = Math.min(itemStack2.getMaxStackSize() - itemStack2.stackSize, slot2.getStack().stackSize);
                     ItemStack decrStackSize = slot2.decrStackSize(min);
                     itemStack2.stackSize += min;
                     if (decrStackSize.stackSize <= 0) {
                        slot2.putStack((ItemStack)null);
                     }

                     slot2.onPickupFromSlot(playerIn, decrStackSize);
                  }
               }
            }
         }

         this.detectAndSendChanges();
      }

      return itemStack;
   }

   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return true;
   }

   public void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
      this.slotClick(slotId, clickedButton, 1, playerIn);
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      InventoryPlayer inventory = playerIn.inventory;
      if (inventory.getItemStack() != null) {
         playerIn.dropPlayerItemWithRandomChoice(inventory.getItemStack(), false);
         inventory.setItemStack((ItemStack)null);
      }
   }

   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.detectAndSendChanges();
   }

   public void putStackInSlot(int slotID, ItemStack stack) {
      this.getSlot(slotID).putStack(stack);
   }

   public void putStacksInSlots(ItemStack[] itemStacks) {
      for (int i = 0; i < itemStacks.length; i++) {
         this.getSlot(i).putStack(itemStacks[i]);
      }
   }

   public void updateProgressBar(int id, int data) {
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

   public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
      boolean flag = false;
      int startIndex2 = startIndex;
      if (reverseDirection) {
         startIndex2 = endIndex - 1;
      }

      if (stack.isStackable()) {
         while (stack.stackSize > 0 && (!reverseDirection && startIndex2 < endIndex || reverseDirection && startIndex2 >= startIndex)) {
            Slot slot = this.inventorySlots.get(startIndex2);
            ItemStack stack2 = slot.getStack();
            if (stack2 != null
               && stack2.getItem() == stack.getItem()
               && (!stack.getHasSubtypes() || stack.getMetadata() == stack2.getMetadata())
               && ItemStack.areItemStackTagsEqual(stack, stack2)) {
               int n = stack2.stackSize + stack.stackSize;
               if (n <= stack.getMaxStackSize()) {
                  stack.stackSize = 0;
                  stack2.stackSize = n;
                  slot.onSlotChanged();
                  flag = true;
               } else if (stack2.stackSize < stack.getMaxStackSize()) {
                  stack.stackSize = stack.stackSize - (stack.getMaxStackSize() - stack2.stackSize);
                  stack2.stackSize = stack.getMaxStackSize();
                  slot.onSlotChanged();
                  flag = true;
               }
            }

            if (reverseDirection) {
               startIndex2--;
            } else {
               startIndex2++;
            }
         }
      }

      if (stack.stackSize > 0) {
         if (reverseDirection) {
            startIndex2 = endIndex - 1;
         } else {
            startIndex2 = startIndex;
         }

         while (!reverseDirection && startIndex2 < endIndex || reverseDirection && startIndex2 >= startIndex) {
            Slot slot = this.inventorySlots.get(startIndex2);
            ItemStack stack2 = slot.getStack();
            if (stack2 == null) {
               slot.putStack(stack.copy());
               slot.onSlotChanged();
               stack.stackSize = 0;
               flag = true;
               break;
            }

            if (reverseDirection) {
               startIndex2--;
            } else {
               startIndex2++;
            }
         }
      }

      return flag;
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

   public static boolean isValidDragMode(int dragModeIn, EntityPlayer player) {
      return dragModeIn == 0 ? true : (dragModeIn == 1 ? true : dragModeIn == 2 && player.capabilities.isCreativeMode);
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

   public static void computeStackSize(Set<Slot> set, int n, ItemStack itemStack, int n2) {
      switch (n) {
         case 0:
            itemStack.stackSize = MathHelper.floor_float((float)itemStack.stackSize / set.size());
            break;
         case 1:
            itemStack.stackSize = 1;
            break;
         case 2:
            itemStack.stackSize = itemStack.getItem().getItemStackLimit();
      }

      itemStack.stackSize += n2;
   }

   public boolean canDragIntoSlot(Slot slot) {
      return true;
   }

   public static int calcRedstone(TileEntity te) {
      return te instanceof IInventory ? calcRedstoneFromInventory((IInventory)te) : 0;
   }

   public static int calcRedstoneFromInventory(IInventory inv) {
      if (inv == null) {
         return 0;
      }

      int n = 0;
      float f = 0.0F;

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            f += (float)stackInSlot.stackSize / Math.min(inv.getInventoryStackLimit(), stackInSlot.getMaxStackSize());
            n++;
         }
      }

      f /= inv.getSizeInventory();
      return MathHelper.floor_float(f * 14.0F) + (n > 0 ? 1 : 0);
   }
}
