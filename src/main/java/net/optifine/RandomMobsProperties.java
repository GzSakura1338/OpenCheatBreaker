package net.optifine;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsProperties {
    public String name = null;
    public String basePath = null;
    public ResourceLocation[] resourceLocations = null;
    public RandomMobsRule[] rules = null;

    public RandomMobsProperties(String str, ResourceLocation[] resourceLocations) {
        ConnectedParser connectedParser = new ConnectedParser("RandomMobs");
        this.name = connectedParser.parseName(str);
        this.basePath = connectedParser.parseBasePath(str);
        this.resourceLocations = resourceLocations;
    }

    public RandomMobsProperties(Properties properties, String str, ResourceLocation resourceLocation) {
        ConnectedParser connectedParser = new ConnectedParser("RandomMobs");
        this.name = connectedParser.parseName(str);
        this.basePath = connectedParser.parseBasePath(str);
        this.rules = this.parseRules(properties, str, resourceLocation, connectedParser);
    }

    public ResourceLocation getTextureLocation(ResourceLocation resourceLocation, EntityLiving entityLiving) {
        if (this.rules != null) {
            for (int i = 0; i < this.rules.length; i++) {
                RandomMobsRule randomMobsRule = this.rules[i];
                if (randomMobsRule.matches(entityLiving)) {
                    return randomMobsRule.getTextureLocation(resourceLocation, entityLiving.randomMobsId);
                }
            }
        }

        if (this.resourceLocations != null) {
            int randomMobsId = entityLiving.randomMobsId;
            int n = randomMobsId % this.resourceLocations.length;
            return this.resourceLocations[n];
        } else {
            return resourceLocation;
        }
    }

    public RandomMobsRule[] parseRules(Properties properties, String str, ResourceLocation resourceLocation, ConnectedParser connectedParser) {
        ArrayList list = new ArrayList();
        int size = properties.size();

        for (int i = 0; i < size; i++) {
            int n = i + 1;
            String property = properties.getProperty("skins." + n);
            if (property != null) {
                int[] parseIntList = connectedParser.parseIntList(property);
                int[] parseIntList2 = connectedParser.parseIntList(properties.getProperty("weights." + n));
                BiomeGenBase[] parseBiomes = connectedParser.parseBiomes(properties.getProperty("biomes." + n));
                RangeListInt parseRangeListInt = connectedParser.parseRangeListInt(properties.getProperty("heights." + n));
                if (parseRangeListInt == null) {
                    parseRangeListInt = this.parseMinMaxHeight(properties, n);
                }

                NbtTagValue parseNbtTagValue = connectedParser.parseNbtTagValue("name", properties.getProperty("name." + n));
                VillagerProfession[] parseProfessions = connectedParser.parseProfessions(properties.getProperty("professions." + n));
                RandomMobsRule randomMobsRule = new RandomMobsRule(resourceLocation, n, parseIntList, parseIntList2, parseBiomes, parseRangeListInt, parseNbtTagValue, parseProfessions);
                if (randomMobsRule.isValid(str)) {
                    list.add(randomMobsRule);
                }
            }
        }

        return (RandomMobsRule[])list.toArray(new RandomMobsRule[list.size()]);
    }

    public RangeListInt parseMinMaxHeight(Properties properties, int n) {
        String property = properties.getProperty("minHeight." + n);
        String property2 = properties.getProperty("maxHeight." + n);
        if (property == null && property2 == null) {
            return null;
        }

        int n2 = 0;
        if (property != null) {
            n2 = Config.parseInt(property, -1);
            if (n2 < 0) {
                Config.warn("Invalid minHeight: " + property);
                return null;
            }
        }

        int n3 = 256;
        if (property2 != null) {
            n3 = Config.parseInt(property2, -1);
            if (n3 < 0) {
                Config.warn("Invalid maxHeight: " + property2);
                return null;
            }
        }

        if (n3 < 0) {
            Config.warn("Invalid minHeight, maxHeight: " + property + ", " + property2);
            return null;
        } else {
            RangeListInt rangeListInt = new RangeListInt();
            rangeListInt.addRange(new RangeInt(n2, n3));
            return rangeListInt;
        }
    }

    public boolean isValid(String str) {
        if (this.resourceLocations == null && this.rules == null) {
            Config.warn("No skins specified: " + str);
            return false;
        }

        if (this.rules != null) {
            for (int i = 0; i < this.rules.length; i++) {
                RandomMobsRule randomMobsRule = this.rules[i];
                if (!randomMobsRule.isValid(str)) {
                    return false;
                }
            }
        }

        if (this.resourceLocations != null) {
            for (int i = 0; i < this.resourceLocations.length; i++) {
                ResourceLocation resourceLocation = this.resourceLocations[i];
                if (!Config.hasResource(resourceLocation)) {
                    Config.warn("Texture not found: " + resourceLocation.getResourcePath());
                    return false;
                }
            }
        }

        return true;
    }
}
