package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
    public static Logger logger = LogManager.getLogger();
    public IInventory outputSlot = new InventoryCraftResult();
    public IInventory inputSlots = new InventoryBasic("Repair", true, 2) {
        @Override
        public void markDirty() {
            super.markDirty();
            ContainerRepair.this.onCraftMatrixChanged(this);
        }
    };
    public World theWorld;
    public int intValue;
    public int intValue2;
    public int intValue3;
    public int maximumCost;
    public int materialCost;
    public String repairedItemName;
    public EntityPlayer thePlayer;

    public ContainerRepair(InventoryPlayer inventoryPlayer, final World world, final int n, final int n2, final int n3, EntityPlayer entityPlayer) {
        this.theWorld = world;
        this.intValue = n;
        this.intValue2 = n2;
        this.intValue3 = n3;
        this.thePlayer = entityPlayer;
        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(
            new Slot(this.outputSlot, 2, 134, 47) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean canTakeStack(EntityPlayer entityPlayer2) {
                    return (entityPlayer2.capabilities.isCreativeMode || entityPlayer2.experienceLevel >= ContainerRepair.this.maximumCost)
                        && ContainerRepair.this.maximumCost > 0
                        && this.getHasStack();
                }

                @Override
                public void onPickupFromSlot(EntityPlayer entityPlayer2, ItemStack itemStack) {
                    if (!entityPlayer2.capabilities.isCreativeMode) {
                        entityPlayer2.addExperienceLevel(-ContainerRepair.this.maximumCost);
                    }

                    ContainerRepair.this.inputSlots.setInventorySlotContents(0, null);
                    if (ContainerRepair.this.materialCost > 0) {
                        ItemStack stackInSlot = ContainerRepair.this.inputSlots.getStackInSlot(1);
                        if (stackInSlot != null && stackInSlot.stackSize > ContainerRepair.this.materialCost) {
                            stackInSlot.stackSize = stackInSlot.stackSize - ContainerRepair.this.materialCost;
                            ContainerRepair.this.inputSlots.setInventorySlotContents(1, stackInSlot);
                        } else {
                            ContainerRepair.this.inputSlots.setInventorySlotContents(1, null);
                        }
                    } else {
                        ContainerRepair.this.inputSlots.setInventorySlotContents(1, null);
                    }

                    ContainerRepair.this.maximumCost = 0;
                    if (!entityPlayer2.capabilities.isCreativeMode && !world.isRemote && world.getBlock(n, n2, n3) == Blocks.anvil && entityPlayer2.getRNG().nextFloat() < 0.12F) {
                        int blockMetadata = world.getBlockMetadata(n, n2, n3);
                        int n4 = blockMetadata & 3;
                        int n5 = blockMetadata >> 2;
                        if (++n5 > 2) {
                            world.setBlockToAir(n, n2, n3);
                            world.playAuxSFX(1020, n, n2, n3, 0);
                        } else {
                            world.setBlockMetadataWithNotify(n, n2, n3, n4 | n5 << 2, 2);
                            world.playAuxSFX(1021, n, n2, n3, 0);
                        }
                    } else if (!world.isRemote) {
                        world.playAuxSFX(1021, n, n2, n3, 0);
                    }
                }
            }
        );

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
    public void onCraftMatrixChanged(IInventory inventory) {
        super.onCraftMatrixChanged(inventory);
        if (inventory == this.inputSlots) {
            this.updateRepairOutput();
        }
    }

    public void updateRepairOutput() {
        ItemStack stackInSlot = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 0;
        int n = 0;
        byte b = 0;
        int n2 = 0;
        if (stackInSlot == null) {
            this.outputSlot.setInventorySlotContents(0, null);
            this.maximumCost = 0;
        } else {
            ItemStack itemStack = stackInSlot.copy();
            ItemStack stackInSlot2 = this.inputSlots.getStackInSlot(1);
            Map enchantments = EnchantmentHelper.getEnchantments(itemStack);
            boolean flag = false;
            int n3 = b + stackInSlot.getRepairCost() + (stackInSlot2 == null ? 0 : stackInSlot2.getRepairCost());
            this.materialCost = 0;
            if (stackInSlot2 != null) {
                flag = stackInSlot2.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantmentNbt(stackInSlot2).tagCount() > 0;
                if (itemStack.isItemStackDamageable() && itemStack.getItem().getIsRepairable(stackInSlot, stackInSlot2)) {
                    int min = Math.min(itemStack.getCurrentDurability(), itemStack.getMaxDurability() / 4);
                    if (min <= 0) {
                        this.outputSlot.setInventorySlotContents(0, null);
                        this.maximumCost = 0;
                        return;
                    }

                    int n4;
                    for (n4 = 0; min > 0 && n4 < stackInSlot2.stackSize; n4++) {
                        int n5 = itemStack.getCurrentDurability() - min;
                        itemStack.setMetadata(n5);
                        n += Math.max(1, min / 100) + enchantments.size();
                        min = Math.min(itemStack.getCurrentDurability(), itemStack.getMaxDurability() / 4);
                    }

                    this.materialCost = n4;
                } else {
                    if (!flag && (itemStack.getItem() != stackInSlot2.getItem() || !itemStack.isItemStackDamageable())) {
                        this.outputSlot.setInventorySlotContents(0, null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (itemStack.isItemStackDamageable() && !flag) {
                        int n4 = stackInSlot.getMaxDurability() - stackInSlot.getCurrentDurability();
                        int n5 = stackInSlot2.getMaxDurability() - stackInSlot2.getCurrentDurability();
                        int n6 = n5 + itemStack.getMaxDurability() * 12 / 100;
                        int n7 = n4 + n6;
                        int n8 = itemStack.getMaxDurability() - n7;
                        if (n8 < 0) {
                            n8 = 0;
                        }

                        if (n8 < itemStack.getMetadata()) {
                            itemStack.setMetadata(n8);
                            n += Math.max(1, n6 / 100);
                        }
                    }

                    Map enchantments2 = EnchantmentHelper.getEnchantments(stackInSlot2);

                    for (int n4 : (Iterable<Integer>)enchantments2.keySet()) {
                        Enchantment enchantment = Enchantment.enchantmentsList[n4];
                        int n5 = enchantments.containsKey(n4) ? (Integer)enchantments.get(n4) : 0;
                        int n6 = (Integer)enchantments2.get(n4);
                        int n7;
                        if (n5 == n6) {
                            n7 = ++n6;
                        } else {
                            n7 = Math.max(n6, n5);
                        }

                        n6 = n7;
                        int n8 = n6 - n5;
                        boolean canApply = enchantment.canApply(stackInSlot);
                        if (this.thePlayer.capabilities.isCreativeMode || stackInSlot.getItem() == Items.enchanted_book) {
                            canApply = true;
                        }

                        for (int n9 : (Iterable<Integer>)enchantments.keySet()) {
                            if (n9 != n4 && !enchantment.canApplyTogether(Enchantment.enchantmentsList[n9])) {
                                canApply = false;
                                n += n8;
                            }
                        }

                        if (canApply) {
                            if (n6 > enchantment.getMaxLevel()) {
                                n6 = enchantment.getMaxLevel();
                            }

                            enchantments.put(n4, n6);
                            int n9 = 0;
                            switch (enchantment.getWeight()) {
                                case 1:
                                    n9 = 8;
                                    break;
                                case 2:
                                    n9 = 4;
                                case 3:
                                case 4:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                default:
                                    break;
                                case 5:
                                    n9 = 2;
                                    break;
                                case 10:
                                    n9 = 1;
                            }

                            if (flag) {
                                n9 = Math.max(1, n9 / 2);
                            }

                            n += n9 * n8;
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.repairedItemName)) {
                if (stackInSlot.hasDisplayName()) {
                    n2 = (stackInSlot.isItemStackDamageable() ? 7 : stackInSlot.stackSize * 5);
                    n += n2;
                    itemStack.removeCustomName();
                }
            } else if (!this.repairedItemName.equals(stackInSlot.getDisplayName())) {
                n2 = (stackInSlot.isItemStackDamageable() ? 7 : stackInSlot.stackSize * 5);
                n += n2;
                if (stackInSlot.hasDisplayName()) {
                    n3 += n2 / 2;
                }

                itemStack.setStackDisplayName(this.repairedItemName);
            }

            int n4 = 0;

            for (int n5 : (Iterable<Integer>)enchantments.keySet()) {
                Enchantment enchantment = Enchantment.enchantmentsList[n5];
                int n6 = (Integer)enchantments.get(n5);
                int n7 = 0;
                n4++;
                switch (enchantment.getWeight()) {
                    case 1:
                        n7 = 8;
                        break;
                    case 2:
                        n7 = 4;
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        break;
                    case 5:
                        n7 = 2;
                        break;
                    case 10:
                        n7 = 1;
                }

                if (flag) {
                    n7 = Math.max(1, n7 / 2);
                }

                n3 += n4 + n6 * n7;
            }

            if (flag) {
                n3 = Math.max(1, n3 / 2);
            }

            this.maximumCost = n3 + n;
            if (n <= 0) {
                itemStack = null;
            }

            if (n2 == n && n2 > 0 && this.maximumCost >= 40) {
                this.maximumCost = 39;
            }

            if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode) {
                itemStack = null;
            }

            if (itemStack != null) {
                int repairCost = itemStack.getRepairCost();
                if (stackInSlot2 != null && repairCost < stackInSlot2.getRepairCost()) {
                    repairCost = stackInSlot2.getRepairCost();
                }

                if (itemStack.hasDisplayName()) {
                    repairCost -= 9;
                }

                if (repairCost < 0) {
                    repairCost = 0;
                }

                repairCost += 2;
                itemStack.setRepairCost(repairCost);
                EnchantmentHelper.setEnchantments(enchantments, itemStack);
            }

            this.outputSlot.setInventorySlotContents(0, itemStack);
            this.detectAndSendChanges();
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.maximumCost);
    }

    @Override
    public void updateProgressBar(int n, int n2) {
        if (n == 0) {
            this.maximumCost = n2;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!this.theWorld.isRemote) {
            for (int i = 0; i < this.inputSlots.getSizeInventory(); i++) {
                ItemStack stackInSlotOnClosing = this.inputSlots.getStackInSlotOnClosing(i);
                if (stackInSlotOnClosing != null) {
                    entityPlayer.dropPlayerItemWithRandomChoice(stackInSlotOnClosing, false);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.theWorld.getBlock(this.intValue, this.intValue2, this.intValue3) == Blocks.anvil
            && player.getDistanceSq(this.intValue + 0.5, this.intValue2 + 0.5, this.intValue3 + 0.5) <= 64.0;
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
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39 && !this.mergeItemStack(stack, 0, 2, false)) {
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

    public void updateItemName(String str) {
        this.repairedItemName = str;
        if (this.getSlot(2).getHasStack()) {
            ItemStack stack = this.getSlot(2).getStack();
            if (StringUtils.isBlank(str)) {
                stack.removeCustomName();
            } else {
                stack.setStackDisplayName(this.repairedItemName);
            }
        }

        this.updateRepairOutput();
    }
}
