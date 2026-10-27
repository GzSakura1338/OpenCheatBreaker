package net.minecraft.entity.player;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public enum EnumPlayerModelParts {
   CAPE(0, "cape"),
   JACKET(1, "jacket"),
   LEFT_SLEEVE(2, "left_sleeve"),
   RIGHT_SLEEVE(3, "right_sleeve"),
   LEFT_PANTS_LEG(4, "left_pants_leg"),
   RIGHT_PANTS_LEG(5, "right_pants_leg"),
   HAT(6, "hat");

   public int partId;
   public int partMask;
   public String partName;
   public IChatComponent optionName;

   EnumPlayerModelParts(int partIdIn, String partNameIn) {
      this.partId = partIdIn;
      this.partMask = 1 << partIdIn;
      this.partName = partNameIn;
      this.optionName = new ChatComponentTranslation("options.modelPart." + partNameIn);
   }

   public int getPartMask() {
      return this.partMask;
   }

   public int getPartId() {
      return this.partId;
   }

   public String getPartName() {
      return this.partName;
   }

   public IChatComponent getOptionName() {
      return this.optionName;
   }
}
