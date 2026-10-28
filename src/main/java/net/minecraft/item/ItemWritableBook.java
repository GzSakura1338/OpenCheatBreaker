package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
    public ItemWritableBook() {
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) throws java.io.IOException {
        player.displayGUIBook(itemStackIn);
        return itemStackIn;
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    public static boolean validBookPageTagContents(NBTTagCompound nbttagCompound) {
        if (nbttagCompound == null) {
            return false;
        }

        if (!nbttagCompound.hasKey("pages", 9)) {
            return false;
        }

        NBTTagList tagList = nbttagCompound.getTagList("pages", 8);

        for (int i = 0; i < tagList.tagCount(); i++) {
            String stringTagAt = tagList.getStringTagAt(i);
            if (stringTagAt == null) {
                return false;
            }

            if (stringTagAt.length() > 256) {
                return false;
            }
        }

        return true;
    }
}
