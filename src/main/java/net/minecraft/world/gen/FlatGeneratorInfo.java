package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo {
   public List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   public Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   public int biomeToUse;

   public int getBiome() {
      return this.biomeToUse;
   }

   public void setBiome(int biome) {
      this.biomeToUse = biome;
   }

   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void updateLayerLevel() {
      int n = 0;

      for (FlatLayerInfo flatLayerInfo : this.flatLayers) {
         flatLayerInfo.setMinY(n);
         n += flatLayerInfo.getLayerCount();
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(3);
      sb.append(";");

      for (int i = 0; i < this.flatLayers.size(); i++) {
         if (i > 0) {
            sb.append(",");
         }

         sb.append(this.flatLayers.get(i).toString());
      }

      sb.append(";");
      sb.append(this.biomeToUse);
      if (!this.worldFeatures.isEmpty()) {
         sb.append(";");
         int n = 0;

         for (Entry entry : this.worldFeatures.entrySet()) {
            if (n++ > 0) {
               sb.append(",");
            }

            sb.append(((String)entry.getKey()).toLowerCase());
            Map value = (Map)entry.getValue();
            if (!value.isEmpty()) {
               sb.append("(");
               int n2 = 0;

               for (Entry entry2 : (Iterable<Entry>)value.entrySet()) {
                  if (n2++ > 0) {
                     sb.append(" ");
                  }

                  sb.append((String)entry2.getKey());
                  sb.append("=");
                  sb.append((String)entry2.getValue());
               }

               sb.append(")");
            }
         }
      } else {
         sb.append(";");
      }

      return sb.toString();
   }

   public static FlatLayerInfo findFlatLayerInfo(int n, String str, int n2) {
      String[] strs = n >= 3 ? str.split("\\*", 2) : str.split("x", 2);
      int n3 = 1;
      int n4 = 0;
      if (strs.length == 2) {
         try {
            n3 = Integer.parseInt(strs[0]);
            if (n2 + n3 >= 256) {
               n3 = 256 - n2;
            }

            if (n3 < 0) {
               n3 = 0;
            }
         } catch (Throwable e) {
            return null;
         }
      }

      Block block = null;

      try {
         String str2 = strs[strs.length - 1];
         if (n < 3) {
            strs = str2.split(":", 2);
            if (strs.length > 1) {
               n4 = Integer.parseInt(strs[1]);
            }

            block = Block.getBlockById(Integer.parseInt(strs[0]));
         } else {
            strs = str2.split(":", 3);
            block = strs.length > 1 ? Block.getBlockFromName(strs[0] + ":" + strs[1]) : null;
            if (block != null) {
               n4 = strs.length > 2 ? Integer.parseInt(strs[2]) : 0;
            } else {
               block = Block.getBlockFromName(strs[0]);
               if (block != null) {
                  n4 = strs.length > 1 ? Integer.parseInt(strs[1]) : 0;
               }
            }

            if (block == null) {
               return null;
            }
         }

         if (block == Blocks.air) {
            n4 = 0;
         }

         if (n4 < 0 || n4 > 15) {
            n4 = 0;
         }
      } catch (Throwable e) {
         return null;
      }

      FlatLayerInfo flatLayerInfo = new FlatLayerInfo(n, n3, block, n4);
      flatLayerInfo.setMinY(n2);
      return flatLayerInfo;
   }

   public static List<FlatLayerInfo> findList(int n, String str) {
      if (str != null && str.length() >= 1) {
         ArrayList newArrayList = Lists.newArrayList();
         String[] split = str.split(",");
         int n2 = 0;

         for (String str2 : split) {
            FlatLayerInfo findFlatLayerInfo2 = findFlatLayerInfo(n, str2, n2);
            if (findFlatLayerInfo2 == null) {
               return null;
            }

            newArrayList.add(findFlatLayerInfo2);
            n2 += findFlatLayerInfo2.getLayerCount();
         }

         return newArrayList;
      } else {
         return null;
      }
   }

   public static FlatGeneratorInfo createFlatGeneratorFromString(String flatGeneratorSettings) {
      if (flatGeneratorSettings == null) {
         return getDefaultFlatGenerator();
      }

      String[] split = flatGeneratorSettings.split(";", -1);
      int n = split.length == 1 ? 0 : MathHelper.parseIntWithDefault(split[0], 0);
      if (n >= 0 && n <= 3) {
         FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
         int n2 = split.length == 1 ? 0 : 1;
         List findList2 = findList(n, split[n2++]);
         if (findList2 != null && !findList2.isEmpty()) {
            flatGeneratorInfo.getFlatLayers().addAll(findList2);
            flatGeneratorInfo.updateLayerLevel();
            int biomeID = BiomeGenBase.plains.biomeID;
            if (n > 0 && split.length > n2) {
               biomeID = MathHelper.parseIntWithDefault(split[n2++], biomeID);
            }

            flatGeneratorInfo.setBiome(biomeID);
            if (n > 0 && split.length > n2) {
               String[] split2 = split[n2++].toLowerCase().split(",");

               for (String str : split2) {
                  String[] split3 = str.split("\\(", 2);
                  HashMap newHashMap = Maps.newHashMap();
                  if (split3[0].length() > 0) {
                     flatGeneratorInfo.getWorldFeatures().put(split3[0], newHashMap);
                     if (split3.length > 1 && split3[1].endsWith(")") && split3[1].length() > 1) {
                        String[] split4 = split3[1].substring(0, split3[1].length() - 1).split(" ");

                        for (int i = 0; i < split4.length; i++) {
                           String[] split5 = split4[i].split("=", 2);
                           if (split5.length == 2) {
                              newHashMap.put(split5[0], split5[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatGeneratorInfo.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return flatGeneratorInfo;
         } else {
            return getDefaultFlatGenerator();
         }
      } else {
         return getDefaultFlatGenerator();
      }
   }

   public static FlatGeneratorInfo getDefaultFlatGenerator() {
      FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
      flatGeneratorInfo.setBiome(BiomeGenBase.plains.biomeID);
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.bedrock));
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(2, Blocks.dirt));
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.grass));
      flatGeneratorInfo.updateLayerLevel();
      flatGeneratorInfo.getWorldFeatures().put("village", Maps.newHashMap());
      return flatGeneratorInfo;
   }
}
