package net.optifine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorField;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;

public class CustomGuiProperties {
   public String fileName = null;
   public String basePath = null;
   public CustomGuiProperties.EnumContainer container = null;
   public Map<ResourceLocation, ResourceLocation> textureLocations = null;
   public NbtTagValue nbtName = null;
   public BiomeGenBase[] biomes = null;
   public RangeListInt heights = null;
   public Boolean large = null;
   public Boolean trapped = null;
   public Boolean christmas = null;
   public Boolean ender = null;
   public RangeListInt levels = null;
   public VillagerProfession[] professions = null;
   public CustomGuiProperties.EnumVariant[] variants = null;
   public EnumDyeColor[] colors = null;
   public static CustomGuiProperties.EnumVariant[] VARIANTS_HORSE = new CustomGuiProperties.EnumVariant[]{
      CustomGuiProperties.EnumVariant.HORSE,
      CustomGuiProperties.EnumVariant.DONKEY,
      CustomGuiProperties.EnumVariant.MULE,
      CustomGuiProperties.EnumVariant.LLAMA
   };
   public static CustomGuiProperties.EnumVariant[] VARIANTS_DISPENSER = new CustomGuiProperties.EnumVariant[]{
      CustomGuiProperties.EnumVariant.DISPENSER, CustomGuiProperties.EnumVariant.DROPPER
   };
   public static CustomGuiProperties.EnumVariant[] VARIANTS_INVALID = new CustomGuiProperties.EnumVariant[0];
   public static EnumDyeColor[] COLORS_INVALID = new EnumDyeColor[0];
   public static ResourceLocation ANVIL_GUI_TEXTURE = new ResourceLocation("textures/gui/container/anvil.png");
   public static ResourceLocation BEACON_GUI_TEXTURE = new ResourceLocation("textures/gui/container/beacon.png");
   public static ResourceLocation BREWING_STAND_GUI_TEXTURE = new ResourceLocation("textures/gui/container/brewing_stand.png");
   public static ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
   public static ResourceLocation CRAFTING_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");
   public static ResourceLocation HORSE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/horse.png");
   public static ResourceLocation DISPENSER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
   public static ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   public static ResourceLocation FURNACE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
   public static ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
   public static ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
   public static ResourceLocation SHULKER_BOX_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
   public static ResourceLocation VILLAGER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");

   public CustomGuiProperties(Properties properties, String str) {
      ConnectedParser connectedParser = new ConnectedParser("CustomGuis");
      this.fileName = connectedParser.parseName(str);
      this.basePath = connectedParser.parseBasePath(str);
      this.container = (CustomGuiProperties.EnumContainer)connectedParser.parseEnum(properties.getProperty("container"), CustomGuiProperties.EnumContainer.values(), "container");
      this.textureLocations = parseTextureLocations(properties, "texture", this.container, "textures/gui/", this.basePath);
      this.nbtName = connectedParser.parseNbtTagValue("name", properties.getProperty("name"));
      this.biomes = connectedParser.parseBiomes(properties.getProperty("biomes"));
      this.heights = connectedParser.parseRangeListInt(properties.getProperty("heights"));
      this.large = connectedParser.parseBooleanObject(properties.getProperty("large"));
      this.trapped = connectedParser.parseBooleanObject(properties.getProperty("trapped"));
      this.christmas = connectedParser.parseBooleanObject(properties.getProperty("christmas"));
      this.ender = connectedParser.parseBooleanObject(properties.getProperty("ender"));
      this.levels = connectedParser.parseRangeListInt(properties.getProperty("levels"));
      this.professions = connectedParser.parseProfessions(properties.getProperty("professions"));
      CustomGuiProperties.EnumVariant[] containerVariants = getContainerVariants(this.container);
      this.variants = (CustomGuiProperties.EnumVariant[])connectedParser.parseEnums(properties.getProperty("variants"), containerVariants, "variants", VARIANTS_INVALID);
      this.colors = parseEnumDyeColors(properties.getProperty("colors"));
   }

   public static CustomGuiProperties.EnumVariant[] getContainerVariants(CustomGuiProperties.EnumContainer enumContainer) {
      return enumContainer == CustomGuiProperties.EnumContainer.HORSE
         ? VARIANTS_HORSE
         : (enumContainer == CustomGuiProperties.EnumContainer.DISPENSER ? VARIANTS_DISPENSER : new CustomGuiProperties.EnumVariant[0]);
   }

   public static EnumDyeColor[] parseEnumDyeColors(String str) {
      if (str == null) {
         return null;
      }

      str = str.toLowerCase();
      String[] tokenize = Config.tokenize(str, " ");
      EnumDyeColor[] enumDyeColors = new EnumDyeColor[tokenize.length];

      for (int i = 0; i < tokenize.length; i++) {
         String str2 = tokenize[i];
         EnumDyeColor parseEnumDyeColor2 = parseEnumDyeColor(str2);
         if (parseEnumDyeColor2 == null) {
            warn("Invalid color: " + str2);
            return COLORS_INVALID;
         }

         enumDyeColors[i] = parseEnumDyeColor2;
      }

      return enumDyeColors;
   }

   public static EnumDyeColor parseEnumDyeColor(String str) {
      if (str == null) {
         return null;
      }

      EnumDyeColor[] values = EnumDyeColor.values();

      for (int i = 0; i < values.length; i++) {
         EnumDyeColor enumDyeColor = values[i];
         if (enumDyeColor.getName().equals(str)) {
            return enumDyeColor;
         }

         if (enumDyeColor.getUnlocalizedName().equals(str)) {
            return enumDyeColor;
         }
      }

      return null;
   }

   public static ResourceLocation parseTextureLocation(String str, String str2) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      String fixResourcePath = TextureUtils.fixResourcePath(str, str2);
      if (!fixResourcePath.endsWith(".png")) {
         fixResourcePath = fixResourcePath + ".png";
      }

      return new ResourceLocation(str2 + "/" + fixResourcePath);
   }

   public static Map<ResourceLocation, ResourceLocation> parseTextureLocations(
      Properties properties, String str, CustomGuiProperties.EnumContainer enumContainer, String str2, String str3
   ) {
      HashMap map = new HashMap();
      String property = properties.getProperty(str);
      if (property != null) {
         ResourceLocation guiTextureLocation = getGuiTextureLocation(enumContainer);
         ResourceLocation parseTextureLocation2 = parseTextureLocation(property, str3);
         if (guiTextureLocation != null && parseTextureLocation2 != null) {
            map.put(guiTextureLocation, parseTextureLocation2);
         }
      }

      String str4 = str + ".";

      for (Object obj : properties.keySet()) {
         String str5 = (String)obj;
         if (str5.startsWith(str4)) {
            String substring = str5.substring(str4.length());
            substring = substring.replace('\\', '/');
            substring = StrUtils.removePrefixSuffix(substring, "/", ".png");
            String str6 = str2 + substring + ".png";
            String property2 = properties.getProperty(str5);
            ResourceLocation resourceLocation = new ResourceLocation(str6);
            ResourceLocation parseTextureLocation2 = parseTextureLocation(property2, str3);
            map.put(resourceLocation, parseTextureLocation2);
         }
      }

      return map;
   }

   public static ResourceLocation getGuiTextureLocation(CustomGuiProperties.EnumContainer enumContainer) {
      if (enumContainer == null) {
         return null;
      }

      switch (enumContainer) {
         case ANVIL:
            return ANVIL_GUI_TEXTURE;
         case BEACON:
            return BEACON_GUI_TEXTURE;
         case BREWING_STAND:
            return BREWING_STAND_GUI_TEXTURE;
         case CHEST:
            return CHEST_GUI_TEXTURE;
         case CRAFTING:
            return CRAFTING_TABLE_GUI_TEXTURE;
         case CREATIVE:
            return null;
         case DISPENSER:
            return DISPENSER_GUI_TEXTURE;
         case ENCHANTMENT:
            return ENCHANTMENT_TABLE_GUI_TEXTURE;
         case FURNACE:
            return FURNACE_GUI_TEXTURE;
         case HOPPER:
            return HOPPER_GUI_TEXTURE;
         case HORSE:
            return HORSE_GUI_TEXTURE;
         case INVENTORY:
            return INVENTORY_GUI_TEXTURE;
         case SHULKER_BOX:
            return SHULKER_BOX_GUI_TEXTURE;
         case VILLAGER:
            return VILLAGER_GUI_TEXTURE;
         default:
            return null;
      }
   }

   public boolean isValid(String str) {
      if (this.fileName == null || this.fileName.length() <= 0) {
         warn("No name found: " + str);
         return false;
      } else if (this.basePath == null) {
         warn("No base path found: " + str);
         return false;
      } else if (this.container == null) {
         warn("No container found: " + str);
         return false;
      } else if (this.textureLocations.isEmpty()) {
         warn("No texture found: " + str);
         return false;
      } else if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
         warn("Invalid professions or careers: " + str);
         return false;
      } else if (this.variants == VARIANTS_INVALID) {
         warn("Invalid variants: " + str);
         return false;
      } else if (this.colors == COLORS_INVALID) {
         warn("Invalid colors: " + str);
         return false;
      } else {
         return true;
      }
   }

   public static void warn(String str) {
      Config.warn("[CustomGuis] " + str);
   }

   public boolean matchesGeneral(CustomGuiProperties.EnumContainer enumContainer, BlockPos blockPos, IBlockAccess blockAccess) {
      if (this.container != enumContainer) {
         return false;
      }

      if (this.biomes != null) {
         BiomeGenBase biomeGenForCoords = blockAccess.getBiomeGenForCoords(blockPos);
         if (!Matches.biome(biomeGenForCoords, this.biomes)) {
            return false;
         }
      }

      return this.heights == null || this.heights.isInRange(blockPos.getY());
   }

   public boolean matchesPos(CustomGuiProperties.EnumContainer enumContainer, BlockPos blockPos, IBlockAccess blockAccess, GuiScreen guiScreen) {
      if (!this.matchesGeneral(enumContainer, blockPos, blockAccess)) {
         return false;
      }

      if (this.nbtName != null) {
         String name = getName(guiScreen);
         if (!this.nbtName.matchesValue(name)) {
            return false;
         }
      }

      switch (enumContainer) {
         case BEACON:
            return this.matchesBeacon(blockPos, blockAccess);
         case CHEST:
            return this.matchesChest(blockPos, blockAccess);
         case DISPENSER:
            return this.matchesDispenser(blockPos, blockAccess);
         default:
            return true;
      }
   }

   public static String getName(GuiScreen guiScreen) {
      IWorldNameable worldNameable = getWorldNameable(guiScreen);
      return worldNameable == null ? null : worldNameable.getDisplayName().getUnformattedText();
   }

   public static IWorldNameable getWorldNameable(GuiScreen guiScreen) {
      return guiScreen instanceof GuiBeacon
         ? getWorldNameable(guiScreen, Reflector.GuiBeacon_tileBeacon)
         : (
            guiScreen instanceof GuiBrewingStand
               ? getWorldNameable(guiScreen, Reflector.GuiBrewingStand_tileBrewingStand)
               : (
                  guiScreen instanceof GuiChest
                     ? getWorldNameable(guiScreen, Reflector.GuiChest_lowerChestInventory)
                     : (
                        guiScreen instanceof GuiDispenser
                           ? ((GuiDispenser)guiScreen).dispenserInventory
                           : (
                              guiScreen instanceof GuiEnchantment
                                 ? getWorldNameable(guiScreen, Reflector.GuiEnchantment_nameable)
                                 : (
                                    guiScreen instanceof GuiFurnace
                                       ? getWorldNameable(guiScreen, Reflector.GuiFurnace_tileFurnace)
                                       : (guiScreen instanceof GuiHopper ? getWorldNameable(guiScreen, Reflector.GuiHopper_hopperInventory) : null)
                                 )
                           )
                     )
               )
         );
   }

   public static IWorldNameable getWorldNameable(GuiScreen guiScreen, ReflectorField reflectorField) {
      Object fieldValue = Reflector.getFieldValue(guiScreen, reflectorField);
      return !(fieldValue instanceof IWorldNameable) ? null : (IWorldNameable)fieldValue;
   }

   public boolean matchesBeacon(BlockPos blockPos, IBlockAccess blockAccess) {
      TileEntity tileEntity = blockAccess.getTileEntity(blockPos);
      if (!(tileEntity instanceof TileEntityBeacon)) {
         return false;
      }

      TileEntityBeacon tileEntityBeacon = (TileEntityBeacon)tileEntity;
      if (this.levels != null) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         tileEntityBeacon.writeToNBT(nbttagCompound);
         int integer = nbttagCompound.getInteger("Levels");
         if (!this.levels.isInRange(integer)) {
            return false;
         }
      }

      return true;
   }

   public boolean matchesChest(BlockPos blockPos, IBlockAccess blockAccess) {
      TileEntity tileEntity = blockAccess.getTileEntity(blockPos);
      if (tileEntity instanceof TileEntityChest) {
         TileEntityChest tileEntityChest = (TileEntityChest)tileEntity;
         return this.matchesChest(tileEntityChest, blockPos, blockAccess);
      } else if (tileEntity instanceof TileEntityEnderChest) {
         TileEntityEnderChest tileEntityEnderChest = (TileEntityEnderChest)tileEntity;
         return this.matchesEnderChest(tileEntityEnderChest, blockPos, blockAccess);
      } else {
         return false;
      }
   }

   public boolean matchesChest(TileEntityChest tileEntityChest, BlockPos blockPos, IBlockAccess blockAccess) {
      boolean flag = tileEntityChest.adjacentChestXNeg != null || tileEntityChest.adjacentChestXPos != null || tileEntityChest.adjacentChestZNeg != null || tileEntityChest.adjacentChestZPos != null;
      boolean flag2 = tileEntityChest.getChestType() == 1;
      boolean isChristmas = CustomGuis.isChristmas;
      boolean flag3 = false;
      return this.matchesChest(flag, flag2, isChristmas, flag3);
   }

   public boolean matchesEnderChest(TileEntityEnderChest tileEntityEnderChest, BlockPos blockPos, IBlockAccess blockAccess) {
      return this.matchesChest(false, false, false, true);
   }

   public boolean matchesChest(boolean flag, boolean flag2, boolean flag3, boolean flag4) {
      return this.large != null && this.large != flag
         ? false
         : (
            this.trapped != null && this.trapped != flag2
               ? false
               : (this.christmas != null && this.christmas != flag3 ? false : this.ender == null || this.ender == flag4)
         );
   }

   public boolean matchesDispenser(BlockPos blockPos, IBlockAccess blockAccess) {
      TileEntity tileEntity = blockAccess.getTileEntity(blockPos);
      if (!(tileEntity instanceof TileEntityDispenser)) {
         return false;
      }

      TileEntityDispenser tileEntityDispenser = (TileEntityDispenser)tileEntity;
      if (this.variants != null) {
         CustomGuiProperties.EnumVariant dispenserVariant = this.getDispenserVariant(tileEntityDispenser);
         if (!Config.equalsOne(dispenserVariant, this.variants)) {
            return false;
         }
      }

      return true;
   }

   public CustomGuiProperties.EnumVariant getDispenserVariant(TileEntityDispenser tileEntityDispenser) {
      return tileEntityDispenser instanceof TileEntityDropper ? CustomGuiProperties.EnumVariant.DROPPER : CustomGuiProperties.EnumVariant.DISPENSER;
   }

   public boolean matchesEntity(CustomGuiProperties.EnumContainer enumContainer, Entity entity, IBlockAccess blockAccess) {
      if (!this.matchesGeneral(enumContainer, entity.getPosition(), blockAccess)) {
         return false;
      }

      if (this.nbtName != null) {
         String name = entity.getName();
         if (!this.nbtName.matchesValue(name)) {
            return false;
         }
      }

      switch (enumContainer) {
         case HORSE:
            return this.matchesHorse(entity, blockAccess);
         case VILLAGER:
            return this.matchesVillager(entity, blockAccess);
         default:
            return true;
      }
   }

   public boolean matchesVillager(Entity entity, IBlockAccess blockAccess) {
      if (!(entity instanceof EntityVillager)) {
         return false;
      }

      EntityVillager entityVillager = (EntityVillager)entity;
      if (this.professions != null) {
         int profession = entityVillager.getProfession();
         int fieldValueInt = Reflector.getFieldValueInt(entityVillager, Reflector.EntityVillager_careerId, -1);
         if (fieldValueInt < 0) {
            return false;
         }

         boolean flag = false;

         for (int i = 0; i < this.professions.length; i++) {
            VillagerProfession villagerProfession = this.professions[i];
            if (villagerProfession.matches(profession, fieldValueInt)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         }
      }

      return true;
   }

   public boolean matchesHorse(Entity entity, IBlockAccess blockAccess) {
      if (!(entity instanceof EntityHorse)) {
         return false;
      }

      EntityHorse entityHorse = (EntityHorse)entity;
      if (this.variants != null) {
         CustomGuiProperties.EnumVariant horseVariant = this.getHorseVariant(entityHorse);
         if (!Config.equalsOne(horseVariant, this.variants)) {
            return false;
         }
      }

      return true;
   }

   public CustomGuiProperties.EnumVariant getHorseVariant(EntityHorse entityHorse) {
      int horseType = entityHorse.getHorseType();
      switch (horseType) {
         case 0:
            return CustomGuiProperties.EnumVariant.HORSE;
         case 1:
            return CustomGuiProperties.EnumVariant.DONKEY;
         case 2:
            return CustomGuiProperties.EnumVariant.MULE;
         default:
            return null;
      }
   }

   public CustomGuiProperties.EnumContainer getContainer() {
      return this.container;
   }

   public ResourceLocation getTextureLocation(ResourceLocation resourceLocation) {
      ResourceLocation resourceLocation2 = this.textureLocations.get(resourceLocation);
      return resourceLocation2 == null ? resourceLocation : resourceLocation2;
   }

   @Override
   public String toString() {
      return "name: " + this.fileName + ", container: " + this.container + ", textures: " + this.textureLocations;
   }

   public enum EnumContainer {
      ANVIL,
      BEACON,
      BREWING_STAND,
      CHEST,
      CRAFTING,
      DISPENSER,
      ENCHANTMENT,
      FURNACE,
      HOPPER,
      HORSE,
      VILLAGER,
      SHULKER_BOX,
      CREATIVE,
      INVENTORY;

      public static CustomGuiProperties.EnumContainer[] VALUES = values();
   }

   private enum EnumVariant {
      HORSE,
      DONKEY,
      MULE,
      LLAMA,
      DISPENSER,
      DROPPER;
   }
}
