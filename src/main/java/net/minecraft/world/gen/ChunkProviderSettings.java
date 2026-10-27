package net.minecraft.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkProviderSettings {
   public float coordinateScale;
   public float heightScale;
   public float upperLimitScale;
   public float lowerLimitScale;
   public float depthNoiseScaleX;
   public float depthNoiseScaleZ;
   public float depthNoiseScaleExponent;
   public float mainNoiseScaleX;
   public float mainNoiseScaleY;
   public float mainNoiseScaleZ;
   public float baseSize;
   public float stretchY;
   public float biomeDepthWeight;
   public float biomeDepthOffSet;
   public float biomeScaleWeight;
   public float biomeScaleOffset;
   public int seaLevel;
   public boolean useCaves;
   public boolean useDungeons;
   public int dungeonChance;
   public boolean useStrongholds;
   public boolean useVillages;
   public boolean useMineShafts;
   public boolean useTemples;
   public boolean useMonuments;
   public boolean useRavines;
   public boolean useWaterLakes;
   public int waterLakeChance;
   public boolean useLavaLakes;
   public int lavaLakeChance;
   public boolean useLavaOceans;
   public int fixedBiome;
   public int biomeSize;
   public int riverSize;
   public int dirtSize;
   public int dirtCount;
   public int dirtMinHeight;
   public int dirtMaxHeight;
   public int gravelSize;
   public int gravelCount;
   public int gravelMinHeight;
   public int gravelMaxHeight;
   public int graniteSize;
   public int graniteCount;
   public int graniteMinHeight;
   public int graniteMaxHeight;
   public int dioriteSize;
   public int dioriteCount;
   public int dioriteMinHeight;
   public int dioriteMaxHeight;
   public int andesiteSize;
   public int andesiteCount;
   public int andesiteMinHeight;
   public int andesiteMaxHeight;
   public int coalSize;
   public int coalCount;
   public int coalMinHeight;
   public int coalMaxHeight;
   public int ironSize;
   public int ironCount;
   public int ironMinHeight;
   public int ironMaxHeight;
   public int goldSize;
   public int goldCount;
   public int goldMinHeight;
   public int goldMaxHeight;
   public int redstoneSize;
   public int redstoneCount;
   public int redstoneMinHeight;
   public int redstoneMaxHeight;
   public int diamondSize;
   public int diamondCount;
   public int diamondMinHeight;
   public int diamondMaxHeight;
   public int lapisSize;
   public int lapisCount;
   public int lapisCenterHeight;
   public int lapisSpread;

   public ChunkProviderSettings(ChunkProviderSettings.Factory settingsFactory) {
      this.coordinateScale = settingsFactory.coordinateScale;
      this.heightScale = settingsFactory.heightScale;
      this.upperLimitScale = settingsFactory.upperLimitScale;
      this.lowerLimitScale = settingsFactory.lowerLimitScale;
      this.depthNoiseScaleX = settingsFactory.depthNoiseScaleX;
      this.depthNoiseScaleZ = settingsFactory.depthNoiseScaleZ;
      this.depthNoiseScaleExponent = settingsFactory.depthNoiseScaleExponent;
      this.mainNoiseScaleX = settingsFactory.mainNoiseScaleX;
      this.mainNoiseScaleY = settingsFactory.mainNoiseScaleY;
      this.mainNoiseScaleZ = settingsFactory.mainNoiseScaleZ;
      this.baseSize = settingsFactory.baseSize;
      this.stretchY = settingsFactory.stretchY;
      this.biomeDepthWeight = settingsFactory.biomeDepthWeight;
      this.biomeDepthOffSet = settingsFactory.biomeDepthOffset;
      this.biomeScaleWeight = settingsFactory.biomeScaleWeight;
      this.biomeScaleOffset = settingsFactory.biomeScaleOffset;
      this.seaLevel = settingsFactory.seaLevel;
      this.useCaves = settingsFactory.useCaves;
      this.useDungeons = settingsFactory.useDungeons;
      this.dungeonChance = settingsFactory.dungeonChance;
      this.useStrongholds = settingsFactory.useStrongholds;
      this.useVillages = settingsFactory.useVillages;
      this.useMineShafts = settingsFactory.useMineShafts;
      this.useTemples = settingsFactory.useTemples;
      this.useMonuments = settingsFactory.useMonuments;
      this.useRavines = settingsFactory.useRavines;
      this.useWaterLakes = settingsFactory.useWaterLakes;
      this.waterLakeChance = settingsFactory.waterLakeChance;
      this.useLavaLakes = settingsFactory.useLavaLakes;
      this.lavaLakeChance = settingsFactory.lavaLakeChance;
      this.useLavaOceans = settingsFactory.useLavaOceans;
      this.fixedBiome = settingsFactory.fixedBiome;
      this.biomeSize = settingsFactory.biomeSize;
      this.riverSize = settingsFactory.riverSize;
      this.dirtSize = settingsFactory.dirtSize;
      this.dirtCount = settingsFactory.dirtCount;
      this.dirtMinHeight = settingsFactory.dirtMinHeight;
      this.dirtMaxHeight = settingsFactory.dirtMaxHeight;
      this.gravelSize = settingsFactory.gravelSize;
      this.gravelCount = settingsFactory.gravelCount;
      this.gravelMinHeight = settingsFactory.gravelMinHeight;
      this.gravelMaxHeight = settingsFactory.gravelMaxHeight;
      this.graniteSize = settingsFactory.graniteSize;
      this.graniteCount = settingsFactory.graniteCount;
      this.graniteMinHeight = settingsFactory.graniteMinHeight;
      this.graniteMaxHeight = settingsFactory.graniteMaxHeight;
      this.dioriteSize = settingsFactory.dioriteSize;
      this.dioriteCount = settingsFactory.dioriteCount;
      this.dioriteMinHeight = settingsFactory.dioriteMinHeight;
      this.dioriteMaxHeight = settingsFactory.dioriteMaxHeight;
      this.andesiteSize = settingsFactory.andesiteSize;
      this.andesiteCount = settingsFactory.andesiteCount;
      this.andesiteMinHeight = settingsFactory.andesiteMinHeight;
      this.andesiteMaxHeight = settingsFactory.andesiteMaxHeight;
      this.coalSize = settingsFactory.coalSize;
      this.coalCount = settingsFactory.coalCount;
      this.coalMinHeight = settingsFactory.coalMinHeight;
      this.coalMaxHeight = settingsFactory.coalMaxHeight;
      this.ironSize = settingsFactory.ironSize;
      this.ironCount = settingsFactory.ironCount;
      this.ironMinHeight = settingsFactory.ironMinHeight;
      this.ironMaxHeight = settingsFactory.ironMaxHeight;
      this.goldSize = settingsFactory.goldSize;
      this.goldCount = settingsFactory.goldCount;
      this.goldMinHeight = settingsFactory.goldMinHeight;
      this.goldMaxHeight = settingsFactory.goldMaxHeight;
      this.redstoneSize = settingsFactory.redstoneSize;
      this.redstoneCount = settingsFactory.redstoneCount;
      this.redstoneMinHeight = settingsFactory.redstoneMinHeight;
      this.redstoneMaxHeight = settingsFactory.redstoneMaxHeight;
      this.diamondSize = settingsFactory.diamondSize;
      this.diamondCount = settingsFactory.diamondCount;
      this.diamondMinHeight = settingsFactory.diamondMinHeight;
      this.diamondMaxHeight = settingsFactory.diamondMaxHeight;
      this.lapisSize = settingsFactory.lapisSize;
      this.lapisCount = settingsFactory.lapisCount;
      this.lapisCenterHeight = settingsFactory.lapisCenterHeight;
      this.lapisSpread = settingsFactory.lapisSpread;
   }

   public static class Factory {
      public static Gson JSON_ADAPTER = new GsonBuilder()
         .registerTypeAdapter(ChunkProviderSettings.Factory.class, new ChunkProviderSettings.Serializer())
         .create();
      public float coordinateScale = 684.412F;
      public float heightScale = 684.412F;
      public float upperLimitScale = 512.0F;
      public float lowerLimitScale = 512.0F;
      public float depthNoiseScaleX = 200.0F;
      public float depthNoiseScaleZ = 200.0F;
      public float depthNoiseScaleExponent = 0.5F;
      public float mainNoiseScaleX = 80.0F;
      public float mainNoiseScaleY = 160.0F;
      public float mainNoiseScaleZ = 80.0F;
      public float baseSize = 8.5F;
      public float stretchY = 12.0F;
      public float biomeDepthWeight = 1.0F;
      public float biomeDepthOffset = 0.0F;
      public float biomeScaleWeight = 1.0F;
      public float biomeScaleOffset = 0.0F;
      public int seaLevel = 63;
      public boolean useCaves = true;
      public boolean useDungeons = true;
      public int dungeonChance = 8;
      public boolean useStrongholds = true;
      public boolean useVillages = true;
      public boolean useMineShafts = true;
      public boolean useTemples = true;
      public boolean useMonuments = true;
      public boolean useRavines = true;
      public boolean useWaterLakes = true;
      public int waterLakeChance = 4;
      public boolean useLavaLakes = true;
      public int lavaLakeChance = 80;
      public boolean useLavaOceans = false;
      public int fixedBiome = -1;
      public int biomeSize = 4;
      public int riverSize = 4;
      public int dirtSize = 33;
      public int dirtCount = 10;
      public int dirtMinHeight = 0;
      public int dirtMaxHeight = 256;
      public int gravelSize = 33;
      public int gravelCount = 8;
      public int gravelMinHeight = 0;
      public int gravelMaxHeight = 256;
      public int graniteSize = 33;
      public int graniteCount = 10;
      public int graniteMinHeight = 0;
      public int graniteMaxHeight = 80;
      public int dioriteSize = 33;
      public int dioriteCount = 10;
      public int dioriteMinHeight = 0;
      public int dioriteMaxHeight = 80;
      public int andesiteSize = 33;
      public int andesiteCount = 10;
      public int andesiteMinHeight = 0;
      public int andesiteMaxHeight = 80;
      public int coalSize = 17;
      public int coalCount = 20;
      public int coalMinHeight = 0;
      public int coalMaxHeight = 128;
      public int ironSize = 9;
      public int ironCount = 20;
      public int ironMinHeight = 0;
      public int ironMaxHeight = 64;
      public int goldSize = 9;
      public int goldCount = 2;
      public int goldMinHeight = 0;
      public int goldMaxHeight = 32;
      public int redstoneSize = 8;
      public int redstoneCount = 8;
      public int redstoneMinHeight = 0;
      public int redstoneMaxHeight = 16;
      public int diamondSize = 8;
      public int diamondCount = 1;
      public int diamondMinHeight = 0;
      public int diamondMaxHeight = 16;
      public int lapisSize = 7;
      public int lapisCount = 1;
      public int lapisCenterHeight = 16;
      public int lapisSpread = 16;

      public static ChunkProviderSettings.Factory jsonToFactory(String str) {
         if (str.length() == 0) {
            return new ChunkProviderSettings.Factory();
         }

         try {
            return (ChunkProviderSettings.Factory)JSON_ADAPTER.fromJson(str, ChunkProviderSettings.Factory.class);
         } catch (Exception e) {
            return new ChunkProviderSettings.Factory();
         }
      }

      @Override
      public String toString() {
         return JSON_ADAPTER.toJson(this);
      }

      public Factory() {
         this.resetToDefault();
      }

      public void resetToDefault() {
         this.coordinateScale = 684.412F;
         this.heightScale = 684.412F;
         this.upperLimitScale = 512.0F;
         this.lowerLimitScale = 512.0F;
         this.depthNoiseScaleX = 200.0F;
         this.depthNoiseScaleZ = 200.0F;
         this.depthNoiseScaleExponent = 0.5F;
         this.mainNoiseScaleX = 80.0F;
         this.mainNoiseScaleY = 160.0F;
         this.mainNoiseScaleZ = 80.0F;
         this.baseSize = 8.5F;
         this.stretchY = 12.0F;
         this.biomeDepthWeight = 1.0F;
         this.biomeDepthOffset = 0.0F;
         this.biomeScaleWeight = 1.0F;
         this.biomeScaleOffset = 0.0F;
         this.seaLevel = 63;
         this.useCaves = true;
         this.useDungeons = true;
         this.dungeonChance = 8;
         this.useStrongholds = true;
         this.useVillages = true;
         this.useMineShafts = true;
         this.useTemples = true;
         this.useMonuments = true;
         this.useRavines = true;
         this.useWaterLakes = true;
         this.waterLakeChance = 4;
         this.useLavaLakes = true;
         this.lavaLakeChance = 80;
         this.useLavaOceans = false;
         this.fixedBiome = -1;
         this.biomeSize = 4;
         this.riverSize = 4;
         this.dirtSize = 33;
         this.dirtCount = 10;
         this.dirtMinHeight = 0;
         this.dirtMaxHeight = 256;
         this.gravelSize = 33;
         this.gravelCount = 8;
         this.gravelMinHeight = 0;
         this.gravelMaxHeight = 256;
         this.graniteSize = 33;
         this.graniteCount = 10;
         this.graniteMinHeight = 0;
         this.graniteMaxHeight = 80;
         this.dioriteSize = 33;
         this.dioriteCount = 10;
         this.dioriteMinHeight = 0;
         this.dioriteMaxHeight = 80;
         this.andesiteSize = 33;
         this.andesiteCount = 10;
         this.andesiteMinHeight = 0;
         this.andesiteMaxHeight = 80;
         this.coalSize = 17;
         this.coalCount = 20;
         this.coalMinHeight = 0;
         this.coalMaxHeight = 128;
         this.ironSize = 9;
         this.ironCount = 20;
         this.ironMinHeight = 0;
         this.ironMaxHeight = 64;
         this.goldSize = 9;
         this.goldCount = 2;
         this.goldMinHeight = 0;
         this.goldMaxHeight = 32;
         this.redstoneSize = 8;
         this.redstoneCount = 8;
         this.redstoneMinHeight = 0;
         this.redstoneMaxHeight = 16;
         this.diamondSize = 8;
         this.diamondCount = 1;
         this.diamondMinHeight = 0;
         this.diamondMaxHeight = 16;
         this.lapisSize = 7;
         this.lapisCount = 1;
         this.lapisCenterHeight = 16;
         this.lapisSpread = 16;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            ChunkProviderSettings.Factory factory = (ChunkProviderSettings.Factory)obj;
            return this.andesiteCount != factory.andesiteCount
               ? false
               : (
                  this.andesiteMaxHeight != factory.andesiteMaxHeight
                     ? false
                     : (
                        this.andesiteMinHeight != factory.andesiteMinHeight
                           ? false
                           : (
                              this.andesiteSize != factory.andesiteSize
                                 ? false
                                 : (
                                    Float.compare(factory.baseSize, this.baseSize) != 0
                                       ? false
                                       : (
                                          Float.compare(factory.biomeDepthOffset, this.biomeDepthOffset) != 0
                                             ? false
                                             : (
                                                Float.compare(factory.biomeDepthWeight, this.biomeDepthWeight) != 0
                                                   ? false
                                                   : (
                                                      Float.compare(factory.biomeScaleOffset, this.biomeScaleOffset) != 0
                                                         ? false
                                                         : (
                                                            Float.compare(factory.biomeScaleWeight, this.biomeScaleWeight) != 0
                                                               ? false
                                                               : (
                                                                  this.biomeSize != factory.biomeSize
                                                                     ? false
                                                                     : (
                                                                        this.coalCount != factory.coalCount
                                                                           ? false
                                                                           : (
                                                                              this.coalMaxHeight != factory.coalMaxHeight
                                                                                 ? false
                                                                                 : (
                                                                                    this.coalMinHeight != factory.coalMinHeight
                                                                                       ? false
                                                                                       : (
                                                                                          this.coalSize != factory.coalSize
                                                                                             ? false
                                                                                             : (
                                                                                                Float.compare(factory.coordinateScale, this.coordinateScale) != 0
                                                                                                   ? false
                                                                                                   : (
                                                                                                      Float.compare(
                                                                                                               factory.depthNoiseScaleExponent,
                                                                                                               this.depthNoiseScaleExponent
                                                                                                            )
                                                                                                            != 0
                                                                                                         ? false
                                                                                                         : (
                                                                                                            Float.compare(
                                                                                                                     factory.depthNoiseScaleX,
                                                                                                                     this.depthNoiseScaleX
                                                                                                                  )
                                                                                                                  != 0
                                                                                                               ? false
                                                                                                               : (
                                                                                                                  Float.compare(
                                                                                                                           factory.depthNoiseScaleZ,
                                                                                                                           this.depthNoiseScaleZ
                                                                                                                        )
                                                                                                                        != 0
                                                                                                                     ? false
                                                                                                                     : (
                                                                                                                        this.diamondCount != factory.diamondCount
                                                                                                                           ? false
                                                                                                                           : (
                                                                                                                              this.diamondMaxHeight
                                                                                                                                    != factory.diamondMaxHeight
                                                                                                                                 ? false
                                                                                                                                 : (
                                                                                                                                    this.diamondMinHeight
                                                                                                                                          != factory.diamondMinHeight
                                                                                                                                       ? false
                                                                                                                                       : (
                                                                                                                                          this.diamondSize
                                                                                                                                                != factory.diamondSize
                                                                                                                                             ? false
                                                                                                                                             : (
                                                                                                                                                this.dioriteCount
                                                                                                                                                      != factory.dioriteCount
                                                                                                                                                   ? false
                                                                                                                                                   : (
                                                                                                                                                      this.dioriteMaxHeight
                                                                                                                                                            != factory.dioriteMaxHeight
                                                                                                                                                         ? false
                                                                                                                                                         : (
                                                                                                                                                            this.dioriteMinHeight
                                                                                                                                                                  != factory.dioriteMinHeight
                                                                                                                                                               ? false
                                                                                                                                                               : (
                                                                                                                                                                  this.dioriteSize
                                                                                                                                                                        != factory.dioriteSize
                                                                                                                                                                     ? false
                                                                                                                                                                     : (
                                                                                                                                                                        this.dirtCount
                                                                                                                                                                              != factory.dirtCount
                                                                                                                                                                           ? false
                                                                                                                                                                           : (
                                                                                                                                                                              this.dirtMaxHeight
                                                                                                                                                                                    != factory.dirtMaxHeight
                                                                                                                                                                                 ? false
                                                                                                                                                                                 : (
                                                                                                                                                                                    this.dirtMinHeight
                                                                                                                                                                                          != factory.dirtMinHeight
                                                                                                                                                                                       ? false
                                                                                                                                                                                       : (
                                                                                                                                                                                          this.dirtSize
                                                                                                                                                                                                != factory.dirtSize
                                                                                                                                                                                             ? false
                                                                                                                                                                                             : (
                                                                                                                                                                                                this.dungeonChance
                                                                                                                                                                                                      != factory.dungeonChance
                                                                                                                                                                                                   ? false
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      this.fixedBiome
                                                                                                                                                                                                            != factory.fixedBiome
                                                                                                                                                                                                         ? false
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            this.goldCount
                                                                                                                                                                                                                  != factory.goldCount
                                                                                                                                                                                                               ? false
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  this.goldMaxHeight
                                                                                                                                                                                                                        != factory.goldMaxHeight
                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        this.goldMinHeight
                                                                                                                                                                                                                              != factory.goldMinHeight
                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              this.goldSize
                                                                                                                                                                                                                                    != factory.goldSize
                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                    this.graniteCount
                                                                                                                                                                                                                                          != factory.graniteCount
                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                          this.graniteMaxHeight
                                                                                                                                                                                                                                                != factory.graniteMaxHeight
                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                this.graniteMinHeight
                                                                                                                                                                                                                                                      != factory.graniteMinHeight
                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                      this.graniteSize
                                                                                                                                                                                                                                                            != factory.graniteSize
                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                            this.gravelCount
                                                                                                                                                                                                                                                                  != factory.gravelCount
                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                  this.gravelMaxHeight
                                                                                                                                                                                                                                                                        != factory.gravelMaxHeight
                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                        this.gravelMinHeight
                                                                                                                                                                                                                                                                              != factory.gravelMinHeight
                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                              this.gravelSize
                                                                                                                                                                                                                                                                                    != factory.gravelSize
                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                    Float.compare(
                                                                                                                                                                                                                                                                                             factory.heightScale,
                                                                                                                                                                                                                                                                                             this.heightScale
                                                                                                                                                                                                                                                                                          )
                                                                                                                                                                                                                                                                                          != 0
                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                          this.ironCount
                                                                                                                                                                                                                                                                                                != factory.ironCount
                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                this.ironMaxHeight
                                                                                                                                                                                                                                                                                                      != factory.ironMaxHeight
                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                      this.ironMinHeight
                                                                                                                                                                                                                                                                                                            != factory.ironMinHeight
                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                            this.ironSize
                                                                                                                                                                                                                                                                                                                  != factory.ironSize
                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                  this.lapisCenterHeight
                                                                                                                                                                                                                                                                                                                        != factory.lapisCenterHeight
                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                        this.lapisCount
                                                                                                                                                                                                                                                                                                                              != factory.lapisCount
                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                              this.lapisSize
                                                                                                                                                                                                                                                                                                                                    != factory.lapisSize
                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                    this.lapisSpread
                                                                                                                                                                                                                                                                                                                                          != factory.lapisSpread
                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                          this.lavaLakeChance
                                                                                                                                                                                                                                                                                                                                                != factory.lavaLakeChance
                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                Float.compare(
                                                                                                                                                                                                                                                                                                                                                         factory.lowerLimitScale,
                                                                                                                                                                                                                                                                                                                                                         this.lowerLimitScale
                                                                                                                                                                                                                                                                                                                                                      )
                                                                                                                                                                                                                                                                                                                                                      != 0
                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                      Float.compare(
                                                                                                                                                                                                                                                                                                                                                               factory.mainNoiseScaleX,
                                                                                                                                                                                                                                                                                                                                                               this.mainNoiseScaleX
                                                                                                                                                                                                                                                                                                                                                            )
                                                                                                                                                                                                                                                                                                                                                            != 0
                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                            Float.compare(
                                                                                                                                                                                                                                                                                                                                                                     factory.mainNoiseScaleY,
                                                                                                                                                                                                                                                                                                                                                                     this.mainNoiseScaleY
                                                                                                                                                                                                                                                                                                                                                                  )
                                                                                                                                                                                                                                                                                                                                                                  != 0
                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                  Float.compare(
                                                                                                                                                                                                                                                                                                                                                                           factory.mainNoiseScaleZ,
                                                                                                                                                                                                                                                                                                                                                                           this.mainNoiseScaleZ
                                                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                                                        != 0
                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                        this.redstoneCount
                                                                                                                                                                                                                                                                                                                                                                              != factory.redstoneCount
                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                              this.redstoneMaxHeight
                                                                                                                                                                                                                                                                                                                                                                                    != factory.redstoneMaxHeight
                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                    this.redstoneMinHeight
                                                                                                                                                                                                                                                                                                                                                                                          != factory.redstoneMinHeight
                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                                                                          this.redstoneSize
                                                                                                                                                                                                                                                                                                                                                                                                != factory.redstoneSize
                                                                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                                                                this.riverSize
                                                                                                                                                                                                                                                                                                                                                                                                      != factory.riverSize
                                                                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                                                                      this.seaLevel
                                                                                                                                                                                                                                                                                                                                                                                                            != factory.seaLevel
                                                                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                                                                            Float.compare(
                                                                                                                                                                                                                                                                                                                                                                                                                     factory.stretchY,
                                                                                                                                                                                                                                                                                                                                                                                                                     this.stretchY
                                                                                                                                                                                                                                                                                                                                                                                                                  )
                                                                                                                                                                                                                                                                                                                                                                                                                  != 0
                                                                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                                                                  Float.compare(
                                                                                                                                                                                                                                                                                                                                                                                                                           factory.upperLimitScale,
                                                                                                                                                                                                                                                                                                                                                                                                                           this.upperLimitScale
                                                                                                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                                                                                                        != 0
                                                                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                                                                        this.useCaves
                                                                                                                                                                                                                                                                                                                                                                                                                              != factory.useCaves
                                                                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                                                                              this.useDungeons
                                                                                                                                                                                                                                                                                                                                                                                                                                    != factory.useDungeons
                                                                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                                                                    this.useLavaLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                          != factory.useLavaLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                                                                                                                          this.useLavaOceans
                                                                                                                                                                                                                                                                                                                                                                                                                                                != factory.useLavaOceans
                                                                                                                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                this.useMineShafts
                                                                                                                                                                                                                                                                                                                                                                                                                                                      != factory.useMineShafts
                                                                                                                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                      this.useRavines
                                                                                                                                                                                                                                                                                                                                                                                                                                                            != factory.useRavines
                                                                                                                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                            this.useStrongholds
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  != factory.useStrongholds
                                                                                                                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  this.useTemples
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        != factory.useTemples
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        this.useMonuments
                                                                                                                                                                                                                                                                                                                                                                                                                                                                              != factory.useMonuments
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                              this.useVillages
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    != factory.useVillages
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    this.useWaterLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          != factory.useWaterLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       : this.waterLakeChance
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          == factory.waterLakeChance
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                     )
                                                                                                                                                                                                               )
                                                                                                                                                                                                         )
                                                                                                                                                                                                   )
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               );
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int n = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
         n = 31 * n + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
         n = 31 * n + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
         n = 31 * n + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
         n = 31 * n + (this.depthNoiseScaleX != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleX) : 0);
         n = 31 * n + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
         n = 31 * n + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
         n = 31 * n + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
         n = 31 * n + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
         n = 31 * n + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
         n = 31 * n + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
         n = 31 * n + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
         n = 31 * n + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
         n = 31 * n + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
         n = 31 * n + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
         n = 31 * n + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
         n = 31 * n + this.seaLevel;
         n = 31 * n + (this.useCaves ? 1 : 0);
         n = 31 * n + (this.useDungeons ? 1 : 0);
         n = 31 * n + this.dungeonChance;
         n = 31 * n + (this.useStrongholds ? 1 : 0);
         n = 31 * n + (this.useVillages ? 1 : 0);
         n = 31 * n + (this.useMineShafts ? 1 : 0);
         n = 31 * n + (this.useTemples ? 1 : 0);
         n = 31 * n + (this.useMonuments ? 1 : 0);
         n = 31 * n + (this.useRavines ? 1 : 0);
         n = 31 * n + (this.useWaterLakes ? 1 : 0);
         n = 31 * n + this.waterLakeChance;
         n = 31 * n + (this.useLavaLakes ? 1 : 0);
         n = 31 * n + this.lavaLakeChance;
         n = 31 * n + (this.useLavaOceans ? 1 : 0);
         n = 31 * n + this.fixedBiome;
         n = 31 * n + this.biomeSize;
         n = 31 * n + this.riverSize;
         n = 31 * n + this.dirtSize;
         n = 31 * n + this.dirtCount;
         n = 31 * n + this.dirtMinHeight;
         n = 31 * n + this.dirtMaxHeight;
         n = 31 * n + this.gravelSize;
         n = 31 * n + this.gravelCount;
         n = 31 * n + this.gravelMinHeight;
         n = 31 * n + this.gravelMaxHeight;
         n = 31 * n + this.graniteSize;
         n = 31 * n + this.graniteCount;
         n = 31 * n + this.graniteMinHeight;
         n = 31 * n + this.graniteMaxHeight;
         n = 31 * n + this.dioriteSize;
         n = 31 * n + this.dioriteCount;
         n = 31 * n + this.dioriteMinHeight;
         n = 31 * n + this.dioriteMaxHeight;
         n = 31 * n + this.andesiteSize;
         n = 31 * n + this.andesiteCount;
         n = 31 * n + this.andesiteMinHeight;
         n = 31 * n + this.andesiteMaxHeight;
         n = 31 * n + this.coalSize;
         n = 31 * n + this.coalCount;
         n = 31 * n + this.coalMinHeight;
         n = 31 * n + this.coalMaxHeight;
         n = 31 * n + this.ironSize;
         n = 31 * n + this.ironCount;
         n = 31 * n + this.ironMinHeight;
         n = 31 * n + this.ironMaxHeight;
         n = 31 * n + this.goldSize;
         n = 31 * n + this.goldCount;
         n = 31 * n + this.goldMinHeight;
         n = 31 * n + this.goldMaxHeight;
         n = 31 * n + this.redstoneSize;
         n = 31 * n + this.redstoneCount;
         n = 31 * n + this.redstoneMinHeight;
         n = 31 * n + this.redstoneMaxHeight;
         n = 31 * n + this.diamondSize;
         n = 31 * n + this.diamondCount;
         n = 31 * n + this.diamondMinHeight;
         n = 31 * n + this.diamondMaxHeight;
         n = 31 * n + this.lapisSize;
         n = 31 * n + this.lapisCount;
         n = 31 * n + this.lapisCenterHeight;
         return 31 * n + this.lapisSpread;
      }

      public ChunkProviderSettings build() {
         return new ChunkProviderSettings(this);
      }
   }

   public static class Serializer implements JsonDeserializer<ChunkProviderSettings.Factory>, JsonSerializer<ChunkProviderSettings.Factory> {
      public ChunkProviderSettings.Factory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         ChunkProviderSettings.Factory factory = new ChunkProviderSettings.Factory();

         try {
            factory.coordinateScale = JsonUtils.getFloat(asJsonObject, "coordinateScale", factory.coordinateScale);
            factory.heightScale = JsonUtils.getFloat(asJsonObject, "heightScale", factory.heightScale);
            factory.lowerLimitScale = JsonUtils.getFloat(asJsonObject, "lowerLimitScale", factory.lowerLimitScale);
            factory.upperLimitScale = JsonUtils.getFloat(asJsonObject, "upperLimitScale", factory.upperLimitScale);
            factory.depthNoiseScaleX = JsonUtils.getFloat(asJsonObject, "depthNoiseScaleX", factory.depthNoiseScaleX);
            factory.depthNoiseScaleZ = JsonUtils.getFloat(asJsonObject, "depthNoiseScaleZ", factory.depthNoiseScaleZ);
            factory.depthNoiseScaleExponent = JsonUtils.getFloat(asJsonObject, "depthNoiseScaleExponent", factory.depthNoiseScaleExponent);
            factory.mainNoiseScaleX = JsonUtils.getFloat(asJsonObject, "mainNoiseScaleX", factory.mainNoiseScaleX);
            factory.mainNoiseScaleY = JsonUtils.getFloat(asJsonObject, "mainNoiseScaleY", factory.mainNoiseScaleY);
            factory.mainNoiseScaleZ = JsonUtils.getFloat(asJsonObject, "mainNoiseScaleZ", factory.mainNoiseScaleZ);
            factory.baseSize = JsonUtils.getFloat(asJsonObject, "baseSize", factory.baseSize);
            factory.stretchY = JsonUtils.getFloat(asJsonObject, "stretchY", factory.stretchY);
            factory.biomeDepthWeight = JsonUtils.getFloat(asJsonObject, "biomeDepthWeight", factory.biomeDepthWeight);
            factory.biomeDepthOffset = JsonUtils.getFloat(asJsonObject, "biomeDepthOffset", factory.biomeDepthOffset);
            factory.biomeScaleWeight = JsonUtils.getFloat(asJsonObject, "biomeScaleWeight", factory.biomeScaleWeight);
            factory.biomeScaleOffset = JsonUtils.getFloat(asJsonObject, "biomeScaleOffset", factory.biomeScaleOffset);
            factory.seaLevel = JsonUtils.getInt(asJsonObject, "seaLevel", factory.seaLevel);
            factory.useCaves = JsonUtils.getBoolean(asJsonObject, "useCaves", factory.useCaves);
            factory.useDungeons = JsonUtils.getBoolean(asJsonObject, "useDungeons", factory.useDungeons);
            factory.dungeonChance = JsonUtils.getInt(asJsonObject, "dungeonChance", factory.dungeonChance);
            factory.useStrongholds = JsonUtils.getBoolean(asJsonObject, "useStrongholds", factory.useStrongholds);
            factory.useVillages = JsonUtils.getBoolean(asJsonObject, "useVillages", factory.useVillages);
            factory.useMineShafts = JsonUtils.getBoolean(asJsonObject, "useMineShafts", factory.useMineShafts);
            factory.useTemples = JsonUtils.getBoolean(asJsonObject, "useTemples", factory.useTemples);
            factory.useMonuments = JsonUtils.getBoolean(asJsonObject, "useMonuments", factory.useMonuments);
            factory.useRavines = JsonUtils.getBoolean(asJsonObject, "useRavines", factory.useRavines);
            factory.useWaterLakes = JsonUtils.getBoolean(asJsonObject, "useWaterLakes", factory.useWaterLakes);
            factory.waterLakeChance = JsonUtils.getInt(asJsonObject, "waterLakeChance", factory.waterLakeChance);
            factory.useLavaLakes = JsonUtils.getBoolean(asJsonObject, "useLavaLakes", factory.useLavaLakes);
            factory.lavaLakeChance = JsonUtils.getInt(asJsonObject, "lavaLakeChance", factory.lavaLakeChance);
            factory.useLavaOceans = JsonUtils.getBoolean(asJsonObject, "useLavaOceans", factory.useLavaOceans);
            factory.fixedBiome = JsonUtils.getInt(asJsonObject, "fixedBiome", factory.fixedBiome);
            if (factory.fixedBiome >= 38 || factory.fixedBiome < -1) {
               factory.fixedBiome = -1;
            } else if (factory.fixedBiome >= BiomeGenBase.hell.biomeID) {
               factory.fixedBiome += 2;
            }

            factory.biomeSize = JsonUtils.getInt(asJsonObject, "biomeSize", factory.biomeSize);
            factory.riverSize = JsonUtils.getInt(asJsonObject, "riverSize", factory.riverSize);
            factory.dirtSize = JsonUtils.getInt(asJsonObject, "dirtSize", factory.dirtSize);
            factory.dirtCount = JsonUtils.getInt(asJsonObject, "dirtCount", factory.dirtCount);
            factory.dirtMinHeight = JsonUtils.getInt(asJsonObject, "dirtMinHeight", factory.dirtMinHeight);
            factory.dirtMaxHeight = JsonUtils.getInt(asJsonObject, "dirtMaxHeight", factory.dirtMaxHeight);
            factory.gravelSize = JsonUtils.getInt(asJsonObject, "gravelSize", factory.gravelSize);
            factory.gravelCount = JsonUtils.getInt(asJsonObject, "gravelCount", factory.gravelCount);
            factory.gravelMinHeight = JsonUtils.getInt(asJsonObject, "gravelMinHeight", factory.gravelMinHeight);
            factory.gravelMaxHeight = JsonUtils.getInt(asJsonObject, "gravelMaxHeight", factory.gravelMaxHeight);
            factory.graniteSize = JsonUtils.getInt(asJsonObject, "graniteSize", factory.graniteSize);
            factory.graniteCount = JsonUtils.getInt(asJsonObject, "graniteCount", factory.graniteCount);
            factory.graniteMinHeight = JsonUtils.getInt(asJsonObject, "graniteMinHeight", factory.graniteMinHeight);
            factory.graniteMaxHeight = JsonUtils.getInt(asJsonObject, "graniteMaxHeight", factory.graniteMaxHeight);
            factory.dioriteSize = JsonUtils.getInt(asJsonObject, "dioriteSize", factory.dioriteSize);
            factory.dioriteCount = JsonUtils.getInt(asJsonObject, "dioriteCount", factory.dioriteCount);
            factory.dioriteMinHeight = JsonUtils.getInt(asJsonObject, "dioriteMinHeight", factory.dioriteMinHeight);
            factory.dioriteMaxHeight = JsonUtils.getInt(asJsonObject, "dioriteMaxHeight", factory.dioriteMaxHeight);
            factory.andesiteSize = JsonUtils.getInt(asJsonObject, "andesiteSize", factory.andesiteSize);
            factory.andesiteCount = JsonUtils.getInt(asJsonObject, "andesiteCount", factory.andesiteCount);
            factory.andesiteMinHeight = JsonUtils.getInt(asJsonObject, "andesiteMinHeight", factory.andesiteMinHeight);
            factory.andesiteMaxHeight = JsonUtils.getInt(asJsonObject, "andesiteMaxHeight", factory.andesiteMaxHeight);
            factory.coalSize = JsonUtils.getInt(asJsonObject, "coalSize", factory.coalSize);
            factory.coalCount = JsonUtils.getInt(asJsonObject, "coalCount", factory.coalCount);
            factory.coalMinHeight = JsonUtils.getInt(asJsonObject, "coalMinHeight", factory.coalMinHeight);
            factory.coalMaxHeight = JsonUtils.getInt(asJsonObject, "coalMaxHeight", factory.coalMaxHeight);
            factory.ironSize = JsonUtils.getInt(asJsonObject, "ironSize", factory.ironSize);
            factory.ironCount = JsonUtils.getInt(asJsonObject, "ironCount", factory.ironCount);
            factory.ironMinHeight = JsonUtils.getInt(asJsonObject, "ironMinHeight", factory.ironMinHeight);
            factory.ironMaxHeight = JsonUtils.getInt(asJsonObject, "ironMaxHeight", factory.ironMaxHeight);
            factory.goldSize = JsonUtils.getInt(asJsonObject, "goldSize", factory.goldSize);
            factory.goldCount = JsonUtils.getInt(asJsonObject, "goldCount", factory.goldCount);
            factory.goldMinHeight = JsonUtils.getInt(asJsonObject, "goldMinHeight", factory.goldMinHeight);
            factory.goldMaxHeight = JsonUtils.getInt(asJsonObject, "goldMaxHeight", factory.goldMaxHeight);
            factory.redstoneSize = JsonUtils.getInt(asJsonObject, "redstoneSize", factory.redstoneSize);
            factory.redstoneCount = JsonUtils.getInt(asJsonObject, "redstoneCount", factory.redstoneCount);
            factory.redstoneMinHeight = JsonUtils.getInt(asJsonObject, "redstoneMinHeight", factory.redstoneMinHeight);
            factory.redstoneMaxHeight = JsonUtils.getInt(asJsonObject, "redstoneMaxHeight", factory.redstoneMaxHeight);
            factory.diamondSize = JsonUtils.getInt(asJsonObject, "diamondSize", factory.diamondSize);
            factory.diamondCount = JsonUtils.getInt(asJsonObject, "diamondCount", factory.diamondCount);
            factory.diamondMinHeight = JsonUtils.getInt(asJsonObject, "diamondMinHeight", factory.diamondMinHeight);
            factory.diamondMaxHeight = JsonUtils.getInt(asJsonObject, "diamondMaxHeight", factory.diamondMaxHeight);
            factory.lapisSize = JsonUtils.getInt(asJsonObject, "lapisSize", factory.lapisSize);
            factory.lapisCount = JsonUtils.getInt(asJsonObject, "lapisCount", factory.lapisCount);
            factory.lapisCenterHeight = JsonUtils.getInt(asJsonObject, "lapisCenterHeight", factory.lapisCenterHeight);
            factory.lapisSpread = JsonUtils.getInt(asJsonObject, "lapisSpread", factory.lapisSpread);
         } catch (Exception e) {
         }

         return factory;
      }

      public JsonElement serialize(ChunkProviderSettings.Factory factory, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("coordinateScale", factory.coordinateScale);
         jsonObject.addProperty("heightScale", factory.heightScale);
         jsonObject.addProperty("lowerLimitScale", factory.lowerLimitScale);
         jsonObject.addProperty("upperLimitScale", factory.upperLimitScale);
         jsonObject.addProperty("depthNoiseScaleX", factory.depthNoiseScaleX);
         jsonObject.addProperty("depthNoiseScaleZ", factory.depthNoiseScaleZ);
         jsonObject.addProperty("depthNoiseScaleExponent", factory.depthNoiseScaleExponent);
         jsonObject.addProperty("mainNoiseScaleX", factory.mainNoiseScaleX);
         jsonObject.addProperty("mainNoiseScaleY", factory.mainNoiseScaleY);
         jsonObject.addProperty("mainNoiseScaleZ", factory.mainNoiseScaleZ);
         jsonObject.addProperty("baseSize", factory.baseSize);
         jsonObject.addProperty("stretchY", factory.stretchY);
         jsonObject.addProperty("biomeDepthWeight", factory.biomeDepthWeight);
         jsonObject.addProperty("biomeDepthOffset", factory.biomeDepthOffset);
         jsonObject.addProperty("biomeScaleWeight", factory.biomeScaleWeight);
         jsonObject.addProperty("biomeScaleOffset", factory.biomeScaleOffset);
         jsonObject.addProperty("seaLevel", factory.seaLevel);
         jsonObject.addProperty("useCaves", factory.useCaves);
         jsonObject.addProperty("useDungeons", factory.useDungeons);
         jsonObject.addProperty("dungeonChance", factory.dungeonChance);
         jsonObject.addProperty("useStrongholds", factory.useStrongholds);
         jsonObject.addProperty("useVillages", factory.useVillages);
         jsonObject.addProperty("useMineShafts", factory.useMineShafts);
         jsonObject.addProperty("useTemples", factory.useTemples);
         jsonObject.addProperty("useMonuments", factory.useMonuments);
         jsonObject.addProperty("useRavines", factory.useRavines);
         jsonObject.addProperty("useWaterLakes", factory.useWaterLakes);
         jsonObject.addProperty("waterLakeChance", factory.waterLakeChance);
         jsonObject.addProperty("useLavaLakes", factory.useLavaLakes);
         jsonObject.addProperty("lavaLakeChance", factory.lavaLakeChance);
         jsonObject.addProperty("useLavaOceans", factory.useLavaOceans);
         jsonObject.addProperty("fixedBiome", factory.fixedBiome);
         jsonObject.addProperty("biomeSize", factory.biomeSize);
         jsonObject.addProperty("riverSize", factory.riverSize);
         jsonObject.addProperty("dirtSize", factory.dirtSize);
         jsonObject.addProperty("dirtCount", factory.dirtCount);
         jsonObject.addProperty("dirtMinHeight", factory.dirtMinHeight);
         jsonObject.addProperty("dirtMaxHeight", factory.dirtMaxHeight);
         jsonObject.addProperty("gravelSize", factory.gravelSize);
         jsonObject.addProperty("gravelCount", factory.gravelCount);
         jsonObject.addProperty("gravelMinHeight", factory.gravelMinHeight);
         jsonObject.addProperty("gravelMaxHeight", factory.gravelMaxHeight);
         jsonObject.addProperty("graniteSize", factory.graniteSize);
         jsonObject.addProperty("graniteCount", factory.graniteCount);
         jsonObject.addProperty("graniteMinHeight", factory.graniteMinHeight);
         jsonObject.addProperty("graniteMaxHeight", factory.graniteMaxHeight);
         jsonObject.addProperty("dioriteSize", factory.dioriteSize);
         jsonObject.addProperty("dioriteCount", factory.dioriteCount);
         jsonObject.addProperty("dioriteMinHeight", factory.dioriteMinHeight);
         jsonObject.addProperty("dioriteMaxHeight", factory.dioriteMaxHeight);
         jsonObject.addProperty("andesiteSize", factory.andesiteSize);
         jsonObject.addProperty("andesiteCount", factory.andesiteCount);
         jsonObject.addProperty("andesiteMinHeight", factory.andesiteMinHeight);
         jsonObject.addProperty("andesiteMaxHeight", factory.andesiteMaxHeight);
         jsonObject.addProperty("coalSize", factory.coalSize);
         jsonObject.addProperty("coalCount", factory.coalCount);
         jsonObject.addProperty("coalMinHeight", factory.coalMinHeight);
         jsonObject.addProperty("coalMaxHeight", factory.coalMaxHeight);
         jsonObject.addProperty("ironSize", factory.ironSize);
         jsonObject.addProperty("ironCount", factory.ironCount);
         jsonObject.addProperty("ironMinHeight", factory.ironMinHeight);
         jsonObject.addProperty("ironMaxHeight", factory.ironMaxHeight);
         jsonObject.addProperty("goldSize", factory.goldSize);
         jsonObject.addProperty("goldCount", factory.goldCount);
         jsonObject.addProperty("goldMinHeight", factory.goldMinHeight);
         jsonObject.addProperty("goldMaxHeight", factory.goldMaxHeight);
         jsonObject.addProperty("redstoneSize", factory.redstoneSize);
         jsonObject.addProperty("redstoneCount", factory.redstoneCount);
         jsonObject.addProperty("redstoneMinHeight", factory.redstoneMinHeight);
         jsonObject.addProperty("redstoneMaxHeight", factory.redstoneMaxHeight);
         jsonObject.addProperty("diamondSize", factory.diamondSize);
         jsonObject.addProperty("diamondCount", factory.diamondCount);
         jsonObject.addProperty("diamondMinHeight", factory.diamondMinHeight);
         jsonObject.addProperty("diamondMaxHeight", factory.diamondMaxHeight);
         jsonObject.addProperty("lapisSize", factory.lapisSize);
         jsonObject.addProperty("lapisCount", factory.lapisCount);
         jsonObject.addProperty("lapisCenterHeight", factory.lapisCenterHeight);
         jsonObject.addProperty("lapisSpread", factory.lapisSpread);
         return jsonObject;
      }
   }
}
