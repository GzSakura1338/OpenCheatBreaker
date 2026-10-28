package net.optifine;

import java.util.List;
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
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class DynamicLights {
    public static DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
    public static long timeUpdateMs = 0L;
    public static final double MAX_DIST = 7.5;
    public static final double MAX_DIST_SQ = 56.25;
    public static final int LIGHT_LEVEL_BLAZE = 10;
    public static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
    public static ReflectorField ItemBlock_block = Reflector.getReflectorField(ItemBlock.class, Block.class);
    public static final int CONSTANT_15 = 15;
    public static final int CONSTANT_8 = 8;
    public static final int CONSTANT_8_2 = 8;
    public static final int CONSTANT_15_2 = 15;
    public static final int CONSTANT_8_3 = 8;

    public static void entityAdded(Entity entity, RenderGlobal renderGlobal) {
    }

    public static void entityRemoved(Entity entity, RenderGlobal renderGlobal) {
        DynamicLightsMap mapDynamicLights2 = mapDynamicLights;
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
            DynamicLightsMap mapDynamicLights2 = mapDynamicLights;
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

    public static void updateMapDynamicLights(RenderGlobal renderGlobal) {
        WorldClient theWorld = renderGlobal.theWorld;
        if (theWorld != null) {
            for (Entity entity : (Iterable<Entity>)theWorld.getLoadedEntityList()) {
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

    public static int getCombinedLight(int n, int n2, int n3, int n4) {
        double lightLevel = getLightLevel(n, n2, n3);
        return getCombinedLight(lightLevel, n4);
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

    public static double getLightLevel(int n, int n2, int n3) {
        double d = 0.0;
        DynamicLightsMap mapDynamicLights2 = mapDynamicLights;
        synchronized (mapDynamicLights) {
            List valueList = mapDynamicLights.valueList();

            for (int i = 0; i < valueList.size(); i++) {
                DynamicLight dynamicLight = (DynamicLight)valueList.get(i);
                int lastLightLevel = dynamicLight.getLastLightLevel();
                if (lastLightLevel > 0) {
                    double lastPosX = dynamicLight.getLastPosX();
                    double lastPosY = dynamicLight.getLastPosY();
                    double lastPosZ = dynamicLight.getLastPosZ();
                    double d2 = n - lastPosX;
                    double d3 = n2 - lastPosY;
                    double d4 = n3 - lastPosZ;
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
            Block fieldValue = (Block)Reflector.getFieldValue(itemBlock, ItemBlock_block);
            if (fieldValue != null) {
                return fieldValue.getLightValue();
            }
        }

        return item == Items.lava_bucket
            ? Blocks.lava.getLightValue()
            : (
                item == Items.blaze_rod || item == Items.blaze_powder
                    ? 10
                    : (item == Items.glowstone_dust ? 8 : (item == Items.magma_cream ? 8 : (item == Items.nether_star ? Blocks.beacon.getLightValue() / 2 : 0)))
            );
    }

    public static int getLightLevel(Entity entity) {
        if (entity == Config.getMinecraft().renderViewEntity && !Config.isDynamicHandLight()) {
            return 0;
        }

        if (entity.isBurning()) {
            return 15;
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
            if (entityCreeper.getCreeperState() > 0) {
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
        DynamicLightsMap mapDynamicLights2 = mapDynamicLights;
        synchronized (mapDynamicLights) {
            List valueList = mapDynamicLights.valueList();

            for (int i = 0; i < valueList.size(); i++) {
                DynamicLight dynamicLight = (DynamicLight)valueList.get(i);
                dynamicLight.updateLitChunks(renderGlobal);
            }

            valueList.clear();
        }
    }

    public static void clear() {
        DynamicLightsMap mapDynamicLights2 = mapDynamicLights;
        synchronized (mapDynamicLights) {
            mapDynamicLights.clear();
        }
    }

    public static int getCount() {
        DynamicLightsMap mapDynamicLights2 = mapDynamicLights;
        synchronized (mapDynamicLights) {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(EntityItem entityItem) {
        return entityItem.getDataWatcher().getWatchableObjectItemStack(10);
    }
}
