package net.minecraft.village;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList {
    public MerchantRecipeList() {
    }

    public MerchantRecipeList(NBTTagCompound nbttagCompound) {
        this.readRecipiesFromTags(nbttagCompound);
    }

    public MerchantRecipe canRecipeBeUsed(ItemStack itemStack, ItemStack itemStack2, int n) {
        if (n > 0 && n < this.size()) {
            MerchantRecipe merchantRecipe = (MerchantRecipe)this.get(n);
            return itemStack.getItem() != merchantRecipe.getItemToBuy().getItem()
                    || (itemStack2 != null || merchantRecipe.hasSecondItemToBuy())
                        && (!merchantRecipe.hasSecondItemToBuy() || itemStack2 == null || merchantRecipe.getSecondItemToBuy().getItem() != itemStack2.getItem())
                    || itemStack.stackSize < merchantRecipe.getItemToBuy().stackSize
                    || merchantRecipe.hasSecondItemToBuy() && itemStack2.stackSize < merchantRecipe.getSecondItemToBuy().stackSize
                ? null
                : merchantRecipe;
        }

        for (int i = 0; i < this.size(); i++) {
            MerchantRecipe merchantRecipe = (MerchantRecipe)this.get(i);
            if (itemStack.getItem() == merchantRecipe.getItemToBuy().getItem()
                && itemStack.stackSize >= merchantRecipe.getItemToBuy().stackSize
                && (
                    !merchantRecipe.hasSecondItemToBuy() && itemStack2 == null
                        || merchantRecipe.hasSecondItemToBuy()
                            && itemStack2 != null
                            && merchantRecipe.getSecondItemToBuy().getItem() == itemStack2.getItem()
                            && itemStack2.stackSize >= merchantRecipe.getSecondItemToBuy().stackSize
                )) {
                return merchantRecipe;
            }
        }

        return null;
    }

    public void addToListWithCheck(MerchantRecipe merchantRecipe) {
        for (int i = 0; i < this.size(); i++) {
            MerchantRecipe merchantRecipe2 = (MerchantRecipe)this.get(i);
            if (merchantRecipe.hasSameIDsAs(merchantRecipe2)) {
                if (merchantRecipe.hasSameItemsAs(merchantRecipe2)) {
                    this.set(i, merchantRecipe);
                }

                return;
            }
        }

        this.add(merchantRecipe);
    }

    public void toPacket(PacketBuffer packetBuffer) throws java.io.IOException {
        packetBuffer.writeByte((byte)(this.size() & 0xFF));

        for (int i = 0; i < this.size(); i++) {
            MerchantRecipe merchantRecipe = (MerchantRecipe)this.get(i);
            packetBuffer.writeItemStackToBuffer(merchantRecipe.getItemToBuy());
            packetBuffer.writeItemStackToBuffer(merchantRecipe.getItemToSell());
            ItemStack secondItemToBuy = merchantRecipe.getSecondItemToBuy();
            packetBuffer.writeBoolean(secondItemToBuy != null);
            if (secondItemToBuy != null) {
                packetBuffer.writeItemStackToBuffer(secondItemToBuy);
            }

            packetBuffer.writeBoolean(merchantRecipe.isRecipeDisabled());
        }
    }

    public static MerchantRecipeList fromPacket(PacketBuffer packetBuffer) throws java.io.IOException {
        MerchantRecipeList merchantRecipeList = new MerchantRecipeList();
        int n = packetBuffer.readByte() & 255;

        for (int i = 0; i < n; i++) {
            ItemStack readItemStackFromBuffer = packetBuffer.readItemStackFromBuffer();
            ItemStack readItemStackFromBuffer2 = packetBuffer.readItemStackFromBuffer();
            ItemStack itemStack = null;
            if (packetBuffer.readBoolean()) {
                itemStack = packetBuffer.readItemStackFromBuffer();
            }

            boolean readBoolean = packetBuffer.readBoolean();
            MerchantRecipe merchantRecipe = new MerchantRecipe(readItemStackFromBuffer, itemStack, readItemStackFromBuffer2);
            if (readBoolean) {
                merchantRecipe.clearUses();
            }

            merchantRecipeList.add(merchantRecipe);
        }

        return merchantRecipeList;
    }

    public void readRecipiesFromTags(NBTTagCompound nbttagCompound) {
        NBTTagList tagList = nbttagCompound.getTagList("Recipes", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            this.add(new MerchantRecipe(compoundTagAt));
        }
    }

    public NBTTagCompound getRecipiesAsTags() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.size(); i++) {
            MerchantRecipe merchantRecipe = (MerchantRecipe)this.get(i);
            nbttagList.appendTag(merchantRecipe.writeToTags());
        }

        nbttagCompound.setTag("Recipes", nbttagList);
        return nbttagCompound;
    }
}
