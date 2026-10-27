package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.config.EntityClassLocator;
import net.optifine.config.IObjectLocator;
import net.optifine.config.ItemLocator;
import net.optifine.reflect.ReflectorForge;
import net.optifine.util.PropertiesOrdered;

public class DynamicLights {
   public static DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
   public static Map<Class, Integer> mapEntityLightLevels = new HashMap<>();
   public static Map<Item, Integer> mapItemLightLevels = new HashMap<>();
   public static long timeUpdateMs = 0L;
   public static final double MAX_DIST = 7.5;
   public static final double MAX_DIST_SQ = 56.25;
   public static final int LIGHT_LEVEL_BLAZE = 10;
   public static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
   public static boolean initialized;
   public static final int CONSTANT_15 = 15;
   public static final int CONSTANT_8 = 8;
   public static final int CONSTANT_15_2 = 15;
   public static final int CONSTANT_8_2 = 8;
   public static final int CONSTANT_8_3 = 8;

   public static void entityAdded(Entity entity, RenderGlobal renderGlobal) {
   }

   public static void entityRemoved(Entity entity, RenderGlobal renderGlobal) {
      synchronized (mapDynamicLights) {
         DynamicLight dynamicLight = mapDynamicLights.remove(entity.getEntityId());
         if (dynamicLight != null) {
            dynamicLight.updateLitChunks(renderGlobal);
         }
      }
   }

   public static void update(RenderGlobal renderGlobal) {
      long currentTimeMillis = System.currentTimeMillis();
      if (currentTimeMillis >= timeUpdateMs + 50L) {
         timeUpdateMs = currentTimeMillis;
         if (!initialized) {
            initialize();
         }

         synchronized (mapDynamicLights) {
            updateMapDynamicLights(renderGlobal);
            if (mapDynamicLights.size() > 0) {
               List valueList = mapDynamicLights.valueList();

               for (int i = 0; i < valueList.size(); i++) {
                  DynamicLight dynamicLight = (DynamicLight)valueList.get(i);
                  dynamicLight.update(renderGlobal);
               }
            }
         }
      }
   }

   public static void initialize() {
      initialized = true;
      mapEntityLightLevels.clear();
      mapItemLightLevels.clear();
      String[] forgeModIds = ReflectorForge.getForgeModIds();

      for (int i = 0; i < forgeModIds.length; i++) {
         String str = forgeModIds[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str, "optifine/dynamic_lights.properties");
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            loadModConfiguration(resourceStream, resourceLocation.toString(), str);
         } catch (IOException e) {
         }
      }

      if (mapEntityLightLevels.size() > 0) {
         Config.dbg("DynamicLights entities: " + mapEntityLightLevels.size());
      }

      if (mapItemLightLevels.size() > 0) {
         Config.dbg("DynamicLights items: " + mapItemLightLevels.size());
      }
   }

   public static void loadModConfiguration(InputStream inputStream, String str, String str2) {
      if (inputStream != null) {
         try {
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream);
            inputStream.close();
            Config.dbg("DynamicLights: Parsing " + str);
            ConnectedParser connectedParser = new ConnectedParser("DynamicLights");
            loadModLightLevels(propertiesOrdered.getProperty("entities"), mapEntityLightLevels, new EntityClassLocator(), connectedParser, str, str2);
            loadModLightLevels(propertiesOrdered.getProperty("items"), mapItemLightLevels, new ItemLocator(), connectedParser, str, str2);
         } catch (IOException e) {
            Config.warn("DynamicLights: Error reading " + str);
         }
      }
   }

   public static void loadModLightLevels(String str, Map map, IObjectLocator objectLocator, ConnectedParser connectedParser, String str2, String str3) {
      if (str != null) {
         String[] tokenize = Config.tokenize(str, " ");

         for (int i = 0; i < tokenize.length; i++) {
            String str4 = tokenize[i];
            String[] tokenize2 = Config.tokenize(str4, ":");
            if (tokenize2.length != 2) {
               connectedParser.warn("Invalid entry: " + str4 + ", in:" + str2);
            } else {
               String str5 = tokenize2[0];
               String str6 = tokenize2[1];
               String str7 = str3 + ":" + str5;
               ResourceLocation resourceLocation = new ResourceLocation(str7);
               Object object = objectLocator.getObject(resourceLocation);
               if (object == null) {
                  connectedParser.warn("Object not found: " + str7);
               } else {
                  int parseInt = connectedParser.parseInt(str6, -1);
                  if (parseInt >= 0 && parseInt <= 15) {
                     map.put(object, new Integer(parseInt));
                  } else {
                     connectedParser.warn("Invalid light level: " + str4);
                  }
               }
            }
         }
      }
   }

   public static void updateMapDynamicLights(RenderGlobal renderGlobal) {
      WorldClient world = renderGlobal.getWorld();
      if (world != null) {
         for (Entity entity : world.getLoadedEntityList()) {
            int lightLevel = getLightLevel(entity);
            if (lightLevel > 0) {
               int entityId = entity.getEntityId();
               DynamicLight dynamicLight = mapDynamicLights.get(entityId);
               if (dynamicLight == null) {
                  dynamicLight = new DynamicLight(entity);
                  mapDynamicLights.put(entityId, dynamicLight);
               }
            } else {
               int entityId = entity.getEntityId();
               DynamicLight dynamicLight = mapDynamicLights.remove(entityId);
               if (dynamicLight != null) {
                  dynamicLight.updateLitChunks(renderGlobal);
               }
            }
         }
      }
   }

   public static int getCombinedLight(BlockPos blockPos, int n) {
      double lightLevel = getLightLevel(blockPos);
      return getCombinedLight(lightLevel, n);
   }

   public static int getCombinedLight(Entity entity, int n) {
      double lightLevel = getLightLevel(entity);
      return getCombinedLight(lightLevel, n);
   }

   public static int getCombinedLight(double d, int n) {
      if (d > 0.0) {
         int n2 = (int)(d * 16.0);
         int n3 = n & 0xFF;
         if (n2 > n3) {
            n &= -256;
            n |= n2;
         }
      }

      return n;
   }

   public static double getLightLevel(BlockPos blockPos) {
      double d = 0.0;
      synchronized (mapDynamicLights) {
         List valueList = mapDynamicLights.valueList();
         int size = valueList.size();

         for (int i = 0; i < size; i++) {
            DynamicLight dynamicLight = (DynamicLight)valueList.get(i);
            int lastLightLevel = dynamicLight.getLastLightLevel();
            if (lastLightLevel > 0) {
               double lastPosX = dynamicLight.getLastPosX();
               double lastPosY = dynamicLight.getLastPosY();
               double lastPosZ = dynamicLight.getLastPosZ();
               double d2 = blockPos.getX() - lastPosX;
               double d3 = blockPos.getY() - lastPosY;
               double d4 = blockPos.getZ() - lastPosZ;
               double d5 = d2 * d2 + d3 * d3 + d4 * d4;
               if (dynamicLight.isUnderwater() && !Config.isClearWater()) {
                  lastLightLevel = Config.limit(lastLightLevel - 2, 0, 15);
                  d5 *= 2.0;
               }

               if (d5 <= 56.25) {
                  double sqrt = Math.sqrt(d5);
                  double d6 = 1.0 - sqrt / 7.5;
                  double d7 = d6 * lastLightLevel;
                  if (d7 > d) {
                     d = d7;
                  }
               }
            }
         }
      }

      return Config.limit(d, 0.0, 15.0);
   }

   public static int getLightLevel(ItemStack itemStack) {
      if (itemStack == null) {
         return 0;
      }

      Item item = itemStack.getItem();
      if (item instanceof ItemBlock) {
         ItemBlock itemBlock = (ItemBlock)item;
         Block block = itemBlock.getBlock();
         if (block != null) {
            return block.getLightValue();
         }
      }

      if (item == Items.lava_bucket) {
         return Blocks.lava.getLightValue();
      }

      if (item == Items.blaze_rod || item == Items.blaze_powder) {
         return 10;
      }

      if (item == Items.glowstone_dust) {
         return 8;
      }

      if (item == Items.prismarine_crystals) {
         return 8;
      }

      if (item == Items.magma_cream) {
         return 8;
      }

      if (item == Items.nether_star) {
         return Blocks.beacon.getLightValue() / 2;
      }

      if (!mapItemLightLevels.isEmpty()) {
         Integer integer = mapItemLightLevels.get(item);
         if (integer != null) {
            return integer;
         }
      }

      return 0;
   }

   public static int getLightLevel(Entity entity) {
      if (entity == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
         return 0;
      }

      if (entity instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)entity;
         if (entityPlayer.isSpectator()) {
            return 0;
         }
      }

      if (entity.isBurning()) {
         return 15;
      }

      if (!mapEntityLightLevels.isEmpty()) {
         Integer integer = mapEntityLightLevels.get(entity.getClass());
         if (integer != null) {
            return integer;
         }
      }

      if (entity instanceof EntityFireball) {
         return 15;
      }

      if (entity instanceof EntityTNTPrimed) {
         return 15;
      }

      if (entity instanceof EntityBlaze) {
         EntityBlaze entityBlaze = (EntityBlaze)entity;
         return entityBlaze.isFireActive() ? 15 : 10;
      }

      if (entity instanceof EntityMagmaCube) {
         EntityMagmaCube entityMagmaCube = (EntityMagmaCube)entity;
         return entityMagmaCube.squishFactor > 0.6 ? 13 : 8;
      }

      if (entity instanceof EntityCreeper) {
         EntityCreeper entityCreeper = (EntityCreeper)entity;
         if (entityCreeper.getCreeperFlashIntensity(0.0F) > 0.001) {
            return 15;
         }
      }

      if (entity instanceof EntityLivingBase) {
         EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
         ItemStack heldItem = entityLivingBase.getHeldItem();
         int lightLevel = getLightLevel(heldItem);
         ItemStack equipmentInSlot = entityLivingBase.getEquipmentInSlot(4);
         int lightLevel2 = getLightLevel(equipmentInSlot);
         return Math.max(lightLevel, lightLevel2);
      } else if (entity instanceof EntityItem) {
         EntityItem entityItem = (EntityItem)entity;
         ItemStack itemStack = getItemStack(entityItem);
         return getLightLevel(itemStack);
      } else {
         return 0;
      }
   }

   public static void removeLights(RenderGlobal renderGlobal) {
      synchronized (mapDynamicLights) {
         List valueList = mapDynamicLights.valueList();

         for (int i = 0; i < valueList.size(); i++) {
            DynamicLight dynamicLight = (DynamicLight)valueList.get(i);
            dynamicLight.updateLitChunks(renderGlobal);
         }

         mapDynamicLights.clear();
      }
   }

   public static void clear() {
      synchronized (mapDynamicLights) {
         mapDynamicLights.clear();
      }
   }

   public static int getCount() {
      synchronized (mapDynamicLights) {
         return mapDynamicLights.size();
      }
   }

   public static ItemStack getItemStack(EntityItem entityItem) {
      return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
   }
}
