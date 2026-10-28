package net.optifine;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.biome.BiomeGenBase;

public class Matches {
    public static boolean block(int n, int n2, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }

        for (int i = 0; i < matchBlocks.length; i++) {
            MatchBlock matchBlock = matchBlocks[i];
            if (matchBlock.matches(n, n2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean blockId(int n, MatchBlock[] matchBlocks) {
        if (matchBlocks == null) {
            return true;
        }

        for (int i = 0; i < matchBlocks.length; i++) {
            MatchBlock matchBlock = matchBlocks[i];
            if (matchBlock.getBlockId() == n) {
                return true;
            }
        }

        return false;
    }

    public static boolean metadata(int n, int[] ints) {
        if (ints == null) {
            return true;
        }

        for (int i = 0; i < ints.length; i++) {
            if (ints[i] == n) {
                return true;
            }
        }

        return false;
    }

    public static boolean sprite(TextureAtlasSprite textureAtlasSprite, TextureAtlasSprite[] textureAtlasSprites) {
        if (textureAtlasSprites == null) {
            return true;
        }

        for (int i = 0; i < textureAtlasSprites.length; i++) {
            if (textureAtlasSprites[i] == textureAtlasSprite) {
                return true;
            }
        }

        return false;
    }

    public static boolean biome(BiomeGenBase biomeGenBase, BiomeGenBase[] biomeGenBases) {
        if (biomeGenBases == null) {
            return true;
        }

        for (int i = 0; i < biomeGenBases.length; i++) {
            if (biomeGenBases[i] == biomeGenBase) {
                return true;
            }
        }

        return false;
    }
}
