package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorRaw;
import net.optifine.util.IntegratedServerUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class RandomEntities {
   public static Map<String, RandomEntityProperties> mapProperties = new HashMap<>();
   public static boolean active = false;
   public static RenderGlobal renderGlobal;
   public static RandomEntity randomEntity = new RandomEntity();
   public static TileEntityRendererDispatcher tileEntityRendererDispatcher;
   public static RandomTileEntity randomTileEntity = new RandomTileEntity();
   public static boolean working = false;
   public static final String SUFFIX_PNG = ".png";
   public static final String SUFFIX_PROPERTIES = ".properties";
   public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
   public static final String PREFIX_TEXTURES_PAINTING = "textures/painting/";
   public static final String PREFIX_TEXTURES = "textures/";
   public static final String PREFIX_OPTIFINE_RANDOM = "optifine/random/";
   public static final String PREFIX_MCPATCHER_MOB = "mcpatcher/mob/";
   public static String[] DEPENDANT_SUFFIXES = new String[]{
      "_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"
   };
   public static final String PREFIX_DYNAMIC_TEXTURE_HORSE = "horse/";
   public static String[] HORSE_TEXTURES = (String[])ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 2);
   public static String[] HORSE_TEXTURES_ABBR = (String[])ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 3);

   public static void entityLoaded(Entity entity, World world) {
      if (world != null) {
         DataWatcher dataWatcher = entity.getDataWatcher();
         dataWatcher.spawnPosition = entity.getPosition();
         dataWatcher.spawnBiome = world.getBiomeGenForCoords(dataWatcher.spawnPosition);
         UUID uniqueID = entity.getUniqueID();
         if (entity instanceof EntityVillager) {
            updateEntityVillager(uniqueID, (EntityVillager)entity);
         }
      }
   }

   public static void entityUnloaded(Entity entity, World world) {
   }

   public static void updateEntityVillager(UUID uuid, EntityVillager entityVillager) {
      Entity entity = IntegratedServerUtils.getEntity(uuid);
      if (entity instanceof EntityVillager) {
         EntityVillager entityVillager2 = (EntityVillager)entity;
         int profession = entityVillager2.getProfession();
         entityVillager.setProfession(profession);
         int fieldValueInt = Reflector.getFieldValueInt(entityVillager2, Reflector.EntityVillager_careerId, 0);
         Reflector.setFieldValueInt(entityVillager, Reflector.EntityVillager_careerId, fieldValueInt);
         int fieldValueInt2 = Reflector.getFieldValueInt(entityVillager2, Reflector.EntityVillager_careerLevel, 0);
         Reflector.setFieldValueInt(entityVillager, Reflector.EntityVillager_careerLevel, fieldValueInt2);
      }
   }

   public static void worldChanged(World world, World world2) {
      if (world2 != null) {
         List loadedEntityList = world2.getLoadedEntityList();

         for (int i = 0; i < loadedEntityList.size(); i++) {
            Entity entity = (Entity)loadedEntityList.get(i);
            entityLoaded(entity, world2);
         }
      }

      randomEntity.setEntity((Entity)null);
      randomTileEntity.setTileEntity((TileEntity)null);
   }

   public static ResourceLocation getTextureLocation(ResourceLocation resourceLocation) {
      if (!active) {
         return resourceLocation;
      }

      if (working) {
         return resourceLocation;
      }

      ResourceLocation resourceLocation2;
      try {
         working = true;
         IRandomEntity randomEntityRendered = getRandomEntityRendered();
         if (randomEntityRendered != null) {
            String resourcePath = resourceLocation.getResourcePath();
            if (resourcePath.startsWith("horse/")) {
               resourcePath = getHorseTexturePath(resourcePath, "horse/".length());
            }

            if (!resourcePath.startsWith("textures/entity/") && !resourcePath.startsWith("textures/painting/")) {
               return resourceLocation;
            }

            RandomEntityProperties randomEntityProperties = mapProperties.get(resourcePath);
            if (randomEntityProperties == null) {
               return resourceLocation;
            }

            return randomEntityProperties.getTextureLocation(resourceLocation, randomEntityRendered);
         }

         resourceLocation2 = resourceLocation;
      } finally {
         working = false;
      }

      return resourceLocation2;
   }

   public static String getHorseTexturePath(String str, int n) {
      if (HORSE_TEXTURES != null && HORSE_TEXTURES_ABBR != null) {
         for (int i = 0; i < HORSE_TEXTURES_ABBR.length; i++) {
            String str2 = HORSE_TEXTURES_ABBR[i];
            if (str.startsWith(str2, n)) {
               return HORSE_TEXTURES[i];
            }
         }

         return str;
      } else {
         return str;
      }
   }

   public static IRandomEntity getRandomEntityRendered() {
      if (renderGlobal.renderedEntity != null) {
         randomEntity.setEntity(renderGlobal.renderedEntity);
         return randomEntity;
      }

      if (tileEntityRendererDispatcher.tileEntityRendered != null) {
         TileEntity tileEntityRendered = tileEntityRendererDispatcher.tileEntityRendered;
         if (tileEntityRendered.getWorld() != null) {
            randomTileEntity.setTileEntity(tileEntityRendered);
            return randomTileEntity;
         }
      }

      return null;
   }

   public static RandomEntityProperties makeProperties(ResourceLocation resourceLocation, boolean flag) {
      String resourcePath = resourceLocation.getResourcePath();
      ResourceLocation locationProperties = getLocationProperties(resourceLocation, flag);
      if (locationProperties != null) {
         RandomEntityProperties parseProperties2 = parseProperties(locationProperties, resourceLocation);
         if (parseProperties2 != null) {
            return parseProperties2;
         }
      }

      ResourceLocation[] locationsVariants = getLocationsVariants(resourceLocation, flag);
      return locationsVariants == null ? null : new RandomEntityProperties(resourcePath, locationsVariants);
   }

   public static RandomEntityProperties parseProperties(ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
      try {
         String resourcePath = resourceLocation.getResourcePath();
         dbg(resourceLocation2.getResourcePath() + ", properties: " + resourcePath);
         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            warn("Properties not found: " + resourcePath);
            return null;
         } else {
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            RandomEntityProperties randomEntityProperties = new RandomEntityProperties(propertiesOrdered, resourcePath, resourceLocation2);
            return !randomEntityProperties.isValid(resourcePath) ? null : randomEntityProperties;
         }
      } catch (FileNotFoundException e) {
         warn("File not found: " + resourceLocation2.getResourcePath());
         return null;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   public static ResourceLocation getLocationProperties(ResourceLocation resourceLocation, boolean flag) {
      ResourceLocation locationRandom = getLocationRandom(resourceLocation, flag);
      if (locationRandom == null) {
         return null;
      }

      String resourceDomain = locationRandom.getResourceDomain();
      String resourcePath = locationRandom.getResourcePath();
      String removeSuffix = StrUtils.removeSuffix(resourcePath, ".png");
      String str = removeSuffix + ".properties";
      ResourceLocation resourceLocation2 = new ResourceLocation(resourceDomain, str);
      if (Config.hasResource(resourceLocation2)) {
         return resourceLocation2;
      }

      String parentTexturePath = getParentTexturePath(removeSuffix);
      if (parentTexturePath == null) {
         return null;
      }

      ResourceLocation resourceLocation3 = new ResourceLocation(resourceDomain, parentTexturePath + ".properties");
      return Config.hasResource(resourceLocation3) ? resourceLocation3 : null;
   }

   public static ResourceLocation getLocationRandom(ResourceLocation resourceLocation, boolean flag) {
      String resourceDomain = resourceLocation.getResourceDomain();
      String resourcePath = resourceLocation.getResourcePath();
      String str = "textures/";
      String str2 = "optifine/random/";
      if (flag) {
         str = "textures/entity/";
         str2 = "mcpatcher/mob/";
      }

      if (!resourcePath.startsWith(str)) {
         return null;
      }

      String replacePrefix = StrUtils.replacePrefix(resourcePath, str, str2);
      return new ResourceLocation(resourceDomain, replacePrefix);
   }

   public static String getPathBase(String str) {
      return str.startsWith("optifine/random/")
         ? StrUtils.replacePrefix(str, "optifine/random/", "textures/")
         : (str.startsWith("mcpatcher/mob/") ? StrUtils.replacePrefix(str, "mcpatcher/mob/", "textures/entity/") : null);
   }

   public static ResourceLocation getLocationIndexed(ResourceLocation resourceLocation, int n) {
      if (resourceLocation == null) {
         return null;
      }

      String resourcePath = resourceLocation.getResourcePath();
      int lastIndexOf = resourcePath.lastIndexOf(46);
      if (lastIndexOf < 0) {
         return null;
      }

      String substring = resourcePath.substring(0, lastIndexOf);
      String substring2 = resourcePath.substring(lastIndexOf);
      String str = substring + n + substring2;
      return new ResourceLocation(resourceLocation.getResourceDomain(), str);
   }

   public static String getParentTexturePath(String str) {
      for (int i = 0; i < DEPENDANT_SUFFIXES.length; i++) {
         String str2 = DEPENDANT_SUFFIXES[i];
         if (str.endsWith(str2)) {
            return StrUtils.removeSuffix(str, str2);
         }
      }

      return null;
   }

   public static ResourceLocation[] getLocationsVariants(ResourceLocation resourceLocation, boolean flag) {
      ArrayList list = new ArrayList();
      list.add(resourceLocation);
      ResourceLocation locationRandom = getLocationRandom(resourceLocation, flag);
      if (locationRandom == null) {
         return null;
      }

      for (int i = 1; i < list.size() + 10; i++) {
         int n = i + 1;
         ResourceLocation locationIndexed = getLocationIndexed(locationRandom, n);
         if (Config.hasResource(locationIndexed)) {
            list.add(locationIndexed);
         }
      }

      if (list.size() <= 1) {
         return null;
      }

      ResourceLocation[] toArray = (ResourceLocation[])list.toArray(new ResourceLocation[list.size()]);
      dbg(resourceLocation.getResourcePath() + ", variants: " + toArray.length);
      return toArray;
   }

   public static void update() {
      mapProperties.clear();
      active = false;
      if (Config.isRandomEntities()) {
         initialize();
      }
   }

   public static void initialize() {
      renderGlobal = Config.getRenderGlobal();
      tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
      String[] strs = new String[]{"optifine/random/", "mcpatcher/mob/"};
      String[] strs2 = new String[]{".png", ".properties"};
      String[] collectFiles = ResUtils.collectFiles(strs, strs2);
      HashSet set = new HashSet();

      for (int i = 0; i < collectFiles.length; i++) {
         String str = collectFiles[i];
         str = StrUtils.removeSuffix(str, strs2);
         str = StrUtils.trimTrailing(str, "0123456789");
         str = str + ".png";
         String pathBase = getPathBase(str);
         if (!set.contains(pathBase)) {
            set.add(pathBase);
            ResourceLocation resourceLocation = new ResourceLocation(pathBase);
            if (Config.hasResource(resourceLocation)) {
               RandomEntityProperties randomEntityProperties = mapProperties.get(pathBase);
               if (randomEntityProperties == null) {
                  randomEntityProperties = makeProperties(resourceLocation, false);
                  if (randomEntityProperties == null) {
                     randomEntityProperties = makeProperties(resourceLocation, true);
                  }

                  if (randomEntityProperties != null) {
                     mapProperties.put(pathBase, randomEntityProperties);
                  }
               }
            }
         }
      }

      active = !mapProperties.isEmpty();
   }

   public static void dbg(String str) {
      Config.dbg("RandomEntities: " + str);
   }

   public static void warn(String str) {
      Config.warn("RandomEntities: " + str);
   }
}
