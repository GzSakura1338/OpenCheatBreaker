package net.minecraft.item;

import net.minecraft.util.EnumChatFormatting;

public enum EnumRarity {
   COMMON(EnumChatFormatting.WHITE, "Common"),
   UNCOMMON(EnumChatFormatting.YELLOW, "Uncommon"),
   RARE(EnumChatFormatting.AQUA, "Rare"),
   EPIC(EnumChatFormatting.LIGHT_PURPLE, "Epic");

   public EnumChatFormatting rarityColor;
   public String rarityName;

   EnumRarity(EnumChatFormatting color, String name) {
      this.rarityColor = color;
      this.rarityName = name;
   }
}
