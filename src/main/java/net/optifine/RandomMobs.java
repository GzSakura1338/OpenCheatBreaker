package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class RandomMobs {
    public static Map locationProperties = new HashMap();
    public static RenderGlobal renderGlobal = null;
    public static boolean initialized = false;
    public static Random random = new Random();
    public static boolean working = false;
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_PROPERTIES = ".properties";
    public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
    public static final String PREFIX_MCPATCHER_MOB = "mcpatcher/mob/";
    public static String[] DEPENDANT_SUFFIXES = new String[]{
        "_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"
    };

    public static void entityLoaded(Entity entity, World world) {
        if (entity instanceof EntityLiving && world != null) {
            EntityLiving entityLiving = (EntityLiving)entity;
            entityLiving.spawnPosition = new BlockPos((int)entityLiving.posX, (int)entityLiving.posY, (int)entityLiving.posZ);
            entityLiving.spawnBiome = world.getBiomeGenForCoords((int)entityLiving.posX, (int)entityLiving.posZ);
            WorldServer worldServer = Config.getWorldServer();
            if (worldServer != null) {
                Entity entityByID = worldServer.getEntityByID(entity.getEntityId());
                if (entityByID instanceof EntityLiving) {
                    EntityLiving entityLiving2 = (EntityLiving)entityByID;
                    UUID uniqueID = entityLiving2.getUniqueID();
                    if (entityLiving instanceof EntityVillager && entityByID instanceof EntityVillager) {
                        updateEntityVillager((EntityVillager)entityLiving, (EntityVillager)entityLiving2);
                    }

                    long leastSignificantBits = uniqueID.getLeastSignificantBits();
                    int n = (int)(leastSignificantBits & 2147483647L);
                    entityLiving.randomMobsId = n;
                }
            }
        }
    }

    public static void updateEntityVillager(EntityVillager entityVillager, EntityVillager entityVillager2) {
        int profession = entityVillager2.getProfession();
        entityVillager.setProfession(profession);
    }

    public static void worldChanged(World world, World world2) {
        if (world2 != null) {
            List loadedEntityList = world2.getLoadedEntityList();

            for (int i = 0; i < loadedEntityList.size(); i++) {
                Entity entity = (Entity)loadedEntityList.get(i);
                entityLoaded(entity, world2);
            }
        }
    }

    public static ResourceLocation getTextureLocation(ResourceLocation resourceLocation) {
        if (working) {
            return resourceLocation;
        }

        ResourceLocation resourceLocation2;
        try {
            working = true;
            if (!initialized) {
                initialize();
            }

            if (renderGlobal == null) {
                return resourceLocation;
            }

            Entity renderedEntity = renderGlobal.renderedEntity;
            if (renderedEntity instanceof EntityLiving) {
                EntityLiving entityLiving = (EntityLiving)renderedEntity;
                String resourcePath = resourceLocation.getResourcePath();
                if (!resourcePath.startsWith("textures/entity/")) {
                    return resourceLocation;
                }

                RandomMobsProperties properties = getProperties(resourceLocation);
                if (properties == null) {
                    return resourceLocation;
                }

                return properties.getTextureLocation(resourceLocation, entityLiving);
            }

            resourceLocation2 = resourceLocation;
        } finally {
            working = false;
        }

        return resourceLocation2;
    }

    public static RandomMobsProperties getProperties(ResourceLocation resourceLocation) {
        String resourcePath = resourceLocation.getResourcePath();
        RandomMobsProperties randomMobsProperties = (RandomMobsProperties)locationProperties.get(resourcePath);
        if (randomMobsProperties == null) {
            randomMobsProperties = makeProperties(resourceLocation);
            locationProperties.put(resourcePath, randomMobsProperties);
        }

        return randomMobsProperties;
    }

    public static RandomMobsProperties makeProperties(ResourceLocation resourceLocation) {
        String resourcePath = resourceLocation.getResourcePath();
        ResourceLocation propertyLocation = getPropertyLocation(resourceLocation);
        if (propertyLocation != null) {
            RandomMobsProperties parseProperties2 = parseProperties(propertyLocation, resourceLocation);
            if (parseProperties2 != null) {
                return parseProperties2;
            }
        }

        ResourceLocation[] textureVariants = getTextureVariants(resourceLocation);
        return new RandomMobsProperties(resourcePath, textureVariants);
    }

    public static RandomMobsProperties parseProperties(ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
        try {
            String resourcePath = resourceLocation.getResourcePath();
            Config.dbg("RandomMobs: " + resourceLocation2.getResourcePath() + ", variants: " + resourcePath);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream == null) {
                Config.warn("RandomMobs properties not found: " + resourcePath);
                return null;
            } else {
                Properties properties = new Properties();
                properties.load(resourceStream);
                resourceStream.close();
                RandomMobsProperties randomMobsProperties = new RandomMobsProperties(properties, resourcePath, resourceLocation2);
                return !randomMobsProperties.isValid(resourcePath) ? null : randomMobsProperties;
            }
        } catch (FileNotFoundException e) {
            Config.warn("RandomMobs file not found: " + resourceLocation2.getResourcePath());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResourceLocation getPropertyLocation(ResourceLocation resourceLocation) {
        ResourceLocation mcpatcherLocation = getMcpatcherLocation(resourceLocation);
        if (mcpatcherLocation == null) {
            return null;
        }

        String resourceDomain = mcpatcherLocation.getResourceDomain();
        String resourcePath = mcpatcherLocation.getResourcePath();
        String str = resourcePath;
        if (resourcePath.endsWith(".png")) {
            str = resourcePath.substring(0, resourcePath.length() - ".png".length());
        }

        String str2 = str + ".properties";
        ResourceLocation resourceLocation2 = new ResourceLocation(resourceDomain, str2);
        if (Config.hasResource(resourceLocation2)) {
            return resourceLocation2;
        }

        String parentPath = getParentPath(str);
        if (parentPath == null) {
            return null;
        }

        ResourceLocation resourceLocation3 = new ResourceLocation(resourceDomain, parentPath + ".properties");
        return Config.hasResource(resourceLocation3) ? resourceLocation3 : null;
    }

    public static ResourceLocation getMcpatcherLocation(ResourceLocation resourceLocation) {
        String resourcePath = resourceLocation.getResourcePath();
        if (!resourcePath.startsWith("textures/entity/")) {
            return null;
        }

        String str = "mcpatcher/mob/" + resourcePath.substring("textures/entity/".length());
        return new ResourceLocation(resourceLocation.getResourceDomain(), str);
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

    public static String getParentPath(String str) {
        for (int i = 0; i < DEPENDANT_SUFFIXES.length; i++) {
            String str2 = DEPENDANT_SUFFIXES[i];
            if (str.endsWith(str2)) {
                return str.substring(0, str.length() - str2.length());
            }
        }

        return null;
    }

    public static ResourceLocation[] getTextureVariants(ResourceLocation resourceLocation) {
        ArrayList list = new ArrayList();
        list.add(resourceLocation);
        ResourceLocation mcpatcherLocation = getMcpatcherLocation(resourceLocation);
        if (mcpatcherLocation == null) {
            return null;
        }

        for (int i = 1; i < list.size() + 10; i++) {
            int n = i + 1;
            ResourceLocation locationIndexed = getLocationIndexed(mcpatcherLocation, n);
            if (Config.hasResource(locationIndexed)) {
                list.add(locationIndexed);
            }
        }

        if (list.size() <= 1) {
            return null;
        }

        ResourceLocation[] toArray = (ResourceLocation[])list.toArray(new ResourceLocation[list.size()]);
        Config.dbg("RandomMobs: " + resourceLocation.getResourcePath() + ", variants: " + toArray.length);
        return toArray;
    }

    public static void resetTextures() {
        locationProperties.clear();
        if (Config.isRandomMobs()) {
            initialize();
        }
    }

    public static void initialize() {
        renderGlobal = Config.getRenderGlobal();
        if (renderGlobal != null) {
            initialized = true;
            ArrayList list = new ArrayList();
            list.add("bat");
            list.add("blaze");
            list.add("cat/black");
            list.add("cat/ocelot");
            list.add("cat/red");
            list.add("cat/siamese");
            list.add("chicken");
            list.add("cow/cow");
            list.add("cow/mooshroom");
            list.add("creeper/creeper");
            list.add("enderman/enderman");
            list.add("enderman/enderman_eyes");
            list.add("ghast/ghast");
            list.add("ghast/ghast_shooting");
            list.add("iron_golem");
            list.add("pig/pig");
            list.add("sheep/sheep");
            list.add("sheep/sheep_fur");
            list.add("silverfish");
            list.add("skeleton/skeleton");
            list.add("skeleton/wither_skeleton");
            list.add("slime/slime");
            list.add("slime/magmacube");
            list.add("snowman");
            list.add("spider/cave_spider");
            list.add("spider/spider");
            list.add("spider_eyes");
            list.add("squid");
            list.add("villager/villager");
            list.add("villager/butcher");
            list.add("villager/farmer");
            list.add("villager/librarian");
            list.add("villager/priest");
            list.add("villager/smith");
            list.add("wither/wither");
            list.add("wither/wither_armor");
            list.add("wither/wither_invulnerable");
            list.add("wolf/wolf");
            list.add("wolf/wolf_angry");
            list.add("wolf/wolf_collar");
            list.add("wolf/wolf_tame");
            list.add("zombie_pigman");
            list.add("zombie/zombie");
            list.add("zombie/zombie_villager");

            for (int i = 0; i < list.size(); i++) {
                String str = (String)list.get(i);
                String str2 = "textures/entity/" + str + ".png";
                ResourceLocation resourceLocation = new ResourceLocation(str2);
                if (!Config.hasResource(resourceLocation)) {
                    Config.warn("Not found: " + resourceLocation);
                }

                getProperties(resourceLocation);
            }
        }
    }
}
