package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MerchantRecipe {
    public ItemStack itemToBuy;
    public ItemStack secondItemToBuy;
    public ItemStack itemToSell;
    public int toolUses;
    public int maxTradeUses;

    public MerchantRecipe(NBTTagCompound nbttagCompound) {
        this.readFromTags(nbttagCompound);
    }

    public MerchantRecipe(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
        this.itemToBuy = itemStack;
        this.secondItemToBuy = itemStack2;
        this.itemToSell = itemStack3;
        this.maxTradeUses = 7;
    }

    public MerchantRecipe(ItemStack itemStack, ItemStack itemStack2) {
        this(itemStack, null, itemStack2);
    }

    public MerchantRecipe(ItemStack itemStack, Item item) {
        this(itemStack, new ItemStack(item));
    }

    public ItemStack getItemToBuy() {
        return this.itemToBuy;
    }

    public ItemStack getSecondItemToBuy() {
        return this.secondItemToBuy;
    }

    public boolean hasSecondItemToBuy() {
        return this.secondItemToBuy != null;
    }

    public ItemStack getItemToSell() {
        return this.itemToSell;
    }

    public boolean hasSameIDsAs(MerchantRecipe merchantRecipe) {
        return this.itemToBuy.getItem() == merchantRecipe.itemToBuy.getItem()
            && this.itemToSell.getItem() == merchantRecipe.itemToSell.getItem()
            && (
                this.secondItemToBuy == null && merchantRecipe.secondItemToBuy == null
                    || this.secondItemToBuy != null && merchantRecipe.secondItemToBuy != null && this.secondItemToBuy.getItem() == merchantRecipe.secondItemToBuy.getItem()
            );
    }

    public boolean hasSameItemsAs(MerchantRecipe merchantRecipe) {
        return this.hasSameIDsAs(merchantRecipe)
            && (
                this.itemToBuy.stackSize < merchantRecipe.itemToBuy.stackSize
                    || this.secondItemToBuy != null && this.secondItemToBuy.stackSize < merchantRecipe.secondItemToBuy.stackSize
            );
    }

    public void incrementToolUses() {
        this.toolUses++;
    }

    public void increaseSpecialPrice(int n) {
        this.maxTradeUses += n;
    }

    public boolean isRecipeDisabled() {
        return this.toolUses >= this.maxTradeUses;
    }

    public void clearUses() {
        this.toolUses = this.maxTradeUses;
    }

    public void readFromTags(NBTTagCompound nbttagCompound) {
        NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("buy");
        this.itemToBuy = ItemStack.loadItemStackFromNBT(compoundTag);
        NBTTagCompound compoundTag2 = nbttagCompound.getCompoundTag("sell");
        this.itemToSell = ItemStack.loadItemStackFromNBT(compoundTag2);
        if (nbttagCompound.hasKey("buyB", 10)) {
            this.secondItemToBuy = ItemStack.loadItemStackFromNBT(nbttagCompound.getCompoundTag("buyB"));
        }

        if (nbttagCompound.hasKey("uses", 99)) {
            this.toolUses = nbttagCompound.getInteger("uses");
        }

        if (nbttagCompound.hasKey("maxUses", 99)) {
            this.maxTradeUses = nbttagCompound.getInteger("maxUses");
        } else {
            this.maxTradeUses = 7;
        }
    }

    public NBTTagCompound writeToTags() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        nbttagCompound.setTag("buy", this.itemToBuy.writeToNBT(new NBTTagCompound()));
        nbttagCompound.setTag("sell", this.itemToSell.writeToNBT(new NBTTagCompound()));
        if (this.secondItemToBuy != null) {
            nbttagCompound.setTag("buyB", this.secondItemToBuy.writeToNBT(new NBTTagCompound()));
        }

        nbttagCompound.setInteger("uses", this.toolUses);
        nbttagCompound.setInteger("maxUses", this.maxTradeUses);
        return nbttagCompound;
    }
}
