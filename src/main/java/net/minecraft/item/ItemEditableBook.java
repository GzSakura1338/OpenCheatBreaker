package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {
    public ItemEditableBook() {
        this.setMaxStackSize(1);
    }

    public static boolean validBookTagContents(NBTTagCompound nbttagCompound) {
        if (!ItemWritableBook.validBookPageTagContents(nbttagCompound)) {
            return false;
        }

        if (!nbttagCompound.hasKey("title", 8)) {
            return false;
        }

        String string = nbttagCompound.getString("title");
        return string != null && string.length() <= 16 && nbttagCompound.hasKey("author", 8);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            String string = tagCompound.getString("title");
            if (!StringUtils.isNullOrEmpty(string)) {
                return string;
            }
        }

        return super.getItemStackDisplayName(itemStack);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            String string = tagCompound.getString("author");
            if (!StringUtils.isNullOrEmpty(string)) {
                list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", string));
            }
        }
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

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }
}
