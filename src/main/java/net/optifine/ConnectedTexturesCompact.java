package net.optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.render.RenderEnv;

public class ConnectedTexturesCompact {
   public static final int COMPACT_NONE = 0;
   public static final int COMPACT_ALL = 1;
   public static final int COMPACT_V = 2;
   public static final int COMPACT_H = 3;
   public static final int COMPACT_HV = 4;

   public static BakedQuad[] getConnectedTextureCtmCompact(int n, ConnectedProperties connectedProperties, int n2, BakedQuad bakedQuad, RenderEnv renderEnv) {
      if (connectedProperties.ctmTileIndexes != null && n >= 0 && n < connectedProperties.ctmTileIndexes.length) {
         int n3 = connectedProperties.ctmTileIndexes[n];
         if (n3 >= 0 && n3 <= connectedProperties.tileIcons.length) {
            return getQuadsCompact(n3, connectedProperties.tileIcons, bakedQuad, renderEnv);
         }
      }

      switch (n) {
         case 1:
            return getQuadsCompactH(0, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 2:
            return getQuadsCompact(3, connectedProperties.tileIcons, bakedQuad, renderEnv);
         case 3:
            return getQuadsCompactH(3, 0, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 4:
            return getQuadsCompact4(0, 3, 2, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 5:
            return getQuadsCompact4(3, 0, 4, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 6:
            return getQuadsCompact4(2, 4, 2, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 7:
            return getQuadsCompact4(3, 3, 4, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 8:
            return getQuadsCompact4(4, 1, 4, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 9:
            return getQuadsCompact4(4, 4, 4, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 10:
            return getQuadsCompact4(1, 4, 1, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 11:
            return getQuadsCompact4(1, 1, 4, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 12:
            return getQuadsCompactV(0, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 13:
            return getQuadsCompact4(0, 3, 2, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 14:
            return getQuadsCompactV(3, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 15:
            return getQuadsCompact4(3, 0, 1, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 16:
            return getQuadsCompact4(2, 4, 0, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 17:
            return getQuadsCompact4(4, 2, 3, 0, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 18:
            return getQuadsCompact4(4, 4, 3, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 19:
            return getQuadsCompact4(4, 2, 4, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 20:
            return getQuadsCompact4(1, 4, 4, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 21:
            return getQuadsCompact4(4, 4, 1, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 22:
            return getQuadsCompact4(4, 4, 1, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 23:
            return getQuadsCompact4(4, 1, 4, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 24:
            return getQuadsCompact(2, connectedProperties.tileIcons, bakedQuad, renderEnv);
         case 25:
            return getQuadsCompactH(2, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 26:
            return getQuadsCompact(1, connectedProperties.tileIcons, bakedQuad, renderEnv);
         case 27:
            return getQuadsCompactH(1, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 28:
            return getQuadsCompact4(2, 4, 2, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 29:
            return getQuadsCompact4(3, 3, 1, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 30:
            return getQuadsCompact4(2, 1, 2, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 31:
            return getQuadsCompact4(3, 3, 4, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 32:
            return getQuadsCompact4(1, 1, 1, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 33:
            return getQuadsCompact4(1, 1, 4, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 34:
            return getQuadsCompact4(4, 1, 1, 4, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 35:
            return getQuadsCompact4(1, 4, 4, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 36:
            return getQuadsCompactV(2, 0, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 37:
            return getQuadsCompact4(2, 1, 0, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 38:
            return getQuadsCompactV(1, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 39:
            return getQuadsCompact4(1, 2, 3, 0, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 40:
            return getQuadsCompact4(4, 1, 3, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 41:
            return getQuadsCompact4(1, 2, 4, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 42:
            return getQuadsCompact4(1, 4, 3, 3, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 43:
            return getQuadsCompact4(4, 2, 1, 2, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 44:
            return getQuadsCompact4(1, 4, 1, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 45:
            return getQuadsCompact4(4, 1, 1, 1, connectedProperties.tileIcons, n2, bakedQuad, renderEnv);
         case 46:
            return getQuadsCompact(4, connectedProperties.tileIcons, bakedQuad, renderEnv);
         default:
            return getQuadsCompact(0, connectedProperties.tileIcons, bakedQuad, renderEnv);
      }
   }

   public static BakedQuad[] getQuadsCompactH(int n, int n2, TextureAtlasSprite[] textureAtlasSprites, int n3, BakedQuad bakedQuad, RenderEnv renderEnv) {
      return getQuadsCompact(ConnectedTexturesCompact.Dir.LEFT, n, ConnectedTexturesCompact.Dir.RIGHT, n2, textureAtlasSprites, n3, bakedQuad, renderEnv);
   }

   public static BakedQuad[] getQuadsCompactV(int n, int n2, TextureAtlasSprite[] textureAtlasSprites, int n3, BakedQuad bakedQuad, RenderEnv renderEnv) {
      return getQuadsCompact(ConnectedTexturesCompact.Dir.UP, n, ConnectedTexturesCompact.Dir.DOWN, n2, textureAtlasSprites, n3, bakedQuad, renderEnv);
   }

   public static BakedQuad[] getQuadsCompact4(int n, int n2, int n3, int n4, TextureAtlasSprite[] textureAtlasSprites, int n5, BakedQuad bakedQuad, RenderEnv renderEnv) {
      return n == n2
         ? (
            n3 == n4
               ? getQuadsCompact(ConnectedTexturesCompact.Dir.UP, n, ConnectedTexturesCompact.Dir.DOWN, n3, textureAtlasSprites, n5, bakedQuad, renderEnv)
               : getQuadsCompact(
                  ConnectedTexturesCompact.Dir.UP,
                  n,
                  ConnectedTexturesCompact.Dir.DOWN_LEFT,
                  n3,
                  ConnectedTexturesCompact.Dir.DOWN_RIGHT,
                  n4,
                  textureAtlasSprites,
                  n5,
                  bakedQuad,
                  renderEnv
               )
         )
         : (
            n3 == n4
               ? getQuadsCompact(
                  ConnectedTexturesCompact.Dir.UP_LEFT,
                  n,
                  ConnectedTexturesCompact.Dir.UP_RIGHT,
                  n2,
                  ConnectedTexturesCompact.Dir.DOWN,
                  n3,
                  textureAtlasSprites,
                  n5,
                  bakedQuad,
                  renderEnv
               )
               : (
                  n == n3
                     ? (
                        n2 == n4
                           ? getQuadsCompact(ConnectedTexturesCompact.Dir.LEFT, n, ConnectedTexturesCompact.Dir.RIGHT, n2, textureAtlasSprites, n5, bakedQuad, renderEnv)
                           : getQuadsCompact(
                              ConnectedTexturesCompact.Dir.LEFT,
                              n,
                              ConnectedTexturesCompact.Dir.UP_RIGHT,
                              n2,
                              ConnectedTexturesCompact.Dir.DOWN_RIGHT,
                              n4,
                              textureAtlasSprites,
                              n5,
                              bakedQuad,
                              renderEnv
                           )
                     )
                     : (
                        n2 == n4
                           ? getQuadsCompact(
                              ConnectedTexturesCompact.Dir.UP_LEFT,
                              n,
                              ConnectedTexturesCompact.Dir.DOWN_LEFT,
                              n3,
                              ConnectedTexturesCompact.Dir.RIGHT,
                              n2,
                              textureAtlasSprites,
                              n5,
                              bakedQuad,
                              renderEnv
                           )
                           : getQuadsCompact(
                              ConnectedTexturesCompact.Dir.UP_LEFT,
                              n,
                              ConnectedTexturesCompact.Dir.UP_RIGHT,
                              n2,
                              ConnectedTexturesCompact.Dir.DOWN_LEFT,
                              n3,
                              ConnectedTexturesCompact.Dir.DOWN_RIGHT,
                              n4,
                              textureAtlasSprites,
                              n5,
                              bakedQuad,
                              renderEnv
                           )
                     )
               )
         );
   }

   public static BakedQuad[] getQuadsCompact(int n, TextureAtlasSprite[] textureAtlasSprites, BakedQuad bakedQuad, RenderEnv renderEnv) {
      TextureAtlasSprite textureAtlasSprite = textureAtlasSprites[n];
      return ConnectedTextures.getQuads(textureAtlasSprite, bakedQuad, renderEnv);
   }

   public static BakedQuad[] getQuadsCompact(
      ConnectedTexturesCompact.Dir dir,
      int n,
      ConnectedTexturesCompact.Dir dir2,
      int n2,
      TextureAtlasSprite[] textureAtlasSprites,
      int n3,
      BakedQuad bakedQuad,
      RenderEnv renderEnv
   ) {
      BakedQuad quadCompact = getQuadCompact(textureAtlasSprites[n], dir, n3, bakedQuad, renderEnv);
      BakedQuad quadCompact2 = getQuadCompact(textureAtlasSprites[n2], dir2, n3, bakedQuad, renderEnv);
      return renderEnv.getArrayQuadsCtm(quadCompact, quadCompact2);
   }

   public static BakedQuad[] getQuadsCompact(
      ConnectedTexturesCompact.Dir dir,
      int n,
      ConnectedTexturesCompact.Dir dir2,
      int n2,
      ConnectedTexturesCompact.Dir dir3,
      int n3,
      TextureAtlasSprite[] textureAtlasSprites,
      int n4,
      BakedQuad bakedQuad,
      RenderEnv renderEnv
   ) {
      BakedQuad quadCompact = getQuadCompact(textureAtlasSprites[n], dir, n4, bakedQuad, renderEnv);
      BakedQuad quadCompact2 = getQuadCompact(textureAtlasSprites[n2], dir2, n4, bakedQuad, renderEnv);
      BakedQuad quadCompact3 = getQuadCompact(textureAtlasSprites[n3], dir3, n4, bakedQuad, renderEnv);
      return renderEnv.getArrayQuadsCtm(quadCompact, quadCompact2, quadCompact3);
   }

   public static BakedQuad[] getQuadsCompact(
      ConnectedTexturesCompact.Dir dir,
      int n,
      ConnectedTexturesCompact.Dir dir2,
      int n2,
      ConnectedTexturesCompact.Dir dir3,
      int n3,
      ConnectedTexturesCompact.Dir dir4,
      int n4,
      TextureAtlasSprite[] textureAtlasSprites,
      int n5,
      BakedQuad bakedQuad,
      RenderEnv renderEnv
   ) {
      BakedQuad quadCompact = getQuadCompact(textureAtlasSprites[n], dir, n5, bakedQuad, renderEnv);
      BakedQuad quadCompact2 = getQuadCompact(textureAtlasSprites[n2], dir2, n5, bakedQuad, renderEnv);
      BakedQuad quadCompact3 = getQuadCompact(textureAtlasSprites[n3], dir3, n5, bakedQuad, renderEnv);
      BakedQuad quadCompact4 = getQuadCompact(textureAtlasSprites[n4], dir4, n5, bakedQuad, renderEnv);
      return renderEnv.getArrayQuadsCtm(quadCompact, quadCompact2, quadCompact3, quadCompact4);
   }

   public static BakedQuad getQuadCompact(TextureAtlasSprite textureAtlasSprite, ConnectedTexturesCompact.Dir dir, int n, BakedQuad bakedQuad, RenderEnv renderEnv) {
      switch (dir) {
         case UP:
            return getQuadCompact(textureAtlasSprite, dir, 0, 0, 16, 8, n, bakedQuad, renderEnv);
         case UP_RIGHT:
            return getQuadCompact(textureAtlasSprite, dir, 8, 0, 16, 8, n, bakedQuad, renderEnv);
         case RIGHT:
            return getQuadCompact(textureAtlasSprite, dir, 8, 0, 16, 16, n, bakedQuad, renderEnv);
         case DOWN_RIGHT:
            return getQuadCompact(textureAtlasSprite, dir, 8, 8, 16, 16, n, bakedQuad, renderEnv);
         case DOWN:
            return getQuadCompact(textureAtlasSprite, dir, 0, 8, 16, 16, n, bakedQuad, renderEnv);
         case DOWN_LEFT:
            return getQuadCompact(textureAtlasSprite, dir, 0, 8, 8, 16, n, bakedQuad, renderEnv);
         case LEFT:
            return getQuadCompact(textureAtlasSprite, dir, 0, 0, 8, 16, n, bakedQuad, renderEnv);
         case UP_LEFT:
            return getQuadCompact(textureAtlasSprite, dir, 0, 0, 8, 8, n, bakedQuad, renderEnv);
         default:
            return bakedQuad;
      }
   }

   public static BakedQuad getQuadCompact(
      TextureAtlasSprite textureAtlasSprite, ConnectedTexturesCompact.Dir dir, int n, int n2, int n3, int n4, int n5, BakedQuad bakedQuad, RenderEnv renderEnv
   ) {
      Map[][] spriteQuadCompactMaps = ConnectedTextures.getSpriteQuadCompactMaps();
      if (spriteQuadCompactMaps == null) {
         return bakedQuad;
      }

      int indexInMap = textureAtlasSprite.getIndexInMap();
      if (indexInMap >= 0 && indexInMap < spriteQuadCompactMaps.length) {
         Map[] maps = spriteQuadCompactMaps[indexInMap];
         if (maps == null) {
            maps = new Map[ConnectedTexturesCompact.Dir.VALUES.length];
            spriteQuadCompactMaps[indexInMap] = maps;
         }

         Map map = maps[dir.ordinal()];
         if (map == null) {
            map = new IdentityHashMap(1);
            maps[dir.ordinal()] = map;
         }

         BakedQuad bakedQuad2 = (BakedQuad)map.get(bakedQuad);
         if (bakedQuad2 == null) {
            bakedQuad2 = makeSpriteQuadCompact(bakedQuad, textureAtlasSprite, n5, n, n2, n3, n4);
            map.put(bakedQuad, bakedQuad2);
         }

         return bakedQuad2;
      } else {
         return bakedQuad;
      }
   }

   public static BakedQuad makeSpriteQuadCompact(BakedQuad bakedQuad, TextureAtlasSprite textureAtlasSprite, int n, int n2, int n3, int n4, int n5) {
      int[] ints = (int[])bakedQuad.getVertexData().clone();
      TextureAtlasSprite sprite = bakedQuad.getSprite();

      for (int i = 0; i < 4; i++) {
         fixVertexCompact(ints, i, sprite, textureAtlasSprite, n, n2, n3, n4, n5);
      }

      return new BakedQuad(ints, bakedQuad.getTintIndex(), bakedQuad.getFace(), textureAtlasSprite);
   }

   public static void fixVertexCompact(int[] ints, int n, TextureAtlasSprite textureAtlasSprite, TextureAtlasSprite textureAtlasSprite2, int n2, int n3, int n4, int n5, int n6) {
      int n7 = ints.length / 4;
      int n8 = n7 * n;
      float intBitsToFloat = Float.intBitsToFloat(ints[n8 + 4]);
      float intBitsToFloat2 = Float.intBitsToFloat(ints[n8 + 4 + 1]);
      double spriteU16 = textureAtlasSprite.getSpriteU16(intBitsToFloat);
      double spriteV16 = textureAtlasSprite.getSpriteV16(intBitsToFloat2);
      float intBitsToFloat3 = Float.intBitsToFloat(ints[n8 + 0]);
      float intBitsToFloat4 = Float.intBitsToFloat(ints[n8 + 1]);
      float intBitsToFloat5 = Float.intBitsToFloat(ints[n8 + 2]);
      float f;
      float f2;
      switch (n2) {
         case 0:
            f = intBitsToFloat3;
            f2 = 1.0F - intBitsToFloat5;
            break;
         case 1:
            f = intBitsToFloat3;
            f2 = intBitsToFloat5;
            break;
         case 2:
            f = 1.0F - intBitsToFloat3;
            f2 = 1.0F - intBitsToFloat4;
            break;
         case 3:
            f = intBitsToFloat3;
            f2 = 1.0F - intBitsToFloat4;
            break;
         case 4:
            f = intBitsToFloat5;
            f2 = 1.0F - intBitsToFloat4;
            break;
         case 5:
            f = 1.0F - intBitsToFloat5;
            f2 = 1.0F - intBitsToFloat4;
            break;
         default:
            return;
      }

      float f3 = 15.968F;
      float f4 = 15.968F;
      if (spriteU16 < n3) {
         f = (float)(f + (n3 - spriteU16) / f3);
         spriteU16 = n3;
      }

      if (spriteU16 > n5) {
         f = (float)(f - (spriteU16 - n5) / f3);
         spriteU16 = n5;
      }

      if (spriteV16 < n4) {
         f2 = (float)(f2 + (n4 - spriteV16) / f4);
         spriteV16 = n4;
      }

      if (spriteV16 > n6) {
         f2 = (float)(f2 - (spriteV16 - n6) / f4);
         spriteV16 = n6;
      }

      switch (n2) {
         case 0:
            intBitsToFloat3 = f;
            intBitsToFloat5 = 1.0F - f2;
            break;
         case 1:
            intBitsToFloat3 = f;
            intBitsToFloat5 = f2;
            break;
         case 2:
            intBitsToFloat3 = 1.0F - f;
            intBitsToFloat4 = 1.0F - f2;
            break;
         case 3:
            intBitsToFloat3 = f;
            intBitsToFloat4 = 1.0F - f2;
            break;
         case 4:
            intBitsToFloat5 = f;
            intBitsToFloat4 = 1.0F - f2;
            break;
         case 5:
            intBitsToFloat5 = 1.0F - f;
            intBitsToFloat4 = 1.0F - f2;
            break;
         default:
            return;
      }

      ints[n8 + 4] = Float.floatToRawIntBits(textureAtlasSprite2.getInterpolatedU(spriteU16));
      ints[n8 + 4 + 1] = Float.floatToRawIntBits(textureAtlasSprite2.getInterpolatedV(spriteV16));
      ints[n8 + 0] = Float.floatToRawIntBits(intBitsToFloat3);
      ints[n8 + 1] = Float.floatToRawIntBits(intBitsToFloat4);
      ints[n8 + 2] = Float.floatToRawIntBits(intBitsToFloat5);
   }

   private enum Dir {
      UP,
      UP_RIGHT,
      RIGHT,
      DOWN_RIGHT,
      DOWN,
      DOWN_LEFT,
      LEFT,
      UP_LEFT;

      public static ConnectedTexturesCompact.Dir[] VALUES = values();
   }
}
