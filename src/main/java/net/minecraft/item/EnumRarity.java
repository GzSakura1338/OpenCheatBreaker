package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity {
    common(EnumChatFormatting.WHITE, "Common"),
    uncommon(EnumChatFormatting.YELLOW, "Uncommon"),
    rare(EnumChatFormatting.AQUA, "Rare"),
    epic(EnumChatFormatting.LIGHT_PURPLE, "Epic");

    public EnumChatFormatting rarityColor;
    public String rarityName;

    EnumRarity(EnumChatFormatting enumChatFormatting, String str) {
        this.rarityColor = enumChatFormatting;
        this.rarityName = str;
    }
}
