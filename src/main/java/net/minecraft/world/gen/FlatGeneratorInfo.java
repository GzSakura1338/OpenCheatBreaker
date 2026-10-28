package net.minecraft.world.gen;

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
    public List flatLayers = new ArrayList();
    public Map worldFeatures = new HashMap();
    public int biomeToUse;

    public int getBiome() {
        return this.biomeToUse;
    }

    public void setBiome(int n) {
        this.biomeToUse = n;
    }

    public Map getWorldFeatures() {
        return this.worldFeatures;
    }

    public List getFlatLayers() {
        return this.flatLayers;
    }

    public void updateLayerLevel() {
        int n = 0;

        for (FlatLayerInfo flatLayerInfo : (Iterable<FlatLayerInfo>)this.flatLayers) {
            flatLayerInfo.setMinY(n);
            n += flatLayerInfo.getLayerCount();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(2);
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

            for (Entry entry : (Iterable<Entry>)this.worldFeatures.entrySet()) {
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

    public static FlatLayerInfo createFlatLayerInfo(String str, int n) {
        String[] split = str.split("x", 2);
        int n2 = 1;
        int n3 = 0;
        if (split.length == 2) {
            try {
                n2 = Integer.parseInt(split[0]);
                if (n + n2 >= 256) {
                    n2 = 256 - n;
                }

                if (n2 < 0) {
                    n2 = 0;
                }
            } catch (Throwable e) {
                return null;
            }
        }

        int n4;
        try {
            String str2 = split[split.length - 1];
            split = str2.split(":", 2);
            n4 = Integer.parseInt(split[0]);
            if (split.length > 1) {
                n3 = Integer.parseInt(split[1]);
            }

            if (Block.getBlockById(n4) == Blocks.air) {
                n4 = 0;
                n3 = 0;
            }

            if (n3 < 0 || n3 > 15) {
                n3 = 0;
            }
        } catch (Throwable e) {
            return null;
        }

        FlatLayerInfo flatLayerInfo = new FlatLayerInfo(n2, Block.getBlockById(n4), n3);
        flatLayerInfo.setMinY(n);
        return flatLayerInfo;
    }

    public static List createArrayList(String str) {
        if (str != null && str.length() >= 1) {
            ArrayList list = new ArrayList();
            String[] split = str.split(",");
            int n = 0;
            String[] strs = split;
            int length = split.length;

            for (int i = 0; i < length; i++) {
                String str2 = strs[i];
                FlatLayerInfo createFlatLayerInfo2 = createFlatLayerInfo(str2, n);
                if (createFlatLayerInfo2 == null) {
                    return null;
                }

                list.add(createFlatLayerInfo2);
                n += createFlatLayerInfo2.getLayerCount();
            }

            return list;
        } else {
            return null;
        }
    }

    public static FlatGeneratorInfo createFlatGeneratorFromString(String str) {
        if (str == null) {
            return getDefaultFlatGenerator();
        }

        String[] split = str.split(";", -1);
        int n = split.length == 1 ? 0 : MathHelper.parseIntWithDefault(split[0], 0);
        if (n >= 0 && n <= 2) {
            FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
            int n2 = split.length == 1 ? 0 : 1;
            List createArrayList2 = createArrayList(split[n2++]);
            if (createArrayList2 != null && !createArrayList2.isEmpty()) {
                flatGeneratorInfo.getFlatLayers().addAll(createArrayList2);
                flatGeneratorInfo.updateLayerLevel();
                int biomeID = BiomeGenBase.plains.biomeID;
                if (n > 0 && split.length > n2) {
                    biomeID = MathHelper.parseIntWithDefault(split[n2++], biomeID);
                }

                flatGeneratorInfo.setBiome(biomeID);
                if (n > 0 && split.length > n2) {
                    String[] split2 = split[n2++].toLowerCase().split(",");
                    String[] strs = split2;
                    int length = split2.length;

                    for (int i = 0; i < length; i++) {
                        String str2 = strs[i];
                        String[] split3 = str2.split("\\(", 2);
                        HashMap map = new HashMap();
                        if (split3[0].length() > 0) {
                            flatGeneratorInfo.getWorldFeatures().put(split3[0], map);
                            if (split3.length > 1 && split3[1].endsWith(")") && split3[1].length() > 1) {
                                String[] split4 = split3[1].substring(0, split3[1].length() - 1).split(" ");

                                for (int j = 0; j < split4.length; j++) {
                                    String[] split5 = split4[j].split("=", 2);
                                    if (split5.length == 2) {
                                        map.put(split5[0], split5[1]);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    flatGeneratorInfo.getWorldFeatures().put("village", new HashMap());
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
        flatGeneratorInfo.getWorldFeatures().put("village", new HashMap());
        return flatGeneratorInfo;
    }
}
