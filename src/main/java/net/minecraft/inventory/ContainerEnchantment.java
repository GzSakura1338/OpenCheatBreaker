package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
    public IInventory tableInventory = new InventoryBasic("Enchant", true, 1) {
        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public void markDirty() {
            super.markDirty();
            ContainerEnchantment.this.onCraftMatrixChanged(this);
        }
    };
    public Random rand = new Random();
    public int[] enchantLevels = new int[3];
    public World worldPointer;
    public int posX;
    public int posY;
    public long nameSeed;
    public int posZ;

    public ContainerEnchantment(InventoryPlayer inventoryPlayer, World world, int n, int n2, int n3) {
        this.worldPointer = world;
        this.posX = n;
        this.posY = n2;
        this.posZ = n3;
        this.addSlotToContainer(new Slot(this.tableInventory, 0, 25, 47) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return true;
            }
        });

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
        crafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
        crafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
        crafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting)this.crafters.get(i);
            crafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
            crafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
            crafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
        }
    }

    @Override
    public void updateProgressBar(int n, int n2) {
        if (n >= 0 && n <= 2) {
            this.enchantLevels[n] = n2;
        } else {
            super.updateProgressBar(n, n2);
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        if (inventory == this.tableInventory) {
            ItemStack stackInSlot = inventory.getStackInSlot(0);
            if (stackInSlot != null && stackInSlot.isItemEnchantable()) {
                this.nameSeed = this.rand.nextLong();
                if (!this.worldPointer.isRemote) {
                    int n = 0;

                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if ((i != 0 || j != 0)
                                && this.worldPointer.isAirBlock(this.posX + j, this.posY, this.posZ + i)
                                && this.worldPointer.isAirBlock(this.posX + j, this.posY + 1, this.posZ + i)) {
                                if (this.worldPointer.getBlock(this.posX + j * 2, this.posY, this.posZ + i * 2) == Blocks.bookshelf) {
                                    n++;
                                }

                                if (this.worldPointer.getBlock(this.posX + j * 2, this.posY + 1, this.posZ + i * 2) == Blocks.bookshelf) {
                                    n++;
                                }

                                if (j != 0 && i != 0) {
                                    if (this.worldPointer.getBlock(this.posX + j * 2, this.posY, this.posZ + i) == Blocks.bookshelf) {
                                        n++;
                                    }

                                    if (this.worldPointer.getBlock(this.posX + j * 2, this.posY + 1, this.posZ + i) == Blocks.bookshelf) {
                                        n++;
                                    }

                                    if (this.worldPointer.getBlock(this.posX + j, this.posY, this.posZ + i * 2) == Blocks.bookshelf) {
                                        n++;
                                    }

                                    if (this.worldPointer.getBlock(this.posX + j, this.posY + 1, this.posZ + i * 2) == Blocks.bookshelf) {
                                        n++;
                                    }
                                }
                            }
                        }
                    }

                    for (int i = 0; i < 3; i++) {
                        this.enchantLevels[i] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i, n, stackInSlot);
                    }

                    this.detectAndSendChanges();
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    this.enchantLevels[i] = 0;
                }
            }
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        ItemStack stackInSlot = this.tableInventory.getStackInSlot(0);
        if (this.enchantLevels[id] > 0 && stackInSlot != null && (player.experienceLevel >= this.enchantLevels[id] || player.capabilities.isCreativeMode)) {
            if (!this.worldPointer.isRemote) {
                List buildEnchantmentList = EnchantmentHelper.buildEnchantmentList(this.rand, stackInSlot, this.enchantLevels[id]);
                boolean flag = stackInSlot.getItem() == Items.book;
                if (buildEnchantmentList != null) {
                    player.addExperienceLevel(-this.enchantLevels[id]);
                    if (flag) {
                        stackInSlot.setItem(Items.enchanted_book);
                    }

                    int n = flag && buildEnchantmentList.size() > 1 ? this.rand.nextInt(buildEnchantmentList.size()) : -1;

                    for (int i = 0; i < buildEnchantmentList.size(); i++) {
                        EnchantmentData enchantmentData = (EnchantmentData)buildEnchantmentList.get(i);
                        if (!flag || i != n) {
                            if (flag) {
                                Items.enchanted_book.addEnchantment(stackInSlot, enchantmentData);
                            } else {
                                stackInSlot.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
                            }
                        }
                    }

                    this.onCraftMatrixChanged(this.tableInventory);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!this.worldPointer.isRemote) {
            ItemStack stackInSlotOnClosing = this.tableInventory.getStackInSlotOnClosing(0);
            if (stackInSlotOnClosing != null) {
                entityPlayer.dropPlayerItemWithRandomChoice(stackInSlotOnClosing, false);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.worldPointer.getBlock(this.posX, this.posY, this.posZ) == Blocks.enchanting_table
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
                if (!this.mergeItemStack(stack, 1, 37, true)) {
                    return null;
                }
            } else {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(stack)) {
                    return null;
                }

                if (stack.hasTagCompound() && stack.stackSize == 1) {
                    ((Slot)this.inventorySlots.get(0)).putStack(stack.copy());
                    stack.stackSize = 0;
                } else if (stack.stackSize >= 1) {
                    ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(stack.getItem(), 1, stack.getMetadata()));
                    stack.stackSize--;
                }
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
