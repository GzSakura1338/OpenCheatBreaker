package net.optifine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedTextures {
    public static ConnectedProperties[][] blockProperties = (ConnectedProperties[][])null;
    public static ConnectedProperties[][] tileProperties = (ConnectedProperties[][])null;
    public static boolean multipass = false;
    public static String[] propSuffixes = new String[]{
        "", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };
    public static int[] ctmIndexes = new int[]{
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        0,
        0,
        0,
        0,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        0,
        0,
        0,
        0,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        0,
        0,
        0,
        0,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        0,
        0,
        0,
        0,
        0
    };
    public static final int CONSTANT_5 = 5;
    public static final int CONSTANT_3 = 3;
    public static final int CONSTANT_3_2 = 3;
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_2 = 2;
    public static final int CONSTANT_0_2 = 0;
    public static final int CONSTANT_2_2 = 2;
    public static final int CONSTANT_1 = 1;
    public static final int CONSTANT_2_3 = 2;
    public static final int CONSTANT_4 = 4;
    public static final int CONSTANT_5_2 = 5;
    public static final int CONSTANT_1_2 = 1;
    public static final int CONSTANT_0_3 = 0;
    public static final int CONSTANT_1_3 = 1;
    public static final int CONSTANT_4_2 = 4;

    public static IIcon getConnectedTexture(IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon) {
        if (blockAccess == null) {
            return icon;
        }

        IIcon connectedTextureSingle = getConnectedTextureSingle(blockAccess, block, n, n2, n3, n4, icon, true);
        if (!multipass) {
            return connectedTextureSingle;
        }

        if (connectedTextureSingle == icon) {
            return connectedTextureSingle;
        }

        IIcon icon2 = connectedTextureSingle;

        for (int i = 0; i < 3; i++) {
            IIcon connectedTextureSingle2 = getConnectedTextureSingle(blockAccess, block, n, n2, n3, n4, icon2, false);
            if (connectedTextureSingle2 == icon2) {
                break;
            }

            icon2 = connectedTextureSingle2;
        }

        return icon2;
    }

    public static IIcon getConnectedTextureSingle(IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon, boolean flag) {
        if (!(icon instanceof TextureAtlasSprite)) {
            return icon;
        }

        TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)icon;
        int indexInMap = textureAtlasSprite.getIndexInMap();
        int n5 = -1;
        if (tileProperties != null && Tessellator.instance.defaultTexture && indexInMap >= 0 && indexInMap < tileProperties.length) {
            ConnectedProperties[] connectedPropertieses = tileProperties[indexInMap];
            if (connectedPropertieses != null) {
                if (n5 < 0) {
                    n5 = blockAccess.getBlockMetadata(n, n2, n3);
                }

                IIcon connectedTexture = getConnectedTexture(connectedPropertieses, blockAccess, block, n, n2, n3, n4, textureAtlasSprite, n5);
                if (connectedTexture != null) {
                    return connectedTexture;
                }
            }
        }

        if (blockProperties != null && flag) {
            int idFromBlock = Block.getIdFromBlock(block);
            if (idFromBlock >= 0 && idFromBlock < blockProperties.length) {
                ConnectedProperties[] connectedPropertieses = blockProperties[idFromBlock];
                if (connectedPropertieses != null) {
                    if (n5 < 0) {
                        n5 = blockAccess.getBlockMetadata(n, n2, n3);
                    }

                    IIcon connectedTexture = getConnectedTexture(connectedPropertieses, blockAccess, block, n, n2, n3, n4, textureAtlasSprite, n5);
                    if (connectedTexture != null) {
                        return connectedTexture;
                    }
                }
            }
        }

        return icon;
    }

    public static ConnectedProperties getConnectedProperties(IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon) {
        if (blockAccess == null) {
            return null;
        }

        if (!(icon instanceof TextureAtlasSprite)) {
            return null;
        }

        TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)icon;
        int indexInMap = textureAtlasSprite.getIndexInMap();
        int n5 = -1;
        if (tileProperties != null && Tessellator.instance.defaultTexture && indexInMap >= 0 && indexInMap < tileProperties.length) {
            ConnectedProperties[] connectedPropertieses = tileProperties[indexInMap];
            if (connectedPropertieses != null) {
                if (n5 < 0) {
                    n5 = blockAccess.getBlockMetadata(n, n2, n3);
                }

                ConnectedProperties connectedProperties = getConnectedProperties(connectedPropertieses, blockAccess, block, n, n2, n3, n4, textureAtlasSprite, n5);
                if (connectedProperties != null) {
                    return connectedProperties;
                }
            }
        }

        if (blockProperties != null) {
            int idFromBlock = Block.getIdFromBlock(block);
            if (idFromBlock >= 0 && idFromBlock < blockProperties.length) {
                ConnectedProperties[] connectedPropertieses = blockProperties[idFromBlock];
                if (connectedPropertieses != null) {
                    if (n5 < 0) {
                        n5 = blockAccess.getBlockMetadata(n, n2, n3);
                    }

                    ConnectedProperties connectedProperties = getConnectedProperties(connectedPropertieses, blockAccess, block, n, n2, n3, n4, textureAtlasSprite, n5);
                    if (connectedProperties != null) {
                        return connectedProperties;
                    }
                }
            }
        }

        return null;
    }

    public static IIcon getConnectedTexture(
        ConnectedProperties[] connectedPropertieses, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon, int n5
    ) {
        for (int i = 0; i < connectedPropertieses.length; i++) {
            ConnectedProperties connectedProperties = connectedPropertieses[i];
            if (connectedProperties != null) {
                IIcon connectedTexture = getConnectedTexture(connectedProperties, blockAccess, block, n, n2, n3, n4, icon, n5);
                if (connectedTexture != null) {
                    return connectedTexture;
                }
            }
        }

        return null;
    }

    public static ConnectedProperties getConnectedProperties(
        ConnectedProperties[] connectedPropertieses, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon, int n5
    ) {
        for (int i = 0; i < connectedPropertieses.length; i++) {
            ConnectedProperties connectedProperties = connectedPropertieses[i];
            if (connectedProperties != null) {
                IIcon connectedTexture = getConnectedTexture(connectedProperties, blockAccess, block, n, n2, n3, n4, icon, n5);
                if (connectedTexture != null) {
                    return connectedProperties;
                }
            }
        }

        return null;
    }

    public static IIcon getConnectedTexture(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon, int n5
    ) {
        if (connectedProperties.heights != null && !connectedProperties.heights.isInRange(n2)) {
            return null;
        }

        if (connectedProperties.biomes != null) {
            BiomeGenBase biomeGenForCoords = blockAccess.getBiomeGenForCoords(n, n3);
            boolean flag = false;

            for (int i = 0; i < connectedProperties.biomes.length; i++) {
                BiomeGenBase biomeGenBase = connectedProperties.biomes[i];
                if (biomeGenForCoords == biomeGenBase) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return null;
            }
        }

        int n6 = 0;
        int n7 = n5;
        if (block instanceof BlockRotatedPillar) {
            n6 = getWoodAxis(n4, n5);
            n7 = n5 & 3;
        }

        if (block instanceof BlockQuartz) {
            n6 = getQuartzAxis(n4, n5);
            if (n7 > 2) {
                n7 = 2;
            }
        }

        if (n4 >= 0 && connectedProperties.faces != 63) {
            int n8 = n4;
            if (n6 != 0) {
                n8 = fixSideByAxis(n4, n6);
            }

            if ((1 << n8 & connectedProperties.faces) == 0) {
                return null;
            }
        }

        if (connectedProperties.metadatas != null) {
            int[] metadatas = connectedProperties.metadatas;
            boolean flag = false;

            for (int i = 0; i < metadatas.length; i++) {
                if (metadatas[i] == n7) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return null;
            }
        }

        switch (connectedProperties.method) {
            case 1:
                return getConnectedTextureCtm(connectedProperties, blockAccess, block, n, n2, n3, n4, icon, n5);
            case 2:
                return getConnectedTextureHorizontal(connectedProperties, blockAccess, block, n, n2, n3, n6, n4, icon, n5);
            case 3:
                return getConnectedTextureTop(connectedProperties, blockAccess, block, n, n2, n3, n6, n4, icon, n5);
            case 4:
                return getConnectedTextureRandom(connectedProperties, blockAccess, block, n, n2, n3, n4);
            case 5:
                return getConnectedTextureRepeat(connectedProperties, n, n2, n3, n4);
            case 6:
                return getConnectedTextureVertical(connectedProperties, blockAccess, block, n, n2, n3, n6, n4, icon, n5);
            case 7:
                return getConnectedTextureFixed(connectedProperties);
            case 8:
                return getConnectedTextureHorizontalVertical(connectedProperties, blockAccess, block, n, n2, n3, n6, n4, icon, n5);
            case 9:
                return getConnectedTextureVerticalHorizontal(connectedProperties, blockAccess, block, n, n2, n3, n6, n4, icon, n5);
            default:
                return null;
        }
    }

    public static int fixSideByAxis(int n, int n2) {
        switch (n2) {
            case 0:
                return n;
            case 1:
                switch (n) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 1;
                    case 3:
                        return 0;
                    default:
                        return n;
                }
            case 2:
                switch (n) {
                    case 0:
                        return 4;
                    case 1:
                        return 5;
                    case 2:
                    case 3:
                    default:
                        return n;
                    case 4:
                        return 1;
                    case 5:
                        return 0;
                }
            default:
                return n;
        }
    }

    public static int getWoodAxis(int n, int n2) {
        int n3 = (n2 & 12) >> 2;
        switch (n3) {
            case 1:
                return 2;
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    public static int getQuartzAxis(int n, int n2) {
        switch (n2) {
            case 3:
                return 2;
            case 4:
                return 1;
            default:
                return 0;
        }
    }

    public static IIcon getConnectedTextureRandom(ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4) {
        if (connectedProperties.tileIcons.length == 1) {
            return connectedProperties.tileIcons[0];
        }

        int n5 = n4 / connectedProperties.symmetry * connectedProperties.symmetry;
        if (connectedProperties.linked) {
            int n6 = n2 - 1;

            for (Block block2 = blockAccess.getBlock(n, n6, n3); block2 == block; block2 = blockAccess.getBlock(n, n6, n3)) {
                n2 = n6--;
                if (n6 < 0) {
                    break;
                }
            }
        }

        int n6 = Config.getRandom(n, n2, n3, n5) & 2147483647;
        int n7 = 0;
        if (connectedProperties.weights == null) {
            n7 = n6 % connectedProperties.tileIcons.length;
        } else {
            int n8 = n6 % connectedProperties.sumAllWeights;
            int[] sumWeights = connectedProperties.sumWeights;

            for (int i = 0; i < sumWeights.length; i++) {
                if (n8 < sumWeights[i]) {
                    n7 = i;
                    break;
                }
            }
        }

        return connectedProperties.tileIcons[n7];
    }

    public static IIcon getConnectedTextureFixed(ConnectedProperties connectedProperties) {
        return connectedProperties.tileIcons[0];
    }

    public static IIcon getConnectedTextureRepeat(ConnectedProperties connectedProperties, int n, int n2, int n3, int n4) {
        if (connectedProperties.tileIcons.length == 1) {
            return connectedProperties.tileIcons[0];
        }

        int n5 = 0;
        int n6 = 0;
        switch (n4) {
            case 0:
                n5 = n;
                n6 = n3;
                break;
            case 1:
                n5 = n;
                n6 = n3;
                break;
            case 2:
                n5 = -n - 1;
                n6 = -n2;
                break;
            case 3:
                n5 = n;
                n6 = -n2;
                break;
            case 4:
                n5 = n3;
                n6 = -n2;
                break;
            case 5:
                n5 = -n3 - 1;
                n6 = -n2;
        }

        n5 %= connectedProperties.width;
        n6 %= connectedProperties.height;
        if (n5 < 0) {
            n5 += connectedProperties.width;
        }

        if (n6 < 0) {
            n6 += connectedProperties.height;
        }

        int n7 = n6 * connectedProperties.width + n5;
        return connectedProperties.tileIcons[n7];
    }

    public static IIcon getConnectedTextureCtm(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon, int n5
    ) {
        boolean[] flags = new boolean[6];
        switch (n4) {
            case 0:
            case 1:
                flags[0] = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n4, icon, n5);
                flags[1] = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n4, icon, n5);
                flags[2] = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n4, icon, n5);
                flags[3] = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n4, icon, n5);
                break;
            case 2:
                flags[0] = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n4, icon, n5);
                flags[1] = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n4, icon, n5);
                flags[2] = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n4, icon, n5);
                flags[3] = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n4, icon, n5);
                break;
            case 3:
                flags[0] = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n4, icon, n5);
                flags[1] = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n4, icon, n5);
                flags[2] = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n4, icon, n5);
                flags[3] = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n4, icon, n5);
                break;
            case 4:
                flags[0] = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n4, icon, n5);
                flags[1] = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n4, icon, n5);
                flags[2] = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n4, icon, n5);
                flags[3] = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n4, icon, n5);
                break;
            case 5:
                flags[0] = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n4, icon, n5);
                flags[1] = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n4, icon, n5);
                flags[2] = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n4, icon, n5);
                flags[3] = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n4, icon, n5);
        }

        byte b = 0;
        if (flags[0] & !flags[1] & !flags[2] & !flags[3]) {
            b = 3;
        } else if (!flags[0] & flags[1] & !flags[2] & !flags[3]) {
            b = 1;
        } else if (!flags[0] & !flags[1] & flags[2] & !flags[3]) {
            b = 12;
        } else if (!flags[0] & !flags[1] & !flags[2] & flags[3]) {
            b = 36;
        } else if (flags[0] & flags[1] & !flags[2] & !flags[3]) {
            b = 2;
        } else if (!flags[0] & !flags[1] & flags[2] & flags[3]) {
            b = 24;
        } else if (flags[0] & !flags[1] & flags[2] & !flags[3]) {
            b = 15;
        } else if (flags[0] & !flags[1] & !flags[2] & flags[3]) {
            b = 39;
        } else if (!flags[0] & flags[1] & flags[2] & !flags[3]) {
            b = 13;
        } else if (!flags[0] & flags[1] & !flags[2] & flags[3]) {
            b = 37;
        } else if (!flags[0] & flags[1] & flags[2] & flags[3]) {
            b = 25;
        } else if (flags[0] & !flags[1] & flags[2] & flags[3]) {
            b = 27;
        } else if (flags[0] & flags[1] & !flags[2] & flags[3]) {
            b = 38;
        } else if (flags[0] & flags[1] & flags[2] & !flags[3]) {
            b = 14;
        } else if (flags[0] & flags[1] & flags[2] & flags[3]) {
            b = 26;
        }

        if (b == 0) {
            return connectedProperties.tileIcons[b];
        }

        if (!Config.isConnectedTexturesFancy()) {
            return connectedProperties.tileIcons[b];
        }

        boolean[] flags2 = new boolean[6];
        switch (n4) {
            case 0:
            case 1:
                flags2[0] = !isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3 + 1, n4, icon, n5);
                flags2[1] = !isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3 + 1, n4, icon, n5);
                flags2[2] = !isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3 - 1, n4, icon, n5);
                flags2[3] = !isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3 - 1, n4, icon, n5);
                break;
            case 2:
                flags2[0] = !isNeighbour(connectedProperties, blockAccess, block, n - 1, n2 - 1, n3, n4, icon, n5);
                flags2[1] = !isNeighbour(connectedProperties, blockAccess, block, n + 1, n2 - 1, n3, n4, icon, n5);
                flags2[2] = !isNeighbour(connectedProperties, blockAccess, block, n - 1, n2 + 1, n3, n4, icon, n5);
                flags2[3] = !isNeighbour(connectedProperties, blockAccess, block, n + 1, n2 + 1, n3, n4, icon, n5);
                break;
            case 3:
                flags2[0] = !isNeighbour(connectedProperties, blockAccess, block, n + 1, n2 - 1, n3, n4, icon, n5);
                flags2[1] = !isNeighbour(connectedProperties, blockAccess, block, n - 1, n2 - 1, n3, n4, icon, n5);
                flags2[2] = !isNeighbour(connectedProperties, blockAccess, block, n + 1, n2 + 1, n3, n4, icon, n5);
                flags2[3] = !isNeighbour(connectedProperties, blockAccess, block, n - 1, n2 + 1, n3, n4, icon, n5);
                break;
            case 4:
                flags2[0] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3 + 1, n4, icon, n5);
                flags2[1] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3 - 1, n4, icon, n5);
                flags2[2] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3 + 1, n4, icon, n5);
                flags2[3] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3 - 1, n4, icon, n5);
                break;
            case 5:
                flags2[0] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3 - 1, n4, icon, n5);
                flags2[1] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3 + 1, n4, icon, n5);
                flags2[2] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3 - 1, n4, icon, n5);
                flags2[3] = !isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3 + 1, n4, icon, n5);
        }

        if (b == 13 && flags2[0]) {
            b = 4;
        } else if (b == 15 && flags2[1]) {
            b = 5;
        } else if (b == 37 && flags2[2]) {
            b = 16;
        } else if (b == 39 && flags2[3]) {
            b = 17;
        } else if (b == 14 && flags2[0] && flags2[1]) {
            b = 7;
        } else if (b == 25 && flags2[0] && flags2[2]) {
            b = 6;
        } else if (b == 27 && flags2[3] && flags2[1]) {
            b = 19;
        } else if (b == 38 && flags2[3] && flags2[2]) {
            b = 18;
        } else if (b == 14 && !flags2[0] && flags2[1]) {
            b = 31;
        } else if (b == 25 && flags2[0] && !flags2[2]) {
            b = 30;
        } else if (b == 27 && !flags2[3] && flags2[1]) {
            b = 41;
        } else if (b == 38 && flags2[3] && !flags2[2]) {
            b = 40;
        } else if (b == 14 && flags2[0] && !flags2[1]) {
            b = 29;
        } else if (b == 25 && !flags2[0] && flags2[2]) {
            b = 28;
        } else if (b == 27 && flags2[3] && !flags2[1]) {
            b = 43;
        } else if (b == 38 && !flags2[3] && flags2[2]) {
            b = 42;
        } else if (b == 26 && flags2[0] && flags2[1] && flags2[2] && flags2[3]) {
            b = 46;
        } else if (b == 26 && !flags2[0] && flags2[1] && flags2[2] && flags2[3]) {
            b = 9;
        } else if (b == 26 && flags2[0] && !flags2[1] && flags2[2] && flags2[3]) {
            b = 21;
        } else if (b == 26 && flags2[0] && flags2[1] && !flags2[2] && flags2[3]) {
            b = 8;
        } else if (b == 26 && flags2[0] && flags2[1] && flags2[2] && !flags2[3]) {
            b = 20;
        } else if (b == 26 && flags2[0] && flags2[1] && !flags2[2] && !flags2[3]) {
            b = 11;
        } else if (b == 26 && !flags2[0] && !flags2[1] && flags2[2] && flags2[3]) {
            b = 22;
        } else if (b == 26 && !flags2[0] && flags2[1] && !flags2[2] && flags2[3]) {
            b = 23;
        } else if (b == 26 && flags2[0] && !flags2[1] && flags2[2] && !flags2[3]) {
            b = 10;
        } else if (b == 26 && flags2[0] && !flags2[1] && !flags2[2] && flags2[3]) {
            b = 34;
        } else if (b == 26 && !flags2[0] && flags2[1] && flags2[2] && !flags2[3]) {
            b = 35;
        } else if (b == 26 && flags2[0] && !flags2[1] && !flags2[2] && !flags2[3]) {
            b = 32;
        } else if (b == 26 && !flags2[0] && flags2[1] && !flags2[2] && !flags2[3]) {
            b = 33;
        } else if (b == 26 && !flags2[0] && !flags2[1] && flags2[2] && !flags2[3]) {
            b = 44;
        } else if (b == 26 && !flags2[0] && !flags2[1] && !flags2[2] && flags2[3]) {
            b = 45;
        }

        return connectedProperties.tileIcons[b];
    }

    public static boolean isNeighbour(ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, IIcon icon, int n5) {
        Block block2 = blockAccess.getBlock(n, n2, n3);
        if (connectedProperties.connect == 2) {
            if (block2 == null) {
                return false;
            }

            int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
            IIcon icon2;
            if (n4 >= 0) {
                icon2 = block2.getIcon(n4, blockMetadata);
            } else {
                icon2 = block2.getIcon(1, blockMetadata);
            }

            return icon2 == icon;
        } else {
            return connectedProperties.connect == 3
                ? (block2 == null ? false : block2.getMaterial() == block.getMaterial())
                : block2 == block && blockAccess.getBlockMetadata(n, n2, n3) == n5;
        }
    }

    public static IIcon getConnectedTextureHorizontal(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, int n5, IIcon icon, int n6
    ) {
        boolean flag;
        boolean flag2;
        flag = false;
        flag2 = false;
        label43:
        switch (n4) {
            case 0:
                switch (n5) {
                    case 0:
                    case 1:
                        return null;
                    case 2:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n5, icon, n6);
                        break label43;
                    case 3:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n5, icon, n6);
                        break label43;
                    case 4:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n5, icon, n6);
                        break label43;
                    case 5:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n5, icon, n6);
                    default:
                        break label43;
                }
            case 1:
                switch (n5) {
                    case 0:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n5, icon, n6);
                        break label43;
                    case 1:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n5, icon, n6);
                        break label43;
                    case 2:
                    case 3:
                        return null;
                    case 4:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n5, icon, n6);
                        break label43;
                    case 5:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n5, icon, n6);
                    default:
                        break label43;
                }
            case 2:
                switch (n5) {
                    case 0:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n5, icon, n6);
                        break;
                    case 1:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n5, icon, n6);
                        break;
                    case 2:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n5, icon, n6);
                        break;
                    case 3:
                        flag = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n5, icon, n6);
                        flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n5, icon, n6);
                        break;
                    case 4:
                    case 5:
                        return null;
                }
        }

        boolean flag3 = true;
        byte b;
        if (flag) {
            if (flag2) {
                b = 1;
            } else {
                b = 2;
            }
        } else if (flag2) {
            b = 0;
        } else {
            b = 3;
        }

        return connectedProperties.tileIcons[b];
    }

    public static IIcon getConnectedTextureVertical(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, int n5, IIcon icon, int n6
    ) {
        boolean flag = false;
        boolean flag2 = false;
        switch (n4) {
            case 0:
                if (n5 == 1 || n5 == 0) {
                    return null;
                }

                flag = isNeighbour(connectedProperties, blockAccess, block, n, n2 - 1, n3, n5, icon, n6);
                flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n5, icon, n6);
                break;
            case 1:
                if (n5 == 3 || n5 == 2) {
                    return null;
                }

                flag = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 - 1, n5, icon, n6);
                flag2 = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n5, icon, n6);
                break;
            case 2:
                if (n5 == 5 || n5 == 4) {
                    return null;
                }

                flag = isNeighbour(connectedProperties, blockAccess, block, n - 1, n2, n3, n5, icon, n6);
                flag2 = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n5, icon, n6);
        }

        boolean flag3 = true;
        byte b;
        if (flag) {
            if (flag2) {
                b = 1;
            } else {
                b = 2;
            }
        } else if (flag2) {
            b = 0;
        } else {
            b = 3;
        }

        return connectedProperties.tileIcons[b];
    }

    public static IIcon getConnectedTextureHorizontalVertical(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, int n5, IIcon icon, int n6
    ) {
        IIcon[] tileIcons = connectedProperties.tileIcons;
        IIcon connectedTextureHorizontal = getConnectedTextureHorizontal(connectedProperties, blockAccess, block, n, n2, n3, n4, n5, icon, n6);
        if (connectedTextureHorizontal != null && connectedTextureHorizontal != icon && connectedTextureHorizontal != tileIcons[3]) {
            return connectedTextureHorizontal;
        }

        IIcon connectedTextureVertical = getConnectedTextureVertical(connectedProperties, blockAccess, block, n, n2, n3, n4, n5, icon, n6);
        return connectedTextureVertical == tileIcons[0] ? tileIcons[4] : (connectedTextureVertical == tileIcons[1] ? tileIcons[5] : (connectedTextureVertical == tileIcons[2] ? tileIcons[6] : connectedTextureVertical));
    }

    public static IIcon getConnectedTextureVerticalHorizontal(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, int n5, IIcon icon, int n6
    ) {
        IIcon[] tileIcons = connectedProperties.tileIcons;
        IIcon connectedTextureVertical = getConnectedTextureVertical(connectedProperties, blockAccess, block, n, n2, n3, n4, n5, icon, n6);
        if (connectedTextureVertical != null && connectedTextureVertical != icon && connectedTextureVertical != tileIcons[3]) {
            return connectedTextureVertical;
        }

        IIcon connectedTextureHorizontal = getConnectedTextureHorizontal(connectedProperties, blockAccess, block, n, n2, n3, n4, n5, icon, n6);
        return connectedTextureHorizontal == tileIcons[0] ? tileIcons[4] : (connectedTextureHorizontal == tileIcons[1] ? tileIcons[5] : (connectedTextureHorizontal == tileIcons[2] ? tileIcons[6] : connectedTextureHorizontal));
    }

    public static IIcon getConnectedTextureTop(
        ConnectedProperties connectedProperties, IBlockAccess blockAccess, Block block, int n, int n2, int n3, int n4, int n5, IIcon icon, int n6
    ) {
        boolean flag = false;
        switch (n4) {
            case 0:
                if (n5 == 1 || n5 == 0) {
                    return null;
                }

                flag = isNeighbour(connectedProperties, blockAccess, block, n, n2 + 1, n3, n5, icon, n6);
                break;
            case 1:
                if (n5 == 3 || n5 == 2) {
                    return null;
                }

                flag = isNeighbour(connectedProperties, blockAccess, block, n, n2, n3 + 1, n5, icon, n6);
                break;
            case 2:
                if (n5 == 5 || n5 == 4) {
                    return null;
                }

                flag = isNeighbour(connectedProperties, blockAccess, block, n + 1, n2, n3, n5, icon, n6);
        }

        return flag ? connectedProperties.tileIcons[0] : null;
    }

    public static void updateIcons(TextureMap textureMap) {
        blockProperties = (ConnectedProperties[][])null;
        tileProperties = (ConnectedProperties[][])null;
        IResourcePack[] resourcePacks = Config.getResourcePacks();

        for (int i = resourcePacks.length - 1; i >= 0; i--) {
            IResourcePack resourcePack = resourcePacks[i];
            updateIcons(textureMap, resourcePack);
        }

        updateIcons(textureMap, Config.getDefaultResourcePack());
    }

    public static void updateIcons(TextureMap textureMap, IResourcePack resourcePack) {
        String[] collectFiles2 = collectFiles(resourcePack, "mcpatcher/ctm/", ".properties");
        Arrays.sort(collectFiles2);
        List makePropertyList2 = makePropertyList(tileProperties);
        List makePropertyList3 = makePropertyList(blockProperties);

        for (int i = 0; i < collectFiles2.length; i++) {
            String str = collectFiles2[i];
            Config.dbg("ConnectedTextures: " + str);

            try {
                ResourceLocation resourceLocation = new ResourceLocation(str);
                InputStream inputStream = resourcePack.getInputStream(resourceLocation);
                if (inputStream == null) {
                    Config.warn("ConnectedTextures file not found: " + str);
                } else {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    ConnectedProperties connectedProperties = new ConnectedProperties(properties, str);
                    if (connectedProperties.isValid(str)) {
                        connectedProperties.updateIcons(textureMap);
                        addToTileList(connectedProperties, makePropertyList2);
                        addToBlockList(connectedProperties, makePropertyList3);
                    }
                }
            } catch (FileNotFoundException e) {
                Config.warn("ConnectedTextures file not found: " + str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        blockProperties = propertyListToArray(makePropertyList3);
        tileProperties = propertyListToArray(makePropertyList2);
        multipass = detectMultipass();
        Config.dbg("Multipass connected textures: " + multipass);
    }

    public static List makePropertyList(ConnectedProperties[][] connectedPropertieseses) {
        ArrayList list = new ArrayList();
        if (connectedPropertieseses != null) {
            for (int i = 0; i < connectedPropertieseses.length; i++) {
                ConnectedProperties[] connectedPropertieses = connectedPropertieseses[i];
                ArrayList list2 = null;
                if (connectedPropertieses != null) {
                    list2 = new ArrayList<>(Arrays.asList(connectedPropertieses));
                }

                list.add(list2);
            }
        }

        return list;
    }

    public static boolean detectMultipass() {
        ArrayList list = new ArrayList();

        for (int i = 0; i < tileProperties.length; i++) {
            ConnectedProperties[] connectedPropertieses = tileProperties[i];
            if (connectedPropertieses != null) {
                list.addAll(Arrays.asList(connectedPropertieses));
            }
        }

        for (int i = 0; i < blockProperties.length; i++) {
            ConnectedProperties[] connectedPropertieses = blockProperties[i];
            if (connectedPropertieses != null) {
                list.addAll(Arrays.asList(connectedPropertieses));
            }
        }

        ConnectedProperties[] toArray = (ConnectedProperties[])list.toArray(new ConnectedProperties[list.size()]);
        HashSet set = new HashSet();
        HashSet set2 = new HashSet();

        for (int i = 0; i < toArray.length; i++) {
            ConnectedProperties connectedProperties = toArray[i];
            if (connectedProperties.matchTileIcons != null) {
                set.addAll(Arrays.asList(connectedProperties.matchTileIcons));
            }

            if (connectedProperties.tileIcons != null) {
                set2.addAll(Arrays.asList(connectedProperties.tileIcons));
            }
        }

        set.retainAll(set2);
        return !set.isEmpty();
    }

    public static ConnectedProperties[][] propertyListToArray(List list) {
        ConnectedProperties[][] connectedPropertieseses = new ConnectedProperties[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            List list2 = (List)list.get(i);
            if (list2 != null) {
                ConnectedProperties[] toArray = (ConnectedProperties[])list2.toArray(new ConnectedProperties[list2.size()]);
                connectedPropertieseses[i] = toArray;
            }
        }

        return connectedPropertieseses;
    }

    public static void addToTileList(ConnectedProperties connectedProperties, List list) {
        if (connectedProperties.matchTileIcons != null) {
            for (int i = 0; i < connectedProperties.matchTileIcons.length; i++) {
                IIcon icon = connectedProperties.matchTileIcons[i];
                if (!(icon instanceof TextureAtlasSprite)) {
                    Config.warn("IIcon is not TextureAtlasSprite: " + icon + ", name: " + icon.getIconName());
                } else {
                    TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)icon;
                    int indexInMap = textureAtlasSprite.getIndexInMap();
                    if (indexInMap < 0) {
                        Config.warn("Invalid tile ID: " + indexInMap + ", icon: " + textureAtlasSprite.getIconName());
                    } else {
                        addToList(connectedProperties, list, indexInMap);
                    }
                }
            }
        }
    }

    public static void addToBlockList(ConnectedProperties connectedProperties, List list) {
        if (connectedProperties.matchBlocks != null) {
            for (int i = 0; i < connectedProperties.matchBlocks.length; i++) {
                int n = connectedProperties.matchBlocks[i];
                if (n < 0) {
                    Config.warn("Invalid block ID: " + n);
                } else {
                    addToList(connectedProperties, list, n);
                }
            }
        }
    }

    public static void addToList(ConnectedProperties connectedProperties, List list, int n) {
        while (n >= list.size()) {
            list.add(null);
        }

        List list2 = (List)list.get(n);
        if (list2 == null) {
            list2 = new ArrayList();
            list.set(n, list2);
        }

        list2.add(connectedProperties);
    }

    public static String[] collectFiles(IResourcePack resourcePack, String str, String str2) {
        if (resourcePack instanceof DefaultResourcePack) {
            return collectFilesDefault(resourcePack);
        }

        if (!(resourcePack instanceof AbstractResourcePack)) {
            return new String[0];
        }

        AbstractResourcePack abstractResourcePack = (AbstractResourcePack)resourcePack;
        File resourcePackFile = ResourceUtils.getResourcePackFile(abstractResourcePack);
        return resourcePackFile == null
            ? new String[0]
            : (resourcePackFile.isDirectory() ? collectFilesFolder(resourcePackFile, "", str, str2) : (resourcePackFile.isFile() ? collectFilesZIP(resourcePackFile, str, str2) : new String[0]));
    }

    public static String[] collectFilesDefault(IResourcePack resourcePack) {
        ArrayList list = new ArrayList();
        String[] defaultCtmPaths = getDefaultCtmPaths();

        for (int i = 0; i < defaultCtmPaths.length; i++) {
            String str = defaultCtmPaths[i];
            ResourceLocation resourceLocation = new ResourceLocation(str);
            if (resourcePack.resourceExists(resourceLocation)) {
                list.add(str);
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static String[] getDefaultCtmPaths() {
        ArrayList list = new ArrayList();
        String str = "mcpatcher/ctm/default/";
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
            list.add(str + "glass.properties");
            list.add(str + "glasspane.properties");
        }

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
            list.add(str + "bookshelf.properties");
        }

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
            list.add(str + "sandstone.properties");
        }

        String[] strs = new String[]{
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
        };

        for (int i = 0; i < strs.length; i++) {
            String str2 = strs[i];
            if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + str2 + ".png"))) {
                list.add(str + i + "_glass_" + str2 + "/glass_" + str2 + ".properties");
                list.add(str + i + "_glass_" + str2 + "/glass_pane_" + str2 + ".properties");
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static String[] collectFilesFolder(File file, String str, String str2, String str3) {
        ArrayList list = new ArrayList();
        String str4 = "assets/minecraft/";
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return new String[0];
        }

        for (int i = 0; i < listFiles.length; i++) {
            File file2 = listFiles[i];
            if (file2.isFile()) {
                String str5 = str + file2.getName();
                if (str5.startsWith(str4)) {
                    str5 = str5.substring(str4.length());
                    if (str5.startsWith(str2) && str5.endsWith(str3)) {
                        list.add(str5);
                    }
                }
            } else if (file2.isDirectory()) {
                String str5 = str + file2.getName() + "/";
                String[] collectFilesFolder2 = collectFilesFolder(file2, str5, str2, str3);

                for (int j = 0; j < collectFilesFolder2.length; j++) {
                    String str6 = collectFilesFolder2[j];
                    list.add(str6);
                }
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static String[] collectFilesZIP(File file, String str, String str2) {
        ArrayList list = new ArrayList();
        String str3 = "assets/minecraft/";

        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry nextElement = (ZipEntry)entries.nextElement();
                String name = nextElement.getName();
                if (name.startsWith(str3)) {
                    name = name.substring(str3.length());
                    if (name.startsWith(str) && name.endsWith(str2)) {
                        list.add(name);
                    }
                }
            }

            zipFile.close();
            return (String[])list.toArray(new String[list.size()]);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static int getPaneTextureIndex(boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        return flag2 && flag
            ? (flag3 ? (flag4 ? 34 : 50) : (flag4 ? 18 : 2))
            : (
                flag2 && !flag
                    ? (flag3 ? (flag4 ? 35 : 51) : (flag4 ? 19 : 3))
                    : (!flag2 && flag ? (flag3 ? (flag4 ? 33 : 49) : (flag4 ? 17 : 1)) : (flag3 ? (flag4 ? 32 : 48) : (flag4 ? 16 : 0)))
            );
    }

    public static int getReversePaneTextureIndex(int n) {
        int n2 = n % 16;
        return n2 == 1 ? n + 2 : (n2 == 3 ? n - 2 : n);
    }

    public static IIcon getCtmTexture(ConnectedProperties connectedProperties, int n, IIcon icon) {
        if (connectedProperties.method != 1) {
            return icon;
        } else if (n >= 0 && n < ctmIndexes.length) {
            int n2 = ctmIndexes[n];
            IIcon[] tileIcons = connectedProperties.tileIcons;
            return n2 >= 0 && n2 < tileIcons.length ? tileIcons[n2] : icon;
        } else {
            return icon;
        }
    }
}
