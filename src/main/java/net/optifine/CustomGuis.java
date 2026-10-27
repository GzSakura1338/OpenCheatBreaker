package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.optifine.override.PlayerControllerOF;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomGuis {
   public static Minecraft mc = Config.getMinecraft();
   public static PlayerControllerOF playerControllerOF = null;
   public static CustomGuiProperties[][] guiProperties = (CustomGuiProperties[][])null;
   public static boolean isChristmas = isChristmas();

   public static ResourceLocation getTextureLocation(ResourceLocation resourceLocation) {
      if (guiProperties == null) {
         return resourceLocation;
      }

      GuiScreen currentScreen = mc.currentScreen;
      if (!(currentScreen instanceof GuiContainer)) {
         return resourceLocation;
      }

      if (!resourceLocation.getResourceDomain().equals("minecraft") || !resourceLocation.getResourcePath().startsWith("textures/gui/")) {
         return resourceLocation;
      }

      if (playerControllerOF == null) {
         return resourceLocation;
      }

      WorldClient theWorld = mc.theWorld;
      if (theWorld == null) {
         return resourceLocation;
      }

      if (currentScreen instanceof GuiContainerCreative) {
         return getTexturePos(CustomGuiProperties.EnumContainer.CREATIVE, mc.thePlayer.getPosition(), theWorld, resourceLocation, currentScreen);
      }

      if (currentScreen instanceof GuiInventory) {
         return getTexturePos(CustomGuiProperties.EnumContainer.INVENTORY, mc.thePlayer.getPosition(), theWorld, resourceLocation, currentScreen);
      }

      BlockPos lastClickBlockPos = playerControllerOF.getLastClickBlockPos();
      if (lastClickBlockPos != null) {
         if (currentScreen instanceof GuiRepair) {
            return getTexturePos(CustomGuiProperties.EnumContainer.ANVIL, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiBeacon) {
            return getTexturePos(CustomGuiProperties.EnumContainer.BEACON, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiBrewingStand) {
            return getTexturePos(CustomGuiProperties.EnumContainer.BREWING_STAND, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiChest) {
            return getTexturePos(CustomGuiProperties.EnumContainer.CHEST, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiCrafting) {
            return getTexturePos(CustomGuiProperties.EnumContainer.CRAFTING, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiDispenser) {
            return getTexturePos(CustomGuiProperties.EnumContainer.DISPENSER, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiEnchantment) {
            return getTexturePos(CustomGuiProperties.EnumContainer.ENCHANTMENT, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiFurnace) {
            return getTexturePos(CustomGuiProperties.EnumContainer.FURNACE, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }

         if (currentScreen instanceof GuiHopper) {
            return getTexturePos(CustomGuiProperties.EnumContainer.HOPPER, lastClickBlockPos, theWorld, resourceLocation, currentScreen);
         }
      }

      Entity lastClickEntity = playerControllerOF.getLastClickEntity();
      if (lastClickEntity != null) {
         if (currentScreen instanceof GuiScreenHorseInventory) {
            return getTextureEntity(CustomGuiProperties.EnumContainer.HORSE, lastClickEntity, theWorld, resourceLocation);
         }

         if (currentScreen instanceof GuiMerchant) {
            return getTextureEntity(CustomGuiProperties.EnumContainer.VILLAGER, lastClickEntity, theWorld, resourceLocation);
         }
      }

      return resourceLocation;
   }

   public static ResourceLocation getTexturePos(CustomGuiProperties.EnumContainer enumContainer, BlockPos blockPos, IBlockAccess blockAccess, ResourceLocation resourceLocation, GuiScreen guiScreen) {
      CustomGuiProperties[] customGuiPropertieses = guiProperties[enumContainer.ordinal()];
      if (customGuiPropertieses == null) {
         return resourceLocation;
      }

      for (int i = 0; i < customGuiPropertieses.length; i++) {
         CustomGuiProperties customGuiProperties = customGuiPropertieses[i];
         if (customGuiProperties.matchesPos(enumContainer, blockPos, blockAccess, guiScreen)) {
            return customGuiProperties.getTextureLocation(resourceLocation);
         }
      }

      return resourceLocation;
   }

   public static ResourceLocation getTextureEntity(CustomGuiProperties.EnumContainer enumContainer, Entity entity, IBlockAccess blockAccess, ResourceLocation resourceLocation) {
      CustomGuiProperties[] customGuiPropertieses = guiProperties[enumContainer.ordinal()];
      if (customGuiPropertieses == null) {
         return resourceLocation;
      }

      for (int i = 0; i < customGuiPropertieses.length; i++) {
         CustomGuiProperties customGuiProperties = customGuiPropertieses[i];
         if (customGuiProperties.matchesEntity(enumContainer, entity, blockAccess)) {
            return customGuiProperties.getTextureLocation(resourceLocation);
         }
      }

      return resourceLocation;
   }

   public static void update() {
      guiProperties = (CustomGuiProperties[][])null;
      if (Config.isCustomGuis()) {
         ArrayList list = new ArrayList();
         IResourcePack[] resourcePacks = Config.getResourcePacks();

         for (int i = resourcePacks.length - 1; i >= 0; i--) {
            IResourcePack resourcePack = resourcePacks[i];
            update(resourcePack, list);
         }

         guiProperties = propertyListToArray(list);
      }
   }

   public static CustomGuiProperties[][] propertyListToArray(List<List<CustomGuiProperties>> list) {
      if (list.isEmpty()) {
         return (CustomGuiProperties[][])null;
      }

      CustomGuiProperties[][] customGuiPropertieseses = new CustomGuiProperties[CustomGuiProperties.EnumContainer.VALUES.length][];

      for (int i = 0; i < customGuiPropertieseses.length; i++) {
         if (list.size() > i) {
            List list2 = (List)list.get(i);
            if (list2 != null) {
               CustomGuiProperties[] toArray = (CustomGuiProperties[])list2.toArray(new CustomGuiProperties[list2.size()]);
               customGuiPropertieseses[i] = toArray;
            }
         }
      }

      return customGuiPropertieseses;
   }

   public static void update(IResourcePack resourcePack, List<List<CustomGuiProperties>> list) {
      String[] collectFiles = ResUtils.collectFiles(resourcePack, "optifine/gui/container/", ".properties", (String[])null);
      Arrays.sort(collectFiles);

      for (int i = 0; i < collectFiles.length; i++) {
         String str = collectFiles[i];
         Config.dbg("CustomGuis: " + str);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            InputStream inputStream = resourcePack.getInputStream(resourceLocation);
            if (inputStream == null) {
               Config.warn("CustomGuis file not found: " + str);
            } else {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               inputStream.close();
               CustomGuiProperties customGuiProperties = new CustomGuiProperties(propertiesOrdered, str);
               if (customGuiProperties.isValid(str)) {
                  addToList(customGuiProperties, list);
               }
            }
         } catch (FileNotFoundException e) {
            Config.warn("CustomGuis file not found: " + str);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public static void addToList(CustomGuiProperties customGuiProperties, List<List<CustomGuiProperties>> list) {
      if (customGuiProperties.getContainer() == null) {
         warn("Invalid container: " + customGuiProperties.getContainer());
      } else {
         int n = customGuiProperties.getContainer().ordinal();

         while (list.size() <= n) {
            list.add(null);
         }

         List list2 = (List)list.get(n);
         if (list2 == null) {
            list2 = new ArrayList();
            list.set(n, list2);
         }

         list2.add(customGuiProperties);
      }
   }

   public static PlayerControllerOF getPlayerControllerOF() {
      return playerControllerOF;
   }

   public static void setPlayerControllerOF(PlayerControllerOF playerControllerOF) {
   }

   public static boolean isChristmas() {
      Calendar instance = Calendar.getInstance();
      return instance.get(2) + 1 == 12 && instance.get(5) >= 24 && instance.get(5) <= 26;
   }

   public static void warn(String str) {
      Config.warn("[CustomGuis] " + str);
   }
}
