package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityBanner extends TileEntity {
   public int baseColor;
   public NBTTagList patterns;
   public boolean patternListTagRead;
   public List<TileEntityBanner.EnumBannerPattern> patternList;
   public List<EnumDyeColor> colorList;
   public String patternResourceLocation;

   public void setItemValues(ItemStack stack) {
      this.patterns = null;
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", 10)) {
         NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("BlockEntityTag");
         if (compoundTag.hasKey("Patterns")) {
            this.patterns = (NBTTagList)compoundTag.getTagList("Patterns", 10).copy();
         }

         if (compoundTag.hasKey("Base", 99)) {
            this.baseColor = compoundTag.getInteger("Base");
         } else {
            this.baseColor = stack.getMetadata() & 15;
         }
      } else {
         this.baseColor = stack.getMetadata() & 15;
      }

      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = "";
      this.patternListTagRead = true;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      setBaseColorAndPatterns(compound, this.baseColor, this.patterns);
   }

   public static void setBaseColorAndPatterns(NBTTagCompound compound, int baseColorIn, NBTTagList patternsIn) {
      compound.setInteger("Base", baseColorIn);
      if (patternsIn != null) {
         compound.setTag("Patterns", patternsIn);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.baseColor = compound.getInteger("Base");
      this.patterns = compound.getTagList("Patterns", 10);
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = null;
      this.patternListTagRead = true;
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      this.writeToNBT(nbttagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 6, nbttagCompound);
   }

   public int getBaseColor() {
      return this.baseColor;
   }

   public static int getBaseColor(ItemStack stack) {
      NBTTagCompound subCompound = stack.getSubCompound("BlockEntityTag", false);
      return subCompound != null && subCompound.hasKey("Base") ? subCompound.getInteger("Base") : stack.getMetadata();
   }

   public static int getPatterns(ItemStack stack) {
      NBTTagCompound subCompound = stack.getSubCompound("BlockEntityTag", false);
      return subCompound != null && subCompound.hasKey("Patterns") ? subCompound.getTagList("Patterns", 10).tagCount() : 0;
   }

   public List<TileEntityBanner.EnumBannerPattern> getPatternList() {
      this.initializeBannerData();
      return this.patternList;
   }

   public NBTTagList getPatterns() {
      return this.patterns;
   }

   public List<EnumDyeColor> getColorList() {
      this.initializeBannerData();
      return this.colorList;
   }

   public String getPatternResourceLocation() {
      this.initializeBannerData();
      return this.patternResourceLocation;
   }

   public void initializeBannerData() {
      if (this.patternList == null || this.colorList == null || this.patternResourceLocation == null) {
         if (!this.patternListTagRead) {
            this.patternResourceLocation = "";
         } else {
            this.patternList = Lists.newArrayList();
            this.colorList = Lists.newArrayList();
            this.patternList.add(TileEntityBanner.EnumBannerPattern.BASE);
            this.colorList.add(EnumDyeColor.byDyeDamage(this.baseColor));
            this.patternResourceLocation = "b" + this.baseColor;
            if (this.patterns != null) {
               for (int i = 0; i < this.patterns.tagCount(); i++) {
                  NBTTagCompound compoundTagAt = this.patterns.getCompoundTagAt(i);
                  TileEntityBanner.EnumBannerPattern patternByID = TileEntityBanner.EnumBannerPattern.getPatternByID(compoundTagAt.getString("Pattern"));
                  if (patternByID != null) {
                     this.patternList.add(patternByID);
                     int integer = compoundTagAt.getInteger("Color");
                     this.colorList.add(EnumDyeColor.byDyeDamage(integer));
                     this.patternResourceLocation = this.patternResourceLocation + patternByID.getPatternID() + integer;
                  }
               }
            }
         }
      }
   }

   public static void removeBannerData(ItemStack stack) {
      NBTTagCompound subCompound = stack.getSubCompound("BlockEntityTag", false);
      if (subCompound != null && subCompound.hasKey("Patterns", 9)) {
         NBTTagList tagList = subCompound.getTagList("Patterns", 10);
         if (tagList.tagCount() > 0) {
            tagList.removeTag(tagList.tagCount() - 1);
            if (tagList.hasNoTags()) {
               stack.getTagCompound().removeTag("BlockEntityTag");
               if (stack.getTagCompound().hasNoTags()) {
                  stack.setTagCompound((NBTTagCompound)null);
               }
            }
         }
      }
   }

   public enum EnumBannerPattern {
      BASE("base", "b"),
      SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
      SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
      SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
      SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
      STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
      STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
      STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
      STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
      STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
      STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
      STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
      STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
      STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
      CROSS("cross", "cr", "# #", " # ", "# #"),
      STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
      TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
      TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
      TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
      TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
      DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
      DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
      DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
      DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
      CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
      RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
      HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
      HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
      HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
      HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
      BORDER("border", "bo", "###", "# #", "###"),
      CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.vine)),
      CREEPER("creeper", "cre", new ItemStack(Items.skull, 1, 4)),
      GRADIENT("gradient", "gra", "# #", " # ", " # "),
      GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
      BRICKS("bricks", "bri", new ItemStack(Blocks.brick_block)),
      SKULL("skull", "sku", new ItemStack(Items.skull, 1, 1)),
      FLOWER("flower", "flo", new ItemStack(Blocks.red_flower, 1, EnumFlowerType.OXEYE_DAISY.getMeta())),
      MOJANG("mojang", "moj", new ItemStack(Items.golden_apple, 1, 1));

      public String[] craftingLayers = new String[3];
      public String patternName;
      public String patternID;
      public ItemStack patternCraftingStack;

      EnumBannerPattern(String name, String id) {
         this.patternName = name;
         this.patternID = id;
      }

      EnumBannerPattern(String name, String id, ItemStack craftingItem) {
         this(name, id);
         this.patternCraftingStack = craftingItem;
      }

      EnumBannerPattern(String name, String id, String craftingTop, String craftingMid, String craftingBot) {
         this(name, id);
         this.craftingLayers[0] = craftingTop;
         this.craftingLayers[1] = craftingMid;
         this.craftingLayers[2] = craftingBot;
      }

      public String getPatternName() {
         return this.patternName;
      }

      public String getPatternID() {
         return this.patternID;
      }

      public String[] getCraftingLayers() {
         return this.craftingLayers;
      }

      public boolean hasValidCrafting() {
         return this.patternCraftingStack != null || this.craftingLayers[0] != null;
      }

      public boolean hasCraftingStack() {
         return this.patternCraftingStack != null;
      }

      public ItemStack getCraftingStack() {
         return this.patternCraftingStack;
      }

      public static TileEntityBanner.EnumBannerPattern getPatternByID(String id) {
         for (TileEntityBanner.EnumBannerPattern enumBannerPattern : values()) {
            if (enumBannerPattern.patternID.equals(id)) {
               return enumBannerPattern;
            }
         }

         return null;
      }
   }
}
