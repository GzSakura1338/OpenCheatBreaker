package net.optifine;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsRule {
    public ResourceLocation baseResLoc = null;
    public int[] skins = null;
    public ResourceLocation[] resourceLocations = null;
    public int[] weights = null;
    public BiomeGenBase[] biomes = null;
    public RangeListInt heights = null;
    public NbtTagValue nbtName = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    public VillagerProfession[] professions = null;
    public int index;

    public RandomMobsRule(
        ResourceLocation resourceLocation, int n, int[] ints, int[] ints2, BiomeGenBase[] biomeGenBases, RangeListInt rangeListInt, NbtTagValue nbtTagValue, VillagerProfession[] villagerProfessions
    ) {
        this.baseResLoc = resourceLocation;
        this.index = n;
        this.skins = ints;
        this.weights = ints2;
        this.biomes = biomeGenBases;
        this.heights = rangeListInt;
        this.nbtName = nbtTagValue;
        this.professions = villagerProfessions;
    }

    public boolean isValid(String str) {
        if (this.skins == null || this.skins.length == 0) {
            Config.warn("Invalid skins for rule: " + this.index);
            return false;
        }

        if (this.resourceLocations != null) {
            return true;
        }

        this.resourceLocations = new ResourceLocation[this.skins.length];
        ResourceLocation mcpatcherLocation = RandomMobs.getMcpatcherLocation(this.baseResLoc);
        if (mcpatcherLocation == null) {
            Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
            return false;
        }

        for (int i = 0; i < this.resourceLocations.length; i++) {
            int n = this.skins[i];
            if (n <= 1) {
                this.resourceLocations[i] = this.baseResLoc;
            } else {
                ResourceLocation locationIndexed = RandomMobs.getLocationIndexed(mcpatcherLocation, n);
                if (locationIndexed == null) {
                    Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
                    return false;
                }

                if (!Config.hasResource(locationIndexed)) {
                    Config.warn("Texture not found: " + locationIndexed.getResourcePath());
                    return false;
                }

                this.resourceLocations[i] = locationIndexed;
            }
        }

        if (this.weights != null) {
            if (this.weights.length > this.resourceLocations.length) {
                Config.warn("More weights defined than skins, trimming weights: " + str);
                int[] ints = new int[this.resourceLocations.length];
                System.arraycopy(this.weights, 0, ints, 0, ints.length);
                this.weights = ints;
            }

            if (this.weights.length < this.resourceLocations.length) {
                Config.warn("Less weights defined than skins, expanding weights: " + str);
                int[] ints = new int[this.resourceLocations.length];
                System.arraycopy(this.weights, 0, ints, 0, this.weights.length);
                int average = MathUtils.getAverage(this.weights);

                for (int length = this.weights.length; length < ints.length; length++) {
                    ints[length] = average;
                }

                this.weights = ints;
            }

            this.sumWeights = new int[this.weights.length];
            int n = 0;

            for (int i = 0; i < this.weights.length; i++) {
                if (this.weights[i] < 0) {
                    Config.warn("Invalid weight: " + this.weights[i]);
                    return false;
                }

                n += this.weights[i];
                this.sumWeights[i] = n;
            }

            this.sumAllWeights = n;
            if (this.sumAllWeights <= 0) {
                Config.warn("Invalid sum of all weights: " + n);
                this.sumAllWeights = 1;
            }
        }

        if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
            Config.warn("Invalid professions or careers: " + str);
            return false;
        } else {
            return true;
        }
    }

    public boolean matches(EntityLiving entityLiving) {
        if (this.biomes != null) {
            BiomeGenBase spawnBiome = entityLiving.spawnBiome;
            boolean flag = false;

            for (int i = 0; i < this.biomes.length; i++) {
                BiomeGenBase biomeGenBase = this.biomes[i];
                if (biomeGenBase == spawnBiome) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            }
        }

        if (this.heights != null && entityLiving.spawnPosition != null) {
            return this.heights.isInRange(entityLiving.spawnPosition.getY());
        }

        if (this.nbtName != null) {
            String str = entityLiving.hasCustomNameTag() ? entityLiving.getCustomNameTag() : null;
            if (!this.nbtName.matchesValue(str)) {
                return false;
            }
        }

        if (this.professions != null && entityLiving instanceof EntityVillager) {
            EntityVillager entityVillager = (EntityVillager)entityLiving;
            int profession = entityVillager.getProfession();
            byte b = 0;
            if (profession < 0 || b < 0) {
                return false;
            }

            boolean flag = false;

            for (int i = 0; i < this.professions.length; i++) {
                VillagerProfession villagerProfession = this.professions[i];
                if (villagerProfession.matches(profession, b)) {
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

    public ResourceLocation getTextureLocation(ResourceLocation resourceLocation, int n) {
        if (this.resourceLocations != null && this.resourceLocations.length != 0) {
            int n2 = 0;
            if (this.weights == null) {
                n2 = n % this.resourceLocations.length;
            } else {
                int n3 = n % this.sumAllWeights;

                for (int i = 0; i < this.sumWeights.length; i++) {
                    if (this.sumWeights[i] > n3) {
                        n2 = i;
                        break;
                    }
                }
            }

            return this.resourceLocations[n2];
        } else {
            return resourceLocation;
        }
    }
}
