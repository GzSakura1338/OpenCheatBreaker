package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot {
    public EntityPlayer thePlayer;
    public IInventory craftMatrix;
    public int amountCrafted;

    public SlotCrafting(EntityPlayer entityPlayer, IInventory inventory, IInventory inventory2, int n, int n2, int n3) {
        super(inventory2, n, n2, n3);
        this.thePlayer = entityPlayer;
        this.craftMatrix = inventory;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int n) {
        if (this.getHasStack()) {
            this.amountCrafted = this.amountCrafted + Math.min(n, this.getStack().stackSize);
        }

        return super.decrStackSize(n);
    }

    @Override
    public void onCrafting(ItemStack itemStack, int n) {
        this.amountCrafted += n;
        this.onCrafting(itemStack);
    }

    @Override
    public void onCrafting(ItemStack itemStack) {
        itemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
        this.amountCrafted = 0;
        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
            this.thePlayer.addStat(AchievementList.buildWorkBench, 1);
        }

        if (itemStack.getItem() instanceof ItemPickaxe) {
            this.thePlayer.addStat(AchievementList.buildPickaxe, 1);
        }

        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
            this.thePlayer.addStat(AchievementList.buildFurnace, 1);
        }

        if (itemStack.getItem() instanceof ItemHoe) {
            this.thePlayer.addStat(AchievementList.buildHoe, 1);
        }

        if (itemStack.getItem() == Items.bread) {
            this.thePlayer.addStat(AchievementList.makeBread, 1);
        }

        if (itemStack.getItem() == Items.cake) {
            this.thePlayer.addStat(AchievementList.bakeCake, 1);
        }

        if (itemStack.getItem() instanceof ItemPickaxe && ((ItemPickaxe)itemStack.getItem()).getToolMaterial() != ToolMaterial.WOOD) {
            this.thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
        }

        if (itemStack.getItem() instanceof ItemSword) {
            this.thePlayer.addStat(AchievementList.buildSword, 1);
        }

        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
            this.thePlayer.addStat(AchievementList.enchantments, 1);
        }

        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
            this.thePlayer.addStat(AchievementList.bookcase, 1);
        }
    }

    @Override
    public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
        this.onCrafting(itemStack);

        for (int i = 0; i < this.craftMatrix.getSizeInventory(); i++) {
            ItemStack stackInSlot = this.craftMatrix.getStackInSlot(i);
            if (stackInSlot != null) {
                this.craftMatrix.decrStackSize(i, 1);
                if (stackInSlot.getItem().hasContainerItem()) {
                    ItemStack itemStack2 = new ItemStack(stackInSlot.getItem().getContainerItem());
                    if (!stackInSlot.getItem().doesContainerItemLeaveCraftingGrid(stackInSlot) || !this.thePlayer.inventory.addItemStackToInventory(itemStack2)) {
                        if (this.craftMatrix.getStackInSlot(i) == null) {
                            this.craftMatrix.setInventorySlotContents(i, itemStack2);
                        } else {
                            this.thePlayer.dropPlayerItemWithRandomChoice(itemStack2, false);
                        }
                    }
                }
            }
        }
    }
}
