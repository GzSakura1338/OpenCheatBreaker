package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnace extends Slot {
    public EntityPlayer thePlayer;
    public int amount;

    public SlotFurnace(EntityPlayer entityPlayer, IInventory inventory, int n, int n2, int n3) {
        super(inventory, n, n2, n3);
        this.thePlayer = entityPlayer;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int n) {
        if (this.getHasStack()) {
            this.amount = this.amount + Math.min(n, this.getStack().stackSize);
        }

        return super.decrStackSize(n);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
        this.onCrafting(itemStack);
        super.onPickupFromSlot(entityPlayer, itemStack);
    }

    @Override
    public void onCrafting(ItemStack itemStack, int n) {
        this.amount += n;
        this.onCrafting(itemStack);
    }

    @Override
    public void onCrafting(ItemStack itemStack) {
        itemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amount);
        if (!this.thePlayer.worldObj.isRemote) {
            int amount = this.amount;
            float smeltingExperience = FurnaceRecipes.instance().getSmeltingExperience(itemStack);
            if (smeltingExperience == 0.0F) {
                amount = 0;
            } else if (smeltingExperience < 1.0F) {
                int n = MathHelper.floor_float(amount * smeltingExperience);
                if (n < MathHelper.ceiling_float_int(amount * smeltingExperience) && (float)Math.random() < amount * smeltingExperience - n) {
                    n++;
                }

                amount = n;
            }

            while (amount > 0) {
                int xPSplit = EntityXPOrb.getXPSplit(amount);
                amount -= xPSplit;
                this.thePlayer
                    .worldObj
                    .spawnEntityInWorld(new EntityXPOrb(this.thePlayer.worldObj, this.thePlayer.posX, this.thePlayer.posY + 0.5, this.thePlayer.posZ + 0.5, xPSplit));
            }
        }

        this.amount = 0;
        if (itemStack.getItem() == Items.iron_ingot) {
            this.thePlayer.addStat(AchievementList.acquireIron, 1);
        }

        if (itemStack.getItem() == Items.cooked_fish) {
            this.thePlayer.addStat(AchievementList.cookFish, 1);
        }
    }
}
