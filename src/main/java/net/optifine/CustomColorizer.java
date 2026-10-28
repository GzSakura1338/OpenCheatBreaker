package net.optifine;

import com.cheatbreaker.client.CheatBreaker;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class CustomColorizer {
    public static int[] grassColors = null;
    public static int[] waterColors = null;
    public static int[] foliageColors = null;
    public static int[] foliagePineColors = null;
    public static int[] foliageBirchColors = null;
    public static int[] swampFoliageColors = null;
    public static int[] swampGrassColors = null;
    public static int[][] blockPalettes = (int[][])null;
    public static int[][] paletteColors = (int[][])null;
    public static int[] skyColors = null;
    public static int[] fogColors = null;
    public static int[] underwaterColors = null;
    public static float[][][] lightMapsColorsRgb = (float[][][])null;
    public static int[] lightMapsHeight = null;
    public static float[][] sunRgbs = new float[16][3];
    public static float[][] torchRgbs = new float[16][3];
    public static int[] redstoneColors = null;
    public static int[] stemColors = null;
    public static int[] myceliumParticleColors = null;
    public static boolean useDefaultColorMultiplier = true;
    public static int particleWaterColor = -1;
    public static int particlePortalColor = -1;
    public static int lilyPadColor = -1;
    public static Vec3 fogColorNether = null;
    public static Vec3 fogColorEnd = null;
    public static Vec3 skyColorEnd = null;
    public static int[] textColors = null;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_GRASS = 1;
    public static final int TYPE_FOLIAGE = 2;
    public static Random random = new Random();

    public static void update() {
        grassColors = null;
        waterColors = null;
        foliageColors = null;
        foliageBirchColors = null;
        foliagePineColors = null;
        swampGrassColors = null;
        swampFoliageColors = null;
        skyColors = null;
        fogColors = null;
        underwaterColors = null;
        redstoneColors = null;
        stemColors = null;
        myceliumParticleColors = null;
        lightMapsColorsRgb = (float[][][])null;
        lightMapsHeight = null;
        lilyPadColor = -1;
        particleWaterColor = -1;
        particlePortalColor = -1;
        fogColorNether = null;
        fogColorEnd = null;
        skyColorEnd = null;
        textColors = null;
        blockPalettes = (int[][])null;
        paletteColors = (int[][])null;
        useDefaultColorMultiplier = true;
        String str = "mcpatcher/colormap/";
        grassColors = getCustomColors("textures/colormap/grass.png", 65536);
        foliageColors = getCustomColors("textures/colormap/foliage.png", 65536);
        String[] strs = new String[]{"water.png", "watercolorX.png"};
        waterColors = getCustomColors(str, strs, 65536);
        if (Config.isCustomColors()) {
            String[] strs2 = new String[]{"pine.png", "pinecolor.png"};
            foliagePineColors = getCustomColors(str, strs2, 65536);
            String[] strs3 = new String[]{"birch.png", "birchcolor.png"};
            foliageBirchColors = getCustomColors(str, strs3, 65536);
            String[] strs4 = new String[]{"swampgrass.png", "swampgrasscolor.png"};
            swampGrassColors = getCustomColors(str, strs4, 65536);
            String[] strs5 = new String[]{"swampfoliage.png", "swampfoliagecolor.png"};
            swampFoliageColors = getCustomColors(str, strs5, 65536);
            String[] strs6 = new String[]{"sky0.png", "skycolor0.png"};
            skyColors = getCustomColors(str, strs6, 65536);
            String[] strs7 = new String[]{"fog0.png", "fogcolor0.png"};
            fogColors = getCustomColors(str, strs7, 65536);
            String[] strs8 = new String[]{"underwater.png", "underwatercolor.png"};
            underwaterColors = getCustomColors(str, strs8, 65536);
            String[] strs9 = new String[]{"redstone.png", "redstonecolor.png"};
            redstoneColors = getCustomColors(str, strs9, 16);
            String[] strs10 = new String[]{"stem.png", "stemcolor.png"};
            stemColors = getCustomColors(str, strs10, 8);
            String[] strs11 = new String[]{"myceliumparticle.png", "myceliumparticlecolor.png"};
            myceliumParticleColors = getCustomColors(str, strs11, -1);
            int[][] intses = new int[3][];
            lightMapsColorsRgb = new float[3][][];
            lightMapsHeight = new int[3];

            for (int i = 0; i < intses.length; i++) {
                String str2 = "mcpatcher/lightmap/world" + (i - 1) + ".png";
                intses[i] = getCustomColors(str2, -1);
                if (intses[i] != null) {
                    lightMapsColorsRgb[i] = toRgb(intses[i]);
                }

                lightMapsHeight[i] = getTextureHeight(str2, 32);
            }

            readColorProperties("mcpatcher/color.properties");
            updateUseDefaultColorMultiplier();
        }
    }

    public static int getTextureHeight(String str, int n) {
        try {
            InputStream resourceStream = Config.getResourceStream(new ResourceLocation(str));
            if (resourceStream == null) {
                return n;
            }

            BufferedImage read = ImageIO.read(resourceStream);
            return read == null ? n : read.getHeight();
        } catch (IOException e) {
            return n;
        }
    }

    public static float[][] toRgb(int[] ints) {
        float[][] floatses = new float[ints.length][3];

        for (int i = 0; i < ints.length; i++) {
            int n = ints[i];
            float f = (n >> 16 & 0xFF) / 255.0F;
            float f2 = (n >> 8 & 0xFF) / 255.0F;
            float f3 = (n & 0xFF) / 255.0F;
            float[] floats = floatses[i];
            floats[0] = f;
            floats[1] = f2;
            floats[2] = f3;
        }

        return floatses;
    }

    public static void readColorProperties(String str) {
        try {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream == null) {
                return;
            }

            Config.log("Loading " + str);
            Properties properties = new Properties();
            properties.load(resourceStream);
            lilyPadColor = readColor(properties, "lilypad");
            particleWaterColor = readColor(properties, new String[]{"particle.water", "drop.water"});
            particlePortalColor = readColor(properties, "particle.portal");
            fogColorNether = readColorVec3(properties, "fog.nether");
            fogColorEnd = readColorVec3(properties, "fog.end");
            skyColorEnd = readColorVec3(properties, "sky.end");
            textColors = readTextColors(properties, str, "text.code.", "Text");
            readCustomPalettes(properties, str);
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readCustomPalettes(Properties properties, String str) {
        blockPalettes = new int[256][1];

        for (int i = 0; i < 256; i++) {
            blockPalettes[i][0] = -1;
        }

        String str2 = "palette.block.";
        HashMap map = new HashMap();

        for (String str3 : (Iterable<String>)(Iterable)properties.keySet()) {
            String property = properties.getProperty(str3);
            if (str3.startsWith(str2)) {
                map.put(str3, property);
            }
        }

        String[] toArray = (String[])map.keySet().toArray(new String[map.size()]);
        paletteColors = new int[toArray.length][];

        for (int i = 0; i < toArray.length; i++) {
            String str3 = toArray[i];
            String property = properties.getProperty(str3);
            Config.log("Block palette: " + str3 + " = " + property);
            String substring = str3.substring(str2.length());
            String basePath = TextureUtils.getBasePath(str);
            substring = TextureUtils.fixResourcePath(substring, basePath);
            int[] customColors = getCustomColors(substring, 65536);
            paletteColors[i] = customColors;
            String[] tokenize = Config.tokenize(property, " ,;");

            for (int j = 0; j < tokenize.length; j++) {
                String str4 = tokenize[j];
                int n = -1;
                if (str4.contains(":")) {
                    String[] tokenize2 = Config.tokenize(str4, ":");
                    str4 = tokenize2[0];
                    String str5 = tokenize2[1];
                    n = Config.parseInt(str5, -1);
                    if (n < 0 || n > 15) {
                        Config.log("Invalid block metadata: " + str4 + " in palette: " + str3);
                        continue;
                    }
                }

                int parseInt = Config.parseInt(str4, -1);
                if (parseInt < 0 || parseInt > 255) {
                    Config.log("Invalid block index: " + parseInt + " in palette: " + str3);
                } else if (parseInt != Block.getIdFromBlock(Blocks.grass)
                    && parseInt != Block.getIdFromBlock(Blocks.tallgrass)
                    && parseInt != Block.getIdFromBlock(Blocks.leaves)
                    && parseInt != Block.getIdFromBlock(Blocks.vine)) {
                    if (n == -1) {
                        blockPalettes[parseInt][0] = i;
                    } else {
                        if (blockPalettes[parseInt].length < 16) {
                            blockPalettes[parseInt] = new int[16];
                            Arrays.fill(blockPalettes[parseInt], -1);
                        }

                        blockPalettes[parseInt][n] = i;
                    }
                }
            }
        }
    }

    public static int readColor(Properties properties, String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            int readColor2 = readColor(properties, str);
            if (readColor2 >= 0) {
                return readColor2;
            }
        }

        return -1;
    }

    public static int readColor(Properties properties, String str) {
        String property = properties.getProperty(str);
        if (property == null) {
            return -1;
        }

        try {
            int n = Integer.parseInt(property, 16) & 16777215;
            Config.log("Custom color: " + str + " = " + property);
            return n;
        } catch (NumberFormatException e) {
            Config.log("Invalid custom color: " + str + " = " + property);
            return -1;
        }
    }

    public static Vec3 readColorVec3(Properties properties, String str) {
        int readColor2 = readColor(properties, str);
        if (readColor2 < 0) {
            return null;
        }

        int n = readColor2 >> 16 & 0xFF;
        int n2 = readColor2 >> 8 & 0xFF;
        int n3 = readColor2 & 0xFF;
        float f = n / 255.0F;
        float f2 = n2 / 255.0F;
        float f3 = n3 / 255.0F;
        return Vec3.createVectorHelper(f, f2, f3);
    }

    public static int[] getCustomColors(String str, String[] strs, int n) {
        for (int i = 0; i < strs.length; i++) {
            String str2 = strs[i];
            str2 = str + str2;
            int[] customColors = getCustomColors(str2, n);
            if (customColors != null) {
                return customColors;
            }
        }

        return null;
    }

    public static int[] getCustomColors(String str, int n) {
        try {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream == null) {
                return null;
            } else {
                int[] readImageData = TextureUtil.readImageData(Config.getResourceManager(), resourceLocation);
                if (readImageData == null) {
                    return null;
                } else if (n > 0 && readImageData.length != n) {
                    Config.log("Invalid custom colors length: " + readImageData.length + ", path: " + str);
                    return null;
                } else {
                    Config.log("Loading custom colors: " + str);
                    return readImageData;
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateUseDefaultColorMultiplier() {
        useDefaultColorMultiplier = foliageBirchColors == null
            && foliagePineColors == null
            && swampGrassColors == null
            && swampFoliageColors == null
            && blockPalettes == null
            && Config.isSwampColors()
            && Config.isSmoothBiomes();
    }

    public static int getColorMultiplier(Block block, IBlockAccess blockAccess, int n, int n2, int n3) {
        if (useDefaultColorMultiplier) {
            return block.colorMultiplier(blockAccess, n, n2, n3);
        }

        int[] ints = null;
        int[] ints2 = null;
        if (blockPalettes != null) {
            int idFromBlock = Block.getIdFromBlock(block);
            if (idFromBlock >= 0 && idFromBlock < 256) {
                int[] ints3 = blockPalettes[idFromBlock];
                boolean flag = true;
                int n4;
                if (ints3.length > 1) {
                    int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
                    n4 = ints3[blockMetadata];
                } else {
                    n4 = ints3[0];
                }

                if (n4 >= 0) {
                    ints = paletteColors[n4];
                }
            }

            if (ints != null) {
                if (Config.isSmoothBiomes()) {
                    return getSmoothColorMultiplier(block, blockAccess, n, n2, n3, ints, ints, 0, 0);
                }

                return getCustomColor(ints, blockAccess, n, n2, n3);
            }
        }

        boolean isSwampColors = Config.isSwampColors();
        boolean flag = false;
        byte b = 0;
        int n4 = 0;
        if (block == Blocks.grass || block == Blocks.tallgrass) {
            b = 1;
            flag = Config.isSmoothBiomes();
            ints = grassColors;
            if (isSwampColors) {
                ints2 = swampGrassColors;
            } else {
                ints2 = ints;
            }
        } else if (block == Blocks.leaves) {
            b = 2;
            flag = Config.isSmoothBiomes();
            n4 = blockAccess.getBlockMetadata(n, n2, n3);
            if ((n4 & 3) == 1) {
                ints = foliagePineColors;
            } else if ((n4 & 3) == 2) {
                ints = foliageBirchColors;
            } else {
                ints = foliageColors;
                if (isSwampColors) {
                    ints2 = swampFoliageColors;
                } else {
                    ints2 = ints;
                }
            }
        } else if (block == Blocks.vine) {
            b = 2;
            flag = Config.isSmoothBiomes();
            ints = foliageColors;
            if (isSwampColors) {
                ints2 = swampFoliageColors;
            } else {
                ints2 = ints;
            }
        }

        if (flag) {
            return getSmoothColorMultiplier(block, blockAccess, n, n2, n3, ints, ints2, b, n4);
        }

        if (ints2 != ints && blockAccess.getBiomeGenForCoords(n, n3) == BiomeGenBase.swampland) {
            ints = ints2;
        }

        return ints != null ? getCustomColor(ints, blockAccess, n, n2, n3) : block.colorMultiplier(blockAccess, n, n2, n3);
    }

    public static int getSmoothColorMultiplier(Block block, IBlockAccess blockAccess, int n, int n2, int n3, int[] ints, int[] ints2, int n4, int n5) {
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;

        for (int i = n - 1; i <= n + 1; i++) {
            for (int j = n3 - 1; j <= n3 + 1; j++) {
                int[] ints3 = ints;
                if (ints2 != ints && blockAccess.getBiomeGenForCoords(i, j) == BiomeGenBase.swampland) {
                    ints3 = ints2;
                }

                boolean flag = false;
                int n9;
                if (ints3 == null) {
                    switch (n4) {
                        case 1:
                            n9 = blockAccess.getBiomeGenForCoords(i, j).getBiomeGrassColor(n, n2, n3);
                            break;
                        case 2:
                            if ((n5 & 3) == 1) {
                                n9 = ColorizerFoliage.getFoliageColorPine();
                            } else if ((n5 & 3) == 2) {
                                n9 = ColorizerFoliage.getFoliageColorBirch();
                            } else {
                                n9 = blockAccess.getBiomeGenForCoords(i, j).getBiomeFoliageColor(n, n2, n3);
                            }
                            break;
                        default:
                            n9 = block.colorMultiplier(blockAccess, i, n2, j);
                    }
                } else {
                    n9 = getCustomColor(ints3, blockAccess, i, n2, j);
                }

                n6 += n9 >> 16 & 0xFF;
                n7 += n9 >> 8 & 0xFF;
                n8 += n9 & 0xFF;
            }
        }

        int n9 = n6 / 9;
        int n10 = n7 / 9;
        int n11 = n8 / 9;
        return n9 << 16 | n10 << 8 | n11;
    }

    public static int getFluidColor(Block block, IBlockAccess blockAccess, int n, int n2, int n3) {
        return block.getMaterial() != Material.water
            ? block.colorMultiplier(blockAccess, n, n2, n3)
            : (
                waterColors != null
                    ? (Config.isSmoothBiomes() ? getSmoothColor(waterColors, blockAccess, n, n2, n3, 3, 1) : getCustomColor(waterColors, blockAccess, n, n2, n3))
                    : (!Config.isSwampColors() ? 16777215 : block.colorMultiplier(blockAccess, n, n2, n3))
            );
    }

    public static int getCustomColor(int[] ints, IBlockAccess blockAccess, int n, int n2, int n3) {
        BiomeGenBase biomeGenForCoords = blockAccess.getBiomeGenForCoords(n, n3);
        double d = MathHelper.clamp_float(biomeGenForCoords.getFloatTemperature(n, n2, n3), 0.0F, 1.0F);
        double d2 = MathHelper.clamp_float(biomeGenForCoords.getFloatRainfall(), 0.0F, 1.0F);
        d2 *= d;
        int n4 = (int)((1.0 - d) * 255.0);
        int n5 = (int)((1.0 - d2) * 255.0);
        return ints[n5 << 8 | n4] & 16777215;
    }

    public static void updatePortalFX(EntityFX entityFX) {
        if (particlePortalColor >= 0) {
            int particlePortalColor2 = particlePortalColor;
            int n = particlePortalColor2 >> 16 & 0xFF;
            int n2 = particlePortalColor2 >> 8 & 0xFF;
            int n3 = particlePortalColor2 & 0xFF;
            float f = n / 255.0F;
            float f2 = n2 / 255.0F;
            float f3 = n3 / 255.0F;
            entityFX.setRBGColorF(f, f2, f3);
        }
    }

    public static void updateMyceliumFX(EntityFX entityFX) {
        if (myceliumParticleColors != null) {
            int n = myceliumParticleColors[random.nextInt(myceliumParticleColors.length)];
            int n2 = n >> 16 & 0xFF;
            int n3 = n >> 8 & 0xFF;
            int n4 = n & 0xFF;
            float f = n2 / 255.0F;
            float f2 = n3 / 255.0F;
            float f3 = n4 / 255.0F;
            entityFX.setRBGColorF(f, f2, f3);
        }
    }

    public static void updateReddustFX(EntityFX entityFX, IBlockAccess blockAccess, double d, double d2, double d3) {
        if (redstoneColors != null) {
            int blockMetadata = blockAccess.getBlockMetadata((int)d, (int)d2, (int)d3);
            int redstoneColor = getRedstoneColor(blockMetadata);
            if (redstoneColor != -1) {
                int n = redstoneColor >> 16 & 0xFF;
                int n2 = redstoneColor >> 8 & 0xFF;
                int n3 = redstoneColor & 0xFF;
                float f = n / 255.0F;
                float f2 = n2 / 255.0F;
                float f3 = n3 / 255.0F;
                entityFX.setRBGColorF(f, f2, f3);
            }
        }
    }

    public static int getRedstoneColor(int n) {
        return redstoneColors == null ? -1 : (n >= 0 && n <= 15 ? redstoneColors[n] & 16777215 : -1);
    }

    public static void updateWaterFX(EntityFX entityFX, IBlockAccess blockAccess) {
        if (waterColors != null) {
            int posX = (int)entityFX.posX;
            int posY = (int)entityFX.posY;
            int posZ = (int)entityFX.posZ;
            int fluidColor = getFluidColor(Blocks.water, blockAccess, posX, posY, posZ);
            int n = fluidColor >> 16 & 0xFF;
            int n2 = fluidColor >> 8 & 0xFF;
            int n3 = fluidColor & 0xFF;
            float f = n / 255.0F;
            float f2 = n2 / 255.0F;
            float f3 = n3 / 255.0F;
            if (particleWaterColor >= 0) {
                int n4 = particleWaterColor >> 16 & 0xFF;
                int n5 = particleWaterColor >> 8 & 0xFF;
                int n6 = particleWaterColor & 0xFF;
                f *= n4 / 255.0F;
                f2 *= n5 / 255.0F;
                f3 *= n6 / 255.0F;
            }

            entityFX.setRBGColorF(f, f2, f3);
        }
    }

    public static int getLilypadColor() {
        return lilyPadColor < 0 ? Blocks.waterlily.getBlockColor() : lilyPadColor;
    }

    public static Vec3 getFogColorNether(Vec3 vec3) {
        return fogColorNether == null ? vec3 : fogColorNether;
    }

    public static Vec3 getFogColorEnd(Vec3 vec3) {
        return fogColorEnd == null ? vec3 : fogColorEnd;
    }

    public static Vec3 getSkyColorEnd(Vec3 vec3) {
        return skyColorEnd == null ? vec3 : skyColorEnd;
    }

    public static Vec3 getSkyColor(Vec3 vec3, IBlockAccess blockAccess, double d, double d2, double d3) {
        if (skyColors == null) {
            return vec3;
        }

        int smoothColor = getSmoothColor(skyColors, blockAccess, d, d2, d3, 10, 1);
        int n = smoothColor >> 16 & 0xFF;
        int n2 = smoothColor >> 8 & 0xFF;
        int n3 = smoothColor & 0xFF;
        float f = n / 255.0F;
        float f2 = n2 / 255.0F;
        float f3 = n3 / 255.0F;
        float f4 = (float)vec3.xCoord / 0.5F;
        float f5 = (float)vec3.yCoord / 0.66275F;
        float zCoord = (float)vec3.zCoord;
        f *= f4;
        f2 *= f5;
        f3 *= zCoord;
        return Vec3.createVectorHelper(f, f2, f3);
    }

    public static Vec3 getFogColor(Vec3 vec3, IBlockAccess blockAccess, double d, double d2, double d3) {
        if (fogColors == null) {
            return vec3;
        }

        int smoothColor = getSmoothColor(fogColors, blockAccess, d, d2, d3, 10, 1);
        int n = smoothColor >> 16 & 0xFF;
        int n2 = smoothColor >> 8 & 0xFF;
        int n3 = smoothColor & 0xFF;
        float f = n / 255.0F;
        float f2 = n2 / 255.0F;
        float f3 = n3 / 255.0F;
        float f4 = (float)vec3.xCoord / 0.753F;
        float f5 = (float)vec3.yCoord / 0.8471F;
        float zCoord = (float)vec3.zCoord;
        f *= f4;
        f2 *= f5;
        f3 *= zCoord;
        return Vec3.createVectorHelper(f, f2, f3);
    }

    public static int[] readTextColors(Properties properties, String str, String str2, String str3) {
        int[] ints = new int[32];
        Arrays.fill(ints, -1);
        int n = 0;

        for (String str4 : (Iterable<String>)(Iterable)properties.keySet()) {
            String property = properties.getProperty(str4);
            if (str4.startsWith(str2)) {
                String removePrefix = StrUtils.removePrefix(str4, str2);
                int parseInt = Config.parseInt(removePrefix, -1);
                int parseColor2 = parseColor(property);
                if (parseInt >= 0 && parseInt < ints.length && parseColor2 >= 0) {
                    ints[parseInt] = parseColor2;
                    n++;
                } else {
                    warn("Invalid color: " + str4 + " = " + property);
                }
            }
        }

        if (n <= 0) {
            return null;
        }

        dbg(str3 + " colors: " + n);
        return ints;
    }

    public static int getTextColor(int n, int n2) {
        if (textColors == null) {
            return n2;
        } else if (n >= 0 && n < textColors.length) {
            int n3 = textColors[n];
            return n3 < 0 ? n2 : n3;
        } else {
            return n2;
        }
    }

    public static int parseColor(String str) {
        if (str == null) {
            return -1;
        }

        str = str.trim();

        try {
            return Integer.parseInt(str, 16) & 16777215;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static Vec3 getUnderwaterColor(IBlockAccess blockAccess, double d, double d2, double d3) {
        if (underwaterColors == null) {
            return null;
        }

        int smoothColor = getSmoothColor(underwaterColors, blockAccess, d, d2, d3, 10, 1);
        int n = smoothColor >> 16 & 0xFF;
        int n2 = smoothColor >> 8 & 0xFF;
        int n3 = smoothColor & 0xFF;
        float f = n / 255.0F;
        float f2 = n2 / 255.0F;
        float f3 = n3 / 255.0F;
        return Vec3.createVectorHelper(f, f2, f3);
    }

    public static int getSmoothColor(int[] ints, IBlockAccess blockAccess, double d, double d2, double d3, int n, int n2) {
        if (ints == null) {
            return -1;
        }

        int floor = (int)Math.floor(d);
        int floor2 = (int)Math.floor(d2);
        int floor3 = (int)Math.floor(d3);
        int n3 = n * n2 / 2;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;

        for (int i = floor - n3; i <= floor + n3; i += n2) {
            for (int j = floor3 - n3; j <= floor3 + n3; j += n2) {
                int customColor = getCustomColor(ints, blockAccess, i, floor2, j);
                n4 += customColor >> 16 & 0xFF;
                n5 += customColor >> 8 & 0xFF;
                n6 += customColor & 0xFF;
                n7++;
            }
        }

        int n8 = n4 / n7;
        int n9 = n5 / n7;
        int n10 = n6 / n7;
        return n8 << 16 | n9 << 8 | n10;
    }

    public static int mixColors(int n, int n2, float f) {
        if (f <= 0.0F) {
            return n2;
        }

        if (f >= 1.0F) {
            return n;
        }

        float f2 = 1.0F - f;
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        int n6 = n2 >> 16 & 0xFF;
        int n7 = n2 >> 8 & 0xFF;
        int n8 = n2 & 0xFF;
        int n9 = (int)(n3 * f + n6 * f2);
        int n10 = (int)(n4 * f + n7 * f2);
        int n11 = (int)(n5 * f + n8 * f2);
        return n9 << 16 | n10 << 8 | n11;
    }

    public static int averageColor(int n, int n2) {
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        int n6 = n2 >> 16 & 0xFF;
        int n7 = n2 >> 8 & 0xFF;
        int n8 = n2 & 0xFF;
        int n9 = (n3 + n6) / 2;
        int n10 = (n4 + n7) / 2;
        int n11 = (n5 + n8) / 2;
        return n9 << 16 | n10 << 8 | n11;
    }

    public static int getStemColorMultiplier(BlockStem blockStem, IBlockAccess blockAccess, int n, int n2, int n3) {
        if (stemColors == null) {
            return blockStem.colorMultiplier(blockAccess, n, n2, n3);
        }

        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        if (blockMetadata < 0) {
            blockMetadata = 0;
        }

        if (blockMetadata >= stemColors.length) {
            blockMetadata = stemColors.length - 1;
        }

        return stemColors[blockMetadata];
    }

    public static boolean updateLightmap(World world, float f, int[] ints, boolean flag) {
        if (world == null) {
            return false;
        }

        if (lightMapsColorsRgb == null) {
            return false;
        }

        if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().enableFpsBoost.getValue()
            && (Boolean)CheatBreaker.getInstance().getKeybindSettings().fullbright.getValue()) {
            return false;
        }

        if (!Config.isCustomColors()) {
            return false;
        }

        int dimensionId = world.provider.dimensionId;
        if (dimensionId >= -1 && dimensionId <= 1) {
            int n = dimensionId + 1;
            float[][] floatses = lightMapsColorsRgb[n];
            if (floatses == null) {
                return false;
            }

            int n2 = lightMapsHeight[n];
            if (flag && n2 < 64) {
                return false;
            }

            int n3 = floatses.length / n2;
            if (n3 < 16) {
                Config.warn("Invalid lightmap width: " + n3 + " for: /environment/lightmap" + dimensionId + ".png");
                lightMapsColorsRgb[n] = (float[][])null;
                return false;
            }

            int n4 = 0;
            if (flag) {
                n4 = n3 * 16 * 2;
            }

            float f2 = 1.1666666F * (world.getSunBrightness(1.0F) - 0.2F);
            if (world.lastLightningBolt > 0) {
                f2 = 1.0F;
            }

            f2 = Config.limitTo1(f2);
            float f3 = f2 * (n3 - 1);
            float f4 = Config.limitTo1(f + 0.5F) * (n3 - 1);
            float limitTo1 = Config.limitTo1(Config.getGameSettings().getGammaSetting());
            boolean flag2 = limitTo1 > 1.0E-4F;
            getLightMapColumn(floatses, f3, n4, n3, sunRgbs);
            getLightMapColumn(floatses, f4, n4 + 16 * n3, n3, torchRgbs);
            float[] floats = new float[3];

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 3; k++) {
                        float limitTo12 = Config.limitTo1(sunRgbs[i][k] + torchRgbs[j][k]);
                        if (flag2) {
                            float f5 = 1.0F - limitTo12;
                            f5 = 1.0F - f5 * f5 * f5 * f5;
                            limitTo12 = limitTo1 * f5 + (1.0F - limitTo1) * limitTo12;
                        }

                        floats[k] = limitTo12;
                    }

                    int n5 = (int)(floats[0] * 255.0F);
                    int n6 = (int)(floats[1] * 255.0F);
                    int n7 = (int)(floats[2] * 255.0F);
                    ints[i * 16 + j] = 0xFF000000 | n5 << 16 | n6 << 8 | n7;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static void getLightMapColumn(float[][] floatses, float f, int n, int n2, float[][] floatses2) {
        int floor = (int)Math.floor(f);
        int ceil = (int)Math.ceil(f);
        if (floor == ceil) {
            for (int i = 0; i < 16; i++) {
                float[] floats = floatses[n + i * n2 + floor];
                float[] floats2 = floatses2[i];

                for (int j = 0; j < 3; j++) {
                    floats2[j] = floats[j];
                }
            }
        } else {
            float f2 = 1.0F - (f - floor);
            float f3 = 1.0F - (ceil - f);

            for (int i = 0; i < 16; i++) {
                float[] floats = floatses[n + i * n2 + floor];
                float[] floats2 = floatses[n + i * n2 + ceil];
                float[] floats3 = floatses2[i];

                for (int j = 0; j < 3; j++) {
                    floats3[j] = floats[j] * f2 + floats2[j] * f3;
                }
            }
        }
    }

    public static Vec3 getWorldFogColor(Vec3 vec3, WorldClient worldClient, float f) {
        int dimensionId = worldClient.provider.dimensionId;
        switch (dimensionId) {
            case -1:
                vec3 = getFogColorNether(vec3);
                break;
            case 0:
                Minecraft minecraft = Minecraft.getMinecraft();
                vec3 = getFogColor(vec3, minecraft.theWorld, minecraft.renderViewEntity.posX, minecraft.renderViewEntity.posY + 1.0, minecraft.renderViewEntity.posZ);
                break;
            case 1:
                vec3 = getFogColorEnd(vec3);
        }

        return vec3;
    }

    public static Vec3 getWorldSkyColor(Vec3 vec3, World world, Entity entity, float f) {
        int dimensionId = world.provider.dimensionId;
        switch (dimensionId) {
            case 0:
                Minecraft minecraft = Minecraft.getMinecraft();
                vec3 = getSkyColor(vec3, minecraft.theWorld, minecraft.renderViewEntity.posX, minecraft.renderViewEntity.posY + 1.0, minecraft.renderViewEntity.posZ);
                break;
            case 1:
                vec3 = getSkyColorEnd(vec3);
        }

        return vec3;
    }

    public static void dbg(String str) {
        Config.dbg("CustomColors: " + str);
    }

    public static void warn(String str) {
        Config.warn("CustomColors: " + str);
    }
}
