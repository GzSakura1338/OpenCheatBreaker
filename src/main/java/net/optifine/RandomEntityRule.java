package net.optifine;

import java.util.Properties;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.config.VillagerProfession;
import net.optifine.config.Weather;
import net.optifine.reflect.Reflector;
import net.optifine.util.ArrayUtils;
import net.optifine.util.MathUtils;

public class RandomEntityRule {
   public String pathProps = null;
   public ResourceLocation baseResLoc = null;
   public int[] textures = null;
   public ResourceLocation[] resourceLocations = null;
   public int[] weights = null;
   public BiomeGenBase[] biomes = null;
   public RangeListInt heights = null;
   public RangeListInt healthRange = null;
   public boolean healthPercent = false;
   public NbtTagValue nbtName = null;
   public int[] sumWeights = null;
   public int sumAllWeights = 1;
   public VillagerProfession[] professions = null;
   public EnumDyeColor[] collarColors = null;
   public Boolean baby = null;
   public RangeListInt moonPhases = null;
   public RangeListInt dayTimes = null;
   public Weather[] weatherList = null;
   public int index;

   public RandomEntityRule(Properties properties, String str, ResourceLocation resourceLocation, int n, String str2, ConnectedParser connectedParser) {
      this.pathProps = str;
      this.baseResLoc = resourceLocation;
      this.index = n;
      this.textures = connectedParser.parseIntList(str2);
      this.weights = connectedParser.parseIntList(properties.getProperty("weights." + n));
      this.biomes = connectedParser.parseBiomes(properties.getProperty("biomes." + n));
      this.heights = connectedParser.parseRangeListInt(properties.getProperty("heights." + n));
      if (this.heights == null) {
         this.heights = this.parseMinMaxHeight(properties, n);
      }

      String property = properties.getProperty("health." + n);
      if (property != null) {
         this.healthPercent = property.contains("%");
         property = property.replace("%", "");
         this.healthRange = connectedParser.parseRangeListInt(property);
      }

      this.nbtName = connectedParser.parseNbtTagValue("name", properties.getProperty("name." + n));
      this.professions = connectedParser.parseProfessions(properties.getProperty("professions." + n));
      this.collarColors = connectedParser.parseDyeColors(properties.getProperty("collarColors." + n), "collar color", ConnectedParser.DYE_COLORS_INVALID);
      this.baby = connectedParser.parseBooleanObject(properties.getProperty("baby." + n));
      this.moonPhases = connectedParser.parseRangeListInt(properties.getProperty("moonPhase." + n));
      this.dayTimes = connectedParser.parseRangeListInt(properties.getProperty("dayTime." + n));
      this.weatherList = connectedParser.parseWeather(properties.getProperty("weather." + n), "weather." + n, (Weather[])null);
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
      if (this.textures == null || this.textures.length == 0) {
         Config.warn("Invalid skins for rule: " + this.index);
         return false;
      }

      if (this.resourceLocations != null) {
         return true;
      }

      this.resourceLocations = new ResourceLocation[this.textures.length];
      boolean startsWith = this.pathProps.startsWith("mcpatcher/mob/");
      ResourceLocation locationRandom = RandomEntities.getLocationRandom(this.baseResLoc, startsWith);
      if (locationRandom == null) {
         Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
         return false;
      }

      for (int i = 0; i < this.resourceLocations.length; i++) {
         int n = this.textures[i];
         if (n <= 1) {
            this.resourceLocations[i] = this.baseResLoc;
         } else {
            ResourceLocation locationIndexed = RandomEntities.getLocationIndexed(locationRandom, n);
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
      } else if (this.collarColors == ConnectedParser.DYE_COLORS_INVALID) {
         Config.warn("Invalid collar colors: " + str);
         return false;
      } else {
         return true;
      }
   }

   public boolean matches(IRandomEntity randomEntity) {
      if (this.biomes != null && !Matches.biome(randomEntity.getSpawnBiome(), this.biomes)) {
         return false;
      }

      if (this.heights != null) {
         BlockPos spawnPosition = randomEntity.getSpawnPosition();
         if (spawnPosition != null && !this.heights.isInRange(spawnPosition.getY())) {
            return false;
         }
      }

      if (this.healthRange != null) {
         int health = randomEntity.getHealth();
         if (this.healthPercent) {
            int maxHealth = randomEntity.getMaxHealth();
            if (maxHealth > 0) {
               health = (int)((double)(health * 100) / maxHealth);
            }
         }

         if (!this.healthRange.isInRange(health)) {
            return false;
         }
      }

      if (this.nbtName != null) {
         String name = randomEntity.getName();
         if (!this.nbtName.matchesValue(name)) {
            return false;
         }
      }

      if (this.professions != null && randomEntity instanceof RandomEntity) {
         RandomEntity randomEntity2 = (RandomEntity)randomEntity;
         Entity entity = randomEntity2.getEntity();
         if (entity instanceof EntityVillager) {
            EntityVillager entityVillager = (EntityVillager)entity;
            int profession = entityVillager.getProfession();
            int fieldValueInt = Reflector.getFieldValueInt(entityVillager, Reflector.EntityVillager_careerId, -1);
            if (profession < 0 || fieldValueInt < 0) {
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
      }

      if (this.collarColors != null && randomEntity instanceof RandomEntity) {
         RandomEntity randomEntity2 = (RandomEntity)randomEntity;
         Entity entity = randomEntity2.getEntity();
         if (entity instanceof EntityWolf) {
            EntityWolf entityWolf = (EntityWolf)entity;
            if (!entityWolf.isTamed()) {
               return false;
            }

            EnumDyeColor collarColor = entityWolf.getCollarColor();
            if (!Config.equalsOne(collarColor, this.collarColors)) {
               return false;
            }
         }
      }

      if (this.baby != null && randomEntity instanceof RandomEntity) {
         RandomEntity randomEntity2 = (RandomEntity)randomEntity;
         Entity entity = randomEntity2.getEntity();
         if (entity instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving)entity;
            if (entityLiving.isChild() != this.baby) {
               return false;
            }
         }
      }

      if (this.moonPhases != null) {
         WorldClient theWorld = Config.getMinecraft().theWorld;
         if (theWorld != null) {
            int moonPhase = theWorld.getMoonPhase();
            if (!this.moonPhases.isInRange(moonPhase)) {
               return false;
            }
         }
      }

      if (this.dayTimes != null) {
         WorldClient theWorld = Config.getMinecraft().theWorld;
         if (theWorld != null) {
            int worldTime = (int)theWorld.getWorldInfo().getWorldTime();
            if (!this.dayTimes.isInRange(worldTime)) {
               return false;
            }
         }
      }

      if (this.weatherList != null) {
         WorldClient theWorld = Config.getMinecraft().theWorld;
         if (theWorld != null) {
            Weather weather = Weather.getWeather(theWorld, 0.0F);
            if (!ArrayUtils.contains(this.weatherList, weather)) {
               return false;
            }
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
